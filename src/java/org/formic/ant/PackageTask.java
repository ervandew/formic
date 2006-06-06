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

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Concat;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Tar;
import org.apache.tools.ant.taskdefs.Zip;

import org.apache.tools.ant.types.FileSet;

import org.apache.tools.ant.types.Path.PathElement;

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
  private static final String FORMIC_HOME_ENV = "env.FORMIC_HOME";

  private static final String UNIX = "unix";
  private static final String LINUX = "linux";
  private static final String WINDOWS = "windows";

  private String os;
  private File destFile;
  private String archive;
  private Tar tar;
  private Zip zip;

  private String formicHome;

  /**
   * Executes this task.
   */
  public void execute ()
    throws BuildException
  {
    formicHome = determineFormicHome();

    checkOs();
    check("destfile", destFile, "");

    try{
      if(os.equals(LINUX) || os.equals(UNIX)){
        File tarFile = new File(archive + ".tar.gz");

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
        tar.setTaskName("tar");
        tar.setProject(getProject());
        tar.execute();

        buildSelfExtractingShellScript(tarFile);
      }else{
        File zipFile = new File(archive + ".zip");
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
        zip.setTaskName("zip");
        zip.setProject(getProject());
        zip.execute();

        buildWindowsExecutable(zipFile);
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
    String path = FilenameUtils.getFullPath(destFile.getAbsolutePath());
    String file = FilenameUtils.getName(destFile.getAbsolutePath());

    if(file.endsWith(".sh")){
      this.archive = path + file.substring(0, file.lastIndexOf('.'));
    }else{
      this.archive = path + file;
    }

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
    concat.setTaskName("concat");
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
