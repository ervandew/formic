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

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.formic.Installer;

/**
 * Ant task which creates a temp directory for use in the install task where
 * temporary installer files can be written and will be removed when the
 * installer exits.
 *
 * @author Eric Van Dewoestine
 */
public class TempDirTask
  extends Task
{
  private String property;

  /**
   * Executes this task.
   */
  public void execute()
    throws BuildException
  {
    File tempDir = Installer.tempDir("");
    getProject().setProperty(property, tempDir.getAbsolutePath());
  }

  /**
   * Sets the property that will be set to the absolute path of the temp
   * directory.
   *
   * @param property The property name.
   */
  public void setProperty(String property)
  {
    this.property = property;
  }
}
