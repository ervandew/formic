/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008  Eric Van Dewoestine
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

import org.apache.tools.ant.taskdefs.Concat;
import org.apache.tools.ant.taskdefs.Tar;

import org.apache.tools.ant.types.Path.PathElement;

import org.formic.ant.util.AntUtils;

/**
 * Implementation of {@link org.formic.ant.Packager} for windows based operating
 * systems.
 *
 * @author Eric Van Dewoestine
 */
public class UnixPackager
  extends AbstractPackager
{
  private Tar tar;

  /**
   * {@inheritDoc}
   * @see org.formic.ant.Packager#execute()
   */
  public void execute()
    throws Exception
  {
    getProject().log("Building *nix installer...");

    File tarFile = new File(getBuildDir() + "/formic.tar.gz");
    File formicHome = new File(getFormicHome());

    Tar.TarFileSet files = tar.createTarFileSet();
    files.setDir(formicHome);
    files.setIncludes("ant/**/*");
    files.setExcludes("ant/bin/**/*");
    files.setExcludes("ant/resources/**/*");
    files.setExcludes("ant/lib/jregistrykey*.jar");
    files.setExcludes("ant/lib/native/windows");
    files.setExcludes("ant/lib/native/windows/*");

    Tar.TarFileSet binFiles = tar.createTarFileSet();
    binFiles.setDir(formicHome);
    binFiles.setMode("755");
    binFiles.setIncludes("ant/bin/ant");

    Tar.TarFileSet formicFiles = tar.createTarFileSet();
    formicFiles.setDir(new File(getFormicHome() + "/ant/resources"));
    formicFiles.setMode("755");
    formicFiles.setIncludes("formic");

    tar.setDestFile(tarFile);
    tar.setTaskName(getTaskName());
    tar.setProject(getProject());
    tar.execute();

    buildSelfExtractingShellScript(tarFile);
    getProject().log("");
  }

  /**
   * Builds a self extracting shell script for *nix operating systems.
   */
  private void buildSelfExtractingShellScript(File archive)
  {
    File selfextract = new File(getBuildDir() + "/selfextract");

    AntUtils.copy(getProject(), getTaskName(),
        new File(getFormicHome() + "/ant/resources/selfextract"), selfextract);

    AntUtils.replace(
        getProject(), selfextract, "${formic.action}", INSTALL_ACTION);

    Concat concat = new Concat();
    concat.setTaskName(getTaskName());
    concat.setProject(getProject());
    concat.setDestfile(getDestFile());
    concat.setBinary(true);

    PathElement script = concat.createPath().createPathElement();
    script.setLocation(selfextract);

    PathElement tarFile = concat.createPath().createPathElement();
    tarFile.setLocation(archive);

    concat.execute();

    // set the file as executable
    AntUtils.chmod(getProject(), getDestFile(), "755");
  }

  /**
   * Sets the tar for this instance.
   *
   * @param tar The tar.
   */
  public void setTar(Tar tar)
  {
    this.tar = tar;
  }
}
