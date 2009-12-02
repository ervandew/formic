/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008  Eric Van Dewoestine
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.formic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import java.net.URL;

import java.text.MessageFormat;

import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;

import org.apache.commons.lang.StringUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import org.formic.ant.util.AntUtils;

import org.formic.util.ResourceBundleAggregate;

import org.formic.util.dialog.gui.GuiDialogs;

import org.formic.wizard.Wizard;
import org.formic.wizard.WizardBuilder;

import org.pietschy.wizard.I18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Installer class that holds installer resources, etc.
 *
 * @author Eric Van Dewoestine
 */
public class Installer
{
  private static final Logger logger = LoggerFactory.getLogger(Installer.class);

  private static ResourceBundleAggregate resourceBundle;
  private static Project project;
  private static Dimension dimension;
  private static Image image;
  private static boolean consoleMode;

  private static InstallContext context = new InstallContext();

  private static boolean envInitialized;

  /**
   * Runs the installer.
   *
   * @param properties Installer properties (height, width, etc.)
   * @param paths List of wizard paths.
   *
   * @return true if the installation completed successfully, false if the user
   * canceled or the installation was aborted.
   */
  public static boolean run(
      Properties properties, List paths)
  {
    logger.info("Running Installer.");
    consoleMode = "true".equalsIgnoreCase(
        properties.getProperty("formic.console"));
    if(consoleMode &&
        !"true".equalsIgnoreCase(Installer.getStringOrDefault("console.support", "true")))
    {
      logger.error(Installer.getString("console.not.supported"));
      return false;
    }

    if(!consoleMode){
      I18n.setBundle(getResourceBundle());
      GuiDialogs.setBundle(getResourceBundle());

      setLookAndFeel();

      String imagePath = getString("wizard.icon", "/images/16x16/wizard.png");
      if(imagePath != null){
        image = getImage(imagePath);
      }
    }

    dimension = new Dimension(
      Integer.parseInt(getString("wizard.width", "600")),
      Integer.parseInt(getString("wizard.height", "400")));

    Wizard wizard = WizardBuilder.build(paths, consoleMode);
    wizard.showWizard(properties.getProperty("formic.action"));
    wizard.waitFor();

    logger.info("Installer Finished.");

    return !wizard.wasCanceled();
  }

  /**
   * Gets the install context.
   *
   * @return The InstallContext.
   */
  public static InstallContext getContext()
  {
    return context;
  }

  /**
   * Gets the ResourceBundle to use during the installation.
   *
   * @return The resourceBundle.
   */
  public static ResourceBundle getResourceBundle()
  {
    return resourceBundle;
  }

  /**
   * Sets the ResourceBundle to use during the installation.
   *
   * @param bundle The resourceBundle.
   */
  public static void setResourceBundle(ResourceBundle bundle)
  {
    if(resourceBundle != null){
      throw new IllegalStateException(getString("resource.already.loaded"));
    }

    resourceBundle = new ResourceBundleAggregate();
    resourceBundle.addBundle(bundle);
    resourceBundle.addBundle(ResourceBundle.getBundle("org/formic/messages"));
    resourceBundle.addBundle(ResourceBundle.getBundle("org/formic/wizard/install"));
  }

  /**
   * Gets the value for the supplied resource key.
   *
   * @param key The key.
   * @return The value or null if not found.
   */
  public static String getString(String key)
  {
    try{
      return key != null ? getResourceBundle().getString(key) : null;
    }catch(MissingResourceException mre){
      return null;
    }
  }

  /**
   * Gets the value for the supplied resource key.
   *
   * @param key The key.
   * @param dflt The value to return if no value found for the specified
   * key.
   * @return The value or the supplied default.
   */
  public static String getStringOrDefault(String key, String dflt)
  {
    try{
      return key != null ? getResourceBundle().getString(key) : dflt;
    }catch(MissingResourceException mre){
      return dflt;
    }
  }

