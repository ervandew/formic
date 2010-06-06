/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2010  Eric Van Dewoestine
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

import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import org.formic.Installer;

/**
 * Ant task mimicing ant's property task but allowing a property file to be
 * loaded from the installer classpath.
 *
 * @author Eric Van Dewoestine
 */
public class Property
  extends Task
{
  private String resource;

  /**
   * Executes this task.
   */
  public void execute()
    throws BuildException
  {
    InputStream in = Installer.class.getResourceAsStream(resource);
    if (in == null){
      throw new BuildException("Resource not found: " + resource);
    }

    try{
      Project project = getProject();
      Properties properties = new Properties();
      properties.load(in);
      Enumeration keys = properties.keys();
      while (keys.hasMoreElements()){
        String key = (String)keys.nextElement();
        project.setProperty(key, properties.getProperty(key));
      }
    }catch(IOException ioe){
      throw new BuildException(ioe);
    }finally{
      IOUtils.closeQuietly(in);
    }
  }

  /**
   * Sets the property file resource to load.
   *
   * @param resource The resource.
   */
  public void setResource(String resource)
  {
    this.resource = resource;
  }
}
