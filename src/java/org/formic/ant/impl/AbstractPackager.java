/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2006  Eric Van Dewoestine
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.formic.ant.impl;

import java.io.File;

import org.apache.tools.ant.Project;

import org.formic.ant.Packager;

import org.formic.ant.util.AntUtils;

/**
 * Abstract base class consisting of shared logic for Packager implementations.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public abstract class AbstractPackager
  implements Packager
{
  private Project project;
  private String taskName;
  private String formicHome;
  private String buildDir;
  private File destFile;

  /**
   * Gets the project for this instance.
   *
   * @return The project.
   */
  public Project getProject ()
  {
    return this.project;
  }

  /**
   * {@inheritDoc}
   * @see Packager#setProject(Project)
   */
  public void setProject (Project project)
  {
    this.project = project;
  }

  /**
   * Gets the taskName for this instance.
   *
   * @return The taskName.
   */
  public String getTaskName ()
  {
    return this.taskName;
  }

  /**
   * {@inheritDoc}
   * @see Packager#setTaskName(String)
   */
  public void setTaskName (String taskName)
  {
    this.taskName = taskName;
  }

  /**
   * Gets the formicHome for this instance.
   *
   * @return The formicHome.
   */
  public String getFormicHome ()
  {
    return this.formicHome;
  }

  /**
   * {@inheritDoc}
   * @see Packager#setFormicHome(String)
   */
  public void setFormicHome (String formicHome)
  {
    this.formicHome = formicHome;
  }

  /**
   * Gets the buildDir for this instance.
   *
   * @return The buildDir.
   */
  public String getBuildDir ()
  {
    return this.buildDir;
  }

  /**
   * {@inheritDoc}
   * @see Packager#setBuildDir(String)
   */
  public void setBuildDir (String buildDir)
  {
    this.buildDir = buildDir;
    AntUtils.mkdir(getProject(), getTaskName(), new File(buildDir));
  }

  /**
   * Gets the destFile for this instance.
   *
   * @return The destFile.
   */
  public File getDestFile ()
  {
    return this.destFile;
  }

  /**
   * {@inheritDoc}
   * @see Packager#setDestFile(File)
   */
  public void setDestFile (File destFile)
  {
    this.destFile = destFile;
  }
}
