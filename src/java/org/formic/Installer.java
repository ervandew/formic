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

import java.io.IOException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import org.formic.swing.wizard.Wizard;
import org.formic.swing.wizard.WizardBuilder;

import org.formic.util.ResourceBundleAggregate;

/**
 * Installer class that hold installer resources, etc.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class Installer
{
  public static String CONSOLE_MODE = "formic.console";

  private static ResourceBundleAggregate resourceBundle;
  private static boolean consoleMode = Boolean.parseBoolean(
      System.getProperty(CONSOLE_MODE));
  private static Properties steps = new Properties();
  private static Map properties = new HashMap();
  private static Project project;

  /**
   * Runs the installer.
   *
   * @param _paths List of wizard paths.
   *
   * @return true if the installation completed successfully, false if the user
   * canceled or the installation was aborted.
   */
  public static boolean run (List _paths)
  {
    if(!Installer.isConsoleMode()){
      return runSwingInstaller(_paths);
    }else{
      throw new UnsupportedOperationException(getString("console.not.supported"));
    }
  }

  /**
   * Runs a swing based installer.
   *
   * @param _paths The installer paths.
   *
   * @return true if the installation completed successfully, false if the user
   * canceled or the installation was aborted.
   */
  public static boolean runSwingInstaller (List _paths)
  {
    setLookAndFeel();

    Wizard wizard = WizardBuilder.build(_paths);
    wizard.showInFrame("Installer Wizard");
    wizard.waitFor();

    return !wizard.wasCanceled();
  }

  /**
   * Sets the initial properties for the wizard.
   *
   * @param _properties
   */
  public static void initProperties (Map _properties)
  {
    properties = _properties;
  }

  /**
   * Gets the installer properties.
   *
   * @return Map of properties.
   */
  public static Map getProperties ()
  {
    return properties;
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
   * Determines if the installer is running in console mode.
   *
   * @return true if in console mode, false otherwise.
   */
  public static boolean isConsoleMode ()
  {
    return consoleMode;
  }

  /**
   * Specifies whether the installer is running in console mode.
   *
   * @param _consoleMode true if in console mode, false otherwise.
   */
  public static void setConsoleMode (boolean _consoleMode)
  {
    consoleMode = _consoleMode;
  }

  /**
   * Loads step name to step class mappings from the supplied resource.
   *
   * @param _resource The resource.
   */
  public static void loadStepNames (String _resource)
  {
    try{
      steps.load(Installer.class.getResourceAsStream(_resource));
    }catch(NullPointerException npe){
      throw new RuntimeException(getString("resource.not.found", _resource));
    }catch(IOException ioe){
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Gets an instance of the step with the supplied name.
   *
   * @param _name The step name.
   * @param _properties The step properties.
   * @return The step.
   */
  public static Object getStep (String _name, Properties _properties)
  {
    try{
      String classname = steps.getProperty(_name);
      if(classname == null){
        throw new RuntimeException(getString("step.not.found", _name));
      }
      Constructor constructor =
        Class.forName(classname).getConstructor(
            new Class[]{String.class, Properties.class});
      return constructor.newInstance(new Object[]{_name, _properties});
    }catch(InvocationTargetException ite){
      Throwable target = ite.getTargetException();
      if(target instanceof IllegalArgumentException){
        throw (IllegalArgumentException)target;
      }
      throw new RuntimeException(target);
    }catch(RuntimeException re){
      throw re;
    }catch(Exception e){
      throw new RuntimeException(getString("step.error.loading", _name), e);
    }
  }

  /**
   * Sets the look and feel.
   */
  private static void setLookAndFeel ()
  {
    try {
      String laf = (String)properties.get("wizard.laf");

      if(laf != null){
        // plastic settings
        if(laf.startsWith("com.jgoodies.looks.plastic")){
          String theme = (String)properties.get("wizard.theme");

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
  }

  /**
   * Logs a debug message.
   *
   * @param _message The message.
   */
  public static void debug (String _message)
  {
    project.log(_message, Project.MSG_DEBUG);
  }

  /**
   * Logs an info message.
   *
   * @param _message The message.
   */
  public static void info (String _message)
  {
    project.log(_message, Project.MSG_INFO);
  }

  /**
   * Logs a warn message.
   *
   * @param _message The message.
   */
  public static void warn (String _message)
  {
    project.log(_message, Project.MSG_WARN);
  }

  /**
   * Logs an error message.
   *
   * @param _message The message.
   */
  public static void error (String _message)
  {
    project.log(_message, Project.MSG_ERR);
  }
}
