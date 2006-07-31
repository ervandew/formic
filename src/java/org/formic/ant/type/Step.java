/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2006  Eric Van Dewoestine
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
package org.formic.ant.type;

import java.util.Properties;

import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Typedef;

import org.formic.Installer;

/**
 * Defines a step in the installation process.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class Step
  extends Typedef
{
  private String name;
  private Properties properties = new Properties();

  /**
   * Gets the name of the step.
   *
   * @return The step name.
   */
  public String getName ()
  {
    return this.name;
  }

  /**
   * Sets the name of the step.
   *
   * @param name The step name.
   */
  public void setName (String name)
  {
    this.name = name;
  }

  /**
   * Gets the description for this step.
   *
   * @return The step description.
   */
  public String getDescription ()
  {
    return Installer.getString(getName() + ".description");
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
