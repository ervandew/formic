/**
 * Formic installer framework.
 * Copyright (C) 2004 - 2006  Eric Van Dewoestine
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

import java.awt.Dimension;

import java.text.MessageFormat;

import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import org.formic.util.ResourceBundleAggregate;

import org.formic.wizard.Wizard;
import org.formic.wizard.WizardBuilder;

/**
 * Installer class that holds installer resources, etc.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class Installer
{
  private static ResourceBundleAggregate resourceBundle;
  private static Project project;
  private static Dimension dimension;

  /**
   * Runs the installer.
   *
   * @param _properties Installer properties (height, width, etc.)
   * @param _paths List of wizard paths.
   * @param _consoleMode true if running in console mode, false otherwise.
   *
   * @return true if the installation completed successfully, false if the user
   * canceled or the installation was aborted.
   */
  public static boolean run (
      Properties _properties, List _paths, boolean _consoleMode)
  {
    if(!_consoleMode){
      setLookAndFeel(_properties);

      dimension = new Dimension(
        Integer.parseInt(_properties.getProperty("wizard.width")),
        Integer.parseInt(_properties.getProperty("wizard.height")));
    }

    Wizard wizard = WizardBuilder.build(_paths, _consoleMode);
    wizard.show();
    wizard.waitFor();

    return !wizard.wasCanceled();
  }

  /**
   * Gets the ResourceBundle to use during the installation.
   *
   * @return The resourceBundle.
   */
  public static ResourceBundle getResourceBundle ()
  {
    return resourceBundle;
  }

  /**
   * Sets the ResourceBundle to use during the installation.
   *
   * @param _resourceBundle The resourceBundle.
   */
  public static void setResourceBundle (ResourceBundle _resourceBundle)
  {
    if(resourceBundle != null){
      throw new IllegalStateException(getString("resource.already.loaded"));
    }

    resourceBundle = new ResourceBundleAggregate();
    resourceBundle.addBundle(_resourceBundle);
    resourceBundle.addBundle(ResourceBundle.getBundle("org/formic/messages"));
  }

  /**
   * Gets the value for the supplied resource key.
   *
   * @param _key The key.
   * @return The value or null if not found.
   */
  public static String getString (String _key)
  {
    try{
      return getResourceBundle().getString(_key);
    }catch(MissingResourceException mre){
      return null;
    }
  }

  /**
   * Gets the value for the supplied resource key and formats the result using
   * the supplied argument.
   *
   * @param _key The key.
   * @param _arg The value to format the result with.
   * @return The value or null if not found.
   */
  public static String getString (String _key, Object _arg)
  {
    return getString(_key, new Object[]{_arg});
  }

  /**
   * Gets the value for the supplied resource key and formats the result using
   * the supplied arguments.
   *
   * @param _key The key.
   * @param _arg1 The first value to format the result with.
   * @param _arg2 The second value to format the result with.
   * @return The value or null if not found.
   */
  public static String getString (String _key, Object _arg1, Object _arg2)
  {
    return getString(_key, new Object[]{_arg1, _arg2});
  }

  /**
   * Gets the value for the supplied resource key and formats the result using
   * the supplied arguments.
   *
   * @param _key The key.
   * @param _args The values to format the result with.
   * @return The value or null if not found.
   */
  public static String getString (String _key, Object[] _args)
  {
    String message = getString(_key);
    return MessageFormat.format(message, _args);
  }

  /**
   * Sets the look and feel.
   */
  private static void setLookAndFeel (Properties _properties)
  {
    try {
      String laf = _properties.getProperty("wizard.laf");

      if(laf != null){
        // plastic settings
        if(laf.startsWith("com.jgoodies.looks.plastic")){
          String theme = _properties.getProperty("wizard.theme");

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
   * Sets the ant project this installer is running under.
   *
   * @param _project The ant project.
   */
  public static void setProject (Project _project)
  {
    project = _project;
    Log.setProject(project);
  }

  /**
   * Gets the dimension to use for the installer window (gui only).
   *
   * @return The dimension of the installer window.
   */
  public static Dimension getDimension ()
  {
    return dimension;
  }
}
