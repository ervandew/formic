/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2012  Eric Van Dewoestine
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import org.apache.tools.ant.taskdefs.Concat;
import org.apache.tools.ant.taskdefs.Copy;

import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;

import org.formic.ant.type.LibSet;

import org.formic.ant.util.AntUtils;

import com.simontuffs.onejar.ant.OneJarTask;

/**
 * Task for packaging the installer for distribution.
 *
 * @author Eric Van Dewoestine
 */
public class PackageTask
  extends Task
{
  private static final String FORMIC_BUILDDIR_PROPERTY = "formic.builddir";
  private static final String DEFAULT_BUILDDIR = "build/formic";

  private File destFile;
  private String buildDir;
  private OneJarTask jar;
  private List libsets = new ArrayList();

  /**
   * Executes this task.
   */
  public void execute()
    throws BuildException
  {
    log("Building installer jar...");

    check("destfile", destFile, "");

    if (getBuildDir() == null){
      setBuildDir(determineBuildDir());
    }

    Project project = getProject();
    String formicHome = AntUtils.getFormicHome(project);

    File libDir = new File(formicHome + "/lib");
    try{
      OneJarTask jar = getJar();
      jar.setDestFile(destFile);
      jar.setTaskName(getTaskName());
      jar.setProject(project);

      Copy cp = new Copy();
      cp.setFile(new File(libDir + "/formic.jar"));
      cp.setTofile(new File(buildDir + "/main.jar"));
      cp.execute();

      OneJarTask.Main main = new OneJarTask.Main();
      main.setJar(new File(buildDir + "/main.jar"));
      jar.addMain(main);

      OneJarTask.Lib lib = new OneJarTask.Lib();
      for (Iterator ii = libsets.iterator(); ii.hasNext();){
        LibSet libset = (LibSet)ii.next();
        ZipFileSet zipset = new ZipFileSet(libset);
        lib.addFileSet(zipset);
      }
      ZipFileSet libs = new ZipFileSet();
      libs.setDir(libDir);
      libs.setIncludes("*.jar");
      libs.setExcludes("one-jar-*.jar");
      // excluding until we really support console mode
      libs.setExcludes("charva-*");
      // excluding until this is utilized (for an uninstaller presumably)
      libs.setExcludes("jregisterykey-*");
      lib.addFileSet(libs);
      jar.addConfiguredLib(lib);

      // create one-jar.properties file (currently only necessary to set
      // application menu name for OSX).
      Concat concat = new Concat();
      concat.setDestfile(new File(buildDir + "/one-jar.properties"));
      concat.addText("com.apple.mrj.application.apple.menu.about.name=Installer");
      concat.execute();

      ZipFileSet files = new ZipFileSet();
      files.setDir(new File(buildDir));
      files.setIncludes("one-jar.properties");
      addFileset(files);

      jar.execute();
    }catch(BuildException be){
      throw be;
    }catch(Exception e){
      if(e instanceof BuildException){
        throw (BuildException)e;
      }
      throw new BuildException(e);
    }
  }

  /**
   * Sets the destFile for this instance.
   *
   * @param destFile The destFile.
   */
  public void setDestFile(File destFile)
  {
    this.destFile = destFile;
  }

  /**
   * Gets the jar instance for this task, creating it if necessary.
   *
   * @return The jar instance.
   */
  protected OneJarTask getJar()
  {
    if(jar == null){
      jar = new OneJarTask();
    }
    return jar;
  }

  /**
   * Gets the buildDir for this instance.
   *
   * @return The buildDir.
   */
  public String getBuildDir()
  {
    return this.buildDir;
  }

  /**
   * Set the buildDir.
   *
   * @param buildDir The buildDir.
   */
  public void setBuildDir(String buildDir)
  {
    this.buildDir = buildDir;
    AntUtils.mkdir(getProject(), getTaskName(), new File(buildDir));
  }

  /**
   * Determines the build directory.
   *
   * @return The build directory.
   */
  private String determineBuildDir()
    throws BuildException
  {
    // first try ant property.
    String dir = getProject().getProperty(FORMIC_BUILDDIR_PROPERTY);
    return dir != null ?
      AntUtils.resolve(getProject(), dir) :
      AntUtils.resolve(getProject(), DEFAULT_BUILDDIR);
  }

  /**
   * Validates that the specified attribute was supplied.
   *
   * @param _name The attribute name.
   * @param _value The attribute value.
   * @param _message The message to display.
   */
  private void check(String _name, Object _value, String _message)
    throws BuildException
  {
    if (_value == null){
      throw new BuildException(
          "Attribute '" + _name + "' is required.\n" + _message);
    }
  }

  /**
   * Adds the supplied libset.
   *
   * @param libset The libset to add.
   */
  public void addLibset(LibSet libset)
  {
    libsets.add(libset);
  }

// Jar delegation methods.

  /**
   * @see OneJarTask#addFileset(FileSet)
   */
  public void addFileset(FileSet _fileset)
  {
    getJar().addFileset(_fileset);
  }

  /**
   * @see OneJarTask#addZipfileset(ZipFileSet)
   */
  public void addZipfileset(ZipFileSet _fileset)
  {
    getJar().addZipfileset(_fileset);
  }

  /**
   * @see OneJarTask#addZipGroupFileset(FileSet)
   */
  public void addZipGroupFileset(FileSet _fileset)
  {
    getJar().addZipGroupFileset(_fileset);
  }
}
