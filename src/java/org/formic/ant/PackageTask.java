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
package org.formic.ant;

import java.io.File;

import java.lang.reflect.Method;

import org.apache.commons.io.FilenameUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Task;

import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Concat;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Replace;
import org.apache.tools.ant.taskdefs.Tar;
import org.apache.tools.ant.taskdefs.Taskdef;
import org.apache.tools.ant.taskdefs.Zip;

import org.apache.tools.ant.types.FileSet;

import org.apache.tools.ant.types.Path.PathElement;

import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.ZipFileSet;

/**
 * Task for packaging the installer for distribution.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class PackageTask
  extends Task
{
  private static final String FORMIC_HOME_PROPERTY = "formic.home";
  private static final String FORMIC_BUILDDIR_PROPERTY = "formic.builddir";
  private static final String FORMIC_HOME_ENV = "env.FORMIC_HOME";
  private static final String DEFAULT_BUILDDIR = "build/formic";

  private static final String UNIX = "unix";
  private static final String LINUX = "linux";
  private static final String WINDOWS = "windows";

  private String os;
  private File destFile;
  private Tar tar;
  private Zip zip;

  private String formicHome;
  private String buildDir;

  /**
   * {@inheritDoc}
   * @see Task#init()
   */
  public void init ()
  {
    formicHome = determineFormicHome();
    buildDir = determineBuildDir();

    Mkdir mkdir = new Mkdir();
    mkdir.setProject(getProject());
    mkdir.setTaskName(getTaskName());
    mkdir.setDir(new File(buildDir));
    mkdir.execute();
  }

  /**
   * Executes this task.
   */
  public void execute ()
    throws BuildException
  {
    checkOs();
    check("destfile", destFile, "");

    String basedir = getProject().getProperty("basedir");
    try{
      if(os.equals(LINUX) || os.equals(UNIX)){
        log("Building *nix installer...");
        File tarFile = new File(basedir + "/" + buildDir + "/formic.tar.gz");

        Tar tar = getTar();

        Tar.TarFileSet files = tar.createTarFileSet();
        files.setDir(new File(formicHome));
        files.setIncludes("ant/**/*");
        files.setExcludes("ant/bin/**/*");
        files.setExcludes("ant/resources/**/*");

        Tar.TarFileSet binFiles = tar.createTarFileSet();
        binFiles.setDir(new File(formicHome));
        binFiles.setMode("755");
        binFiles.setIncludes("ant/bin/ant");

        Tar.TarFileSet formicFiles = tar.createTarFileSet();
        formicFiles.setDir(new File(formicHome + "/ant/resources"));
        formicFiles.setMode("755");
        formicFiles.setIncludes("formic");

        tar.setDestFile(tarFile);
        tar.setTaskName(getTaskName());
        tar.setProject(getProject());
        tar.execute();

        buildSelfExtractingShellScript(tarFile);
        log("");
      }else{
        log("Building Windows installer...");
        File zipFile = new File(basedir + "/" + buildDir + "/formic.zip");
        Zip zip = getZip();

        FileSet files = new FileSet();
        files.setDir(new File(formicHome));
        files.setIncludes("ant/**/*");
        files.setExcludes("ant/resources/**/*");
        files.setExcludes("ant/lib/native/**/*");
        zip.addFileset(files);

        FileSet formicFiles = new FileSet();
        formicFiles.setDir(new File(formicHome + "/ant/resources"));
        formicFiles.setIncludes("formic.bat");
        zip.addFileset(formicFiles);

        zip.setDestFile(zipFile);
        zip.setTaskName(getTaskName());
        zip.setProject(getProject());
        zip.execute();

        buildWindowsExecutable(zipFile);
        log("");
      }
    }catch(BuildException be){
      throw be;
    }catch(Exception e){
      throw new BuildException(e);
    }
  }

  /**
   * Sets the os for this instance.
   *
   * @param os The os.
   */
  public void setOs (String os)
  {
    this.os = os;
    checkOs();
  }

  /**
   * Sets the destFile for this instance.
   *
   * @param destFile The destFile.
   */
  public void setDestFile (File destFile)
  {
    this.destFile = destFile;
  }

  /**
   * Gets the tar instance for this task, creating it if necessary.
   *
   * @return The tar instance.
   */
  protected Tar getTar ()
  {
    if(tar == null){
      tar = new Tar();
    }
    return tar;
  }

  /**
   * Gets the zip instance for this task, creating it if necessary.
   *
   * @return The zip instance.
   */
  protected Zip getZip ()
  {
    if(zip == null){
      zip = new Zip();
    }
    return zip;
  }

  /**
   * Determines the location of the formic distribution based on the current
   * settings.
   *
   * @return The formic home directory.
   */
  private String determineFormicHome ()
    throws BuildException
  {
    // first try ant property.
    String home = getProject().getProperty(FORMIC_HOME_PROPERTY);

    // attempt to locate environment variable.
    if(home == null){
      Property property = new Property();
      property.setEnvironment("env");
      property.setProject(getProject());
      property.execute();

      home = getProject().getProperty(FORMIC_HOME_ENV);
    }

    if(home == null){
      throw new BuildException(
          "Unable to determine location of formic distribution:  " +
          "Property 'formic.home' not set. " +
          "Environment variable 'FORMIC_HOME' not found.");
    }

    return home;
  }

  /**
   * Determines the build directory.
   *
   * @return The build directory.
   */
  private String determineBuildDir ()
    throws BuildException
  {
    // first try ant property.
    String dir = getProject().getProperty(FORMIC_BUILDDIR_PROPERTY);
    return dir != null ? dir : DEFAULT_BUILDDIR;
  }

  /**
   * Validate that the OS was supplied and that it is valid.
   */
  private void checkOs ()
    throws BuildException
  {
    check("os", os,
      "The target OS must be specified.\n" +
      "Possible values include linux, unix, and windows.");

    if (!LINUX.equals(os) &&
        !UNIX.equals(os) &&
        !WINDOWS.equals(os))
    {
      throw new BuildException(
          "Currently only linux/unix and windows are supported as target " +
          "operating systems.");
    }
  }

  /**
   * Validates that the specified attribute was supplied.
   *
   * @param _name The attribute name.
   * @param _value The attribute value.
   * @param _message The message to display.
   */
  private void check (String _name, Object _value, String _message)
    throws BuildException
  {
    if (_value == null){
      throw new BuildException(
          "Attribute '" + _name + "' is required.\n" + _message);
    }
  }

  /**
   * Builds a self extracting shell script for *nix operating systems.
   */
  private void buildSelfExtractingShellScript (File archive)
    throws BuildException
  {
    Concat concat = new Concat();
    concat.setTaskName(getTaskName());
    concat.setProject(getProject());
    concat.setDestfile(destFile);
    concat.setBinary(true);

    PathElement script = concat.createPath().createPathElement();
    script.setLocation(new File(formicHome + "/ant/resources/selfextract"));

    PathElement tarFile = concat.createPath().createPathElement();
    tarFile.setLocation(archive);

    concat.execute();

    // set the file as executable
    Chmod chmod = new Chmod();
    chmod.setProject(getProject());
    chmod.setFile(destFile);
    chmod.setPerm("755");

    chmod.execute();
  }

  /**
   * Builds an executable for Windows.
   */
  private void buildWindowsExecutable (File archive)
    throws BuildException
  {
    // construct bootstrap jar
    File jar = constructBoostrapJar(archive);

    // generate config
    File config = generateLaunch4jConfig();

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
    throws BuildException
  {
    File bootstrap = new File(
        getProject().getProperty("basedir") + "/" + buildDir + "/formic-boostrap.jar");

    Copy copy = new Copy();
    copy.setProject(getProject());
    copy.setTaskName(getTaskName());
    copy.setTofile(bootstrap);
    copy.setFile(new File(formicHome + "/ant/resources/formic-bootstrap.jar"));
    copy.execute();

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
  private File generateLaunch4jConfig ()
    throws BuildException
  {
    File launch4jConfig = new File(buildDir + "/launch4j.config.xml");

    Copy copy = new Copy();
    copy.setTaskName(getTaskName());
    copy.setProject(getProject());
    copy.setTofile(launch4jConfig);
    copy.setFile(new File(formicHome + "/ant/resources/launch4j.config.xml"));
    copy.execute();

    /*Replace replace = new Replace();
    replace.setProject(getProject());
    replace.setFile(launch4jConfig);
    replace.execute();*/

    return launch4jConfig;
  }

  /**
   * Executes the launch4j task to generate the windows executable.
   *
   * @param config The launch4j config file to use.
   * @param jar The jar file to be executed.
   */
  private void executeLaunch4j (File config, File jar)
    throws BuildException
  {
    // set location of launch4j distribution
    Property property = new Property();
    property.setName("launch4j.dir");
    property.setLocation(new File(formicHome + "/ant/resources/launch4j"));
    property.setProject(getProject());
    property.execute();

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
    try{
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
      setOutfile.invoke(launch4j, new Object[]{destFile});

      launch4j.execute();
    }catch(Exception e){
      throw new BuildException(e);
    }
  }

// Tar delegation methods.

  /**
   * @see Tar#createTarFileSet()
   */
  public Tar.TarFileSet createTarFileSet ()
  {
    return getTar().createTarFileSet();
  }

  /**
   * @see Tar#setCompression(Tar.TarCompressionMethod)
   */
  public void setCompression (Tar.TarCompressionMethod _method)
  {
    getTar().setCompression(_method);
  }

// Zip delegation methods.

  /**
   * @see Zip#setCompress(boolean)
   */
  public void setCompress (boolean _compress)
  {
    getZip().setCompress(_compress);
  }

  /**
   * @see Zip#addFileset(FileSet)
   */
  public void addFileset (FileSet _fileset)
  {
    getZip().addFileset(_fileset);
  }

  /**
   * @see Zip#addZipfileset(ZipFileSet)
   */
  public void addZipfileset (ZipFileSet _fileset)
  {
    getZip().addZipfileset(_fileset);
  }

  /**
   * @see Zip#addZipGroupFileset(FileSet)
   */
  public void addZipGroupFileset (FileSet _fileset)
  {
    getZip().addZipGroupFileset(_fileset);
  }
}
