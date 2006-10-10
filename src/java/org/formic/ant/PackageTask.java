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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.apache.tools.ant.taskdefs.Tar;
import org.apache.tools.ant.taskdefs.Zip;

import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;

import org.formic.ant.impl.UnixPackager;
import org.formic.ant.impl.WindowsPackager;

import org.formic.ant.type.LibSet;

import org.formic.ant.util.AntUtils;

/**
 * Task for packaging the installer for distribution.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class PackageTask
  extends Task
{
  private static final String FORMIC_BUILDDIR_PROPERTY = "formic.builddir";
  private static final String DEFAULT_BUILDDIR = "build/formic";

  private static final Map PACKAGERS = new HashMap();
  static{
    PACKAGERS.put("unix", UnixPackager.class);
    PACKAGERS.put("linux", UnixPackager.class);
    PACKAGERS.put("windows", WindowsPackager.class);
  }

  private String os;
  private File destFile;
  private Tar tar;
  private Zip zip;
  private List libsets = new ArrayList();

  /**
   * Executes this task.
   */
  public void execute ()
    throws BuildException
  {
    checkOs();
    check("destfile", destFile, "");

    try{
      Packager packager = (Packager)((Class)PACKAGERS.get(os)).newInstance();
      packager.setProject(getProject());
      packager.setTaskName(getTaskName());
      packager.setFormicHome(AntUtils.getFormicHome(getProject()));
      packager.setBuildDir(determineBuildDir());
      packager.setDestFile(destFile);

      if(packager instanceof UnixPackager){
        for (Iterator ii = libsets.iterator(); ii.hasNext();){
          LibSet libset = (LibSet)ii.next();
          Tar.TarFileSet tarset = new Tar.TarFileSet(libset);
          tarset.setPrefix("ant/lib");
          getTar().addFileset(tarset);
        }
        ((UnixPackager)packager).setTar(getTar());
      }else if(packager instanceof WindowsPackager){
        for (Iterator ii = libsets.iterator(); ii.hasNext();){
          LibSet libset = (LibSet)ii.next();
          ZipFileSet zipset = new ZipFileSet(libset);
          zipset.setPrefix("ant/lib");
          getZip().addFileset(zipset);
        }
        ((WindowsPackager)packager).setZip(getZip());
      }

      packager.execute();
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
   * Determines the build directory.
   *
   * @return The build directory.
   */
  private String determineBuildDir ()
    throws BuildException
  {
    // first try ant property.
    String dir = getProject().getProperty(FORMIC_BUILDDIR_PROPERTY);
    return dir != null ?
      AntUtils.resolve(getProject(), dir) :
      AntUtils.resolve(getProject(), DEFAULT_BUILDDIR);
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

    if (!PACKAGERS.containsKey(os)){
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
   * Adds the supplied libset.
   *
   * @param libset The libset to add.
   */
  public void addLibset (LibSet libset)
  {
    libsets.add(libset);
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
