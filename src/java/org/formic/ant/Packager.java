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
package org.formic.ant;

import java.io.File;

import org.apache.tools.ant.Project;

/**
 * Interface representing a platform specific packager of installers.
 *
 * @author Eric Van Dewoestine
 */
public interface Packager
{
  public static final String INSTALL_ACTION = "installer";
  public static final String UNINSTALL_ACTION = "uninstaller";

  /**
   * Executes this packager.
   */
  public void execute()
    throws Exception;

  /**
   * Sets the current ant project.
   *
   * @param project The ant project.
   */
  public void setProject(Project project);

  /**
   * Sets the name of the task currently executing.
   *
   * @param name The task name.
   */
  public void setTaskName(String name);

  /**
   * Sets the path to the local formic distribution.
   *
   * @param home The path.
   */
  public void setFormicHome(String home);

  /**
   * Sets the path to the build directory.
   *
   * @param dir The build path.
   */
  public void setBuildDir(String dir);

  /**
   * Sets the destination file.
   *
   * @param destFile The destination file.
   */
  public void setDestFile(File destFile);
}
