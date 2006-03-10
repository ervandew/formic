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
package org.formic.ant;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.apache.tools.ant.taskdefs.Property;

import org.formic.Installer;

import org.formic.ant.type.Path;

/**
 * Ant task that initializes the installer.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class InstallerTask
  extends Task
{
  private String property;
  private String resources;
  private String swingSteps;
  private String consoleSteps;
  private List paths = new ArrayList();
  private Properties properties = new Properties();

  /**
   * Executes this task.
   */
  public void execute ()
    throws BuildException
  {
    Installer.setProject(getProject());
    Installer.setResourceBundle(ResourceBundle.getBundle(resources));
    Installer.setConsoleMode(Boolean.parseBoolean(
          getProject().getProperty(Installer.CONSOLE_MODE)));
    Installer.initProperties(getProperties());

    if(!Installer.isConsoleMode()){
      Installer.loadStepNames(
          "/org/formic/swing/wizard/step/steps.properties");
      if(swingSteps != null){
        Installer.loadStepNames(swingSteps);
      }
    }else{
      Installer.loadStepNames(
          "/org/formic/console/wizard/step/steps.properties");
      if(consoleSteps != null){
        Installer.loadStepNames(consoleSteps);
      }
    }

    boolean completed = Installer.run(paths);
    if(property != null && completed){
      getProject().setProperty(property, String.valueOf(completed));
    }
  }

  /**
   * Sets the property that will be set upon successful completion of the
   * install wizard.
   *
   * @param property The property name.
   */
  public void setProperty (String property)
  {
    this.property = property;
  }

  /**
   * Sets the resources to use during the installation process.
   *
   * @param resources The resources.
   */
  public void setResources (String resources)
  {
    this.resources = resources;
  }

  /**
   * Sets the classpath resource containing the mappings for swing step names to
   * swing step classes.
   *
   * @param swingSteps The swingSteps.
   */
  public void setSwingSteps (String swingSteps)
  {
    this.swingSteps = swingSteps;
  }

  /**
   * Sets the classpath resource containing the mappings for console step names
   * to console step classes.
   *
   * @param consoleSteps The consoleSteps.
   */
  public void setConsoleSteps (String consoleSteps)
  {
    this.consoleSteps = consoleSteps;
  }

  /**
   * Adds the configured Path instance.
   *
   * @param _path The path.
   */
  public void addConfiguredPath (Path _path)
  {
    paths.add(_path);
  }

  /**
   * Adds the configured Property instance.
   *
   * @param _property The property.
   */
  public void addConfiguredProperty (Property _property)
  {
    properties.setProperty(_property.getName(), _property.getValue());
  }

  /**
   * Gets this tasks configured properties.
   *
   * @return The Properties.
   */
  public Properties getProperties ()
  {
    return properties;
  }
}