  /**
   * Gets the value for the supplied resource key and formats the result using
   * the supplied argument.
   *
   * @param key The key.
   * @param arg The value to format the result with.
   * @return The value or null if not found.
   */
  public static String getString(String key, Object arg)
  {
    return getString(key, new Object[]{arg});
  }

  /**
   * Gets the value for the supplied resource key and formats the result using
   * the supplied arguments.
   *
   * @param key The key.
   * @param arg1 The first value to format the result with.
   * @param arg2 The second value to format the result with.
   * @return The value or null if not found.
   */
  public static String getString(String key, Object arg1, Object arg2)
  {
    return getString(key, new Object[]{arg1, arg2});
  }

  /**
   * Gets the value for the supplied resource key and formats the result using
   * the supplied arguments.
   *
   * @param key The key.
   * @param args The values to format the result with.
   * @return The value or null if not found.
   */
  public static String getString(String key, Object[] args)
  {
    String message = getString(key);
    return MessageFormat.format(message != null ? message : key, args);
  }

  /**
   * Gets an image given either the image resource path or a key to lookup the
   * resource path.
   *
   * @param image The path or key.
   * @return The image or null if not found.
   */
  public static Image getImage(String image)
  {
    String path = getString(image);
    if(path == null){
      path = image;
    }

    URL url = Installer.class.getResource(path);
    return url != null ? Toolkit.getDefaultToolkit().createImage(url) : null;
  }

  /**
   * Gets a color given a resource key.
   *
   * @param color The key.
   * @return The color or null if not found.
   */
  public static Color getColor(String color)
  {
    String value = getString(color);
    if(color == null){
      return null;
    }

    String[] rgb = StringUtils.split(value, ',');
    int red = Integer.parseInt(rgb[0]);
    int green = Integer.parseInt(rgb[1]);
    int blue = Integer.parseInt(rgb[2]);

    return new Color(red, green, blue);
  }

  /**
   * Sets the look and feel.
   */
  private static void setLookAndFeel()
  {
    try {
      String laf = getString("wizard.laf",
          "com.jgoodies.looks.plastic.Plastic3DLookAndFeel");

      if(laf != null){
        // plastic settings
        if(laf.startsWith("com.jgoodies.looks.plastic")){
          String theme = getString("wizard.theme");
          if(theme != null){
            PlasticLookAndFeel.setPlasticTheme(
                (PlasticTheme)Class.forName(theme).newInstance());
          }
        }

        UIManager.setLookAndFeel(laf);
      }
    } catch (Exception e) {
      throw new BuildException(e);
    }
  }

  /**
   * Gets the ant project this installer is running under.
   *
   * @return The ant project.
   */
  public static Project getProject()
  {
    return project;
  }

  /**
   * Sets the ant project this installer is running under.
   *
   * @param prjct The ant project.
   */
  public static void setProject(Project prjct)
  {
    project = prjct;
    Log.setProject(project);
  }

  /**
   * Gets the dimension to use for the installer window (gui only).
   *
   * @return The dimension of the installer window.
   */
  public static Dimension getDimension()
  {
    return dimension;
  }

  /**
   * Gets the wizard image (for frame icon, etc).
   *
   * @return The wizard image or null if none.
   */
  public static Image getImage()
  {
    return image;
  }

  /**
   * Determines if the installer is running in console mode or not.
   *
   * @return true if in console mode, false otherwise.
   */
  public static boolean isConsoleMode()
  {
    return consoleMode;
  }

  /**
   * Gets the value of an environment variable.
   *
   * @param name The name of the environment variable to get.
   * @return The value of the environment variable, or null if not found.
   */
  public synchronized static String getEnvironmentVariable(String name)
  {
    if(!envInitialized){
      envInitialized = true;
      AntUtils.property(getProject(), "env");
    }

    String key = "${env." + name + '}';
    String value = getProject().replaceProperties(key);

    return key.equals(value) ? null : value;
  }
}
