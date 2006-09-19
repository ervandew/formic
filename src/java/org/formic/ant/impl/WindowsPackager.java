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

import java.lang.reflect.Method;

import org.apache.commons.io.FilenameUtils;

import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Task;

import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Taskdef;
import org.apache.tools.ant.taskdefs.Zip;

import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.ZipFileSet;

import org.formic.ant.util.AntUtils;

/**
 * Implementation of {@link org.formic.ant.Packager} for unix based operating
 * systems.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class WindowsPackager
  extends AbstractPackager
{
  private Zip zip;

  /**
   * {@inheritDoc}
   * @see org.formic.ant.Packager#execute()
   */
  public void execute ()
    throws Exception
  {
    getProject().log("Building Windows installer...");

    File zipFile = new File(getBuildDir() + "/formic.zip");

    FileSet files = new FileSet();
    files.setDir(new File(getFormicHome()));
    files.setIncludes("ant/**/*");
    files.setExcludes("ant/resources/**/*");
    files.setExcludes("ant/lib/native/linux");
    files.setExcludes("ant/lib/native/linux/*");
    zip.addFileset(files);

    FileSet formicFiles = new FileSet();
    formicFiles.setDir(new File(getFormicHome() + "/ant/resources"));
    formicFiles.setIncludes("formic.bat");
    zip.addFileset(formicFiles);

    zip.setDestFile(zipFile);
    zip.setTaskName(getTaskName());
    zip.setProject(getProject());
    zip.execute();

    buildWindowsExecutable(zipFile);
    getProject().log("");
  }

  /**
   * Builds an executable for Windows.
   */
  private void buildWindowsExecutable (File archive)
    throws Exception
  {
    // construct bootstrap jar
    File jar = constructBoostrapJar(archive);

    // generate config
    File config = generateLaunch4jConfig("install");

    // execute launch4j
    executeLaunch4j(config, jar);
  }

  /**
   * Constructs a boostrap jar file for the windows installer.
   *
   * @param archive The archive containing the installer files.
   * @return The bootstrap file.
   */
  private File constructBoostrapJar (File archive)
  {
    File bootstrap = new File(getBuildDir() + "/formic-boostrap.jar");

    AntUtils.copy(getProject(), getTaskName(),
        new File(getFormicHome() + "/ant/resources/formic-bootstrap.jar"),
        bootstrap);

    ZipFileSet fileset = new ZipFileSet();
    fileset.setDir(new File(FilenameUtils.getFullPath(archive.getAbsolutePath())));
    fileset.createInclude().setName(FilenameUtils.getName(archive.getPath()));

    Jar jar = new Jar();
    jar.setTaskName(getTaskName());
    jar.setProject(getProject());
    jar.setDestFile(bootstrap);
    jar.setUpdate(true);
    jar.addZipfileset(fileset);
    jar.execute();

    return bootstrap;
  }

  /**
   * Generates a launch4j config file for building the executable.
   *
   * @return The generated config file.
   */
  private File generateLaunch4jConfig (String action)
  {
    File launch4jConfig = new File(getBuildDir() + "/launch4j.config.xml");

    AntUtils.copy(getProject(), getTaskName(),
        new File(getFormicHome() + "/ant/resources/launch4j.config.xml"),
        launch4jConfig);

    AntUtils.replace(getProject(), launch4jConfig, "${formic.action}", action);

    return launch4jConfig;
  }

  /**
   * Executes the launch4j task to generate the windows executable.
   *
   * @param config The launch4j config file to use.
   * @param jar The jar file to be executed.
   */
  private void executeLaunch4j (File config, File jar)
    throws Exception
  {
    // set location of launch4j distribution
    AntUtils.property(getProject(), "launch4j.dir",
        new File(getFormicHome() + "/ant/resources/launch4j"));

    // define launch4j task
    Path classpath = new Path(getProject());
    classpath.createPathElement().setPath(
        getProject().getProperty("launch4j.dir") + "/launch4j.jar");
    classpath.createPathElement().setPath(
        getProject().getProperty("launch4j.dir") + "/lib/xstream.jar");

    Taskdef task = new Taskdef();
    task.setProject(getProject());
    task.setName("launch4j");
    task.setClassname("net.sf.launch4j.ant.Launch4jTask");
    task.setClasspath(classpath);
    task.execute();

    // execute launch4j task
    Task launch4j = (Task)ComponentHelper.getComponentHelper(getProject())
      .getComponentClass("launch4j").newInstance();
    Method setConfigFile = launch4j.getClass().getDeclaredMethod(
        "setConfigFile", new Class[]{File.class});
    setConfigFile.invoke(launch4j, new Object[]{config});

    Method setJar = launch4j.getClass().getDeclaredMethod(
        "setJar", new Class[]{File.class});
    setJar.invoke(launch4j, new Object[]{jar});

    Method setOutfile = launch4j.getClass().getDeclaredMethod(
        "setOutfile", new Class[]{File.class});
    setOutfile.invoke(launch4j, new Object[]{getDestFile()});

    launch4j.execute();
  }

  /**
   * Sets the zip for this instance.
   *
   * @param zip The zip.
   */
  public void setZip (Zip zip)
  {
    this.zip = zip;
  }
}
