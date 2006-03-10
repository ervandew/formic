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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.apache.tools.ant.taskdefs.Tar;
import org.apache.tools.ant.taskdefs.Zip;

import org.apache.tools.ant.types.FileSet;
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
  // FIXME
  private static final String FORMIC_HOME = ".";

  private static final String UNIX = "unix";
  private static final String LINUX = "linux";
  private static final String WINDOWS = "windows";

  private String os;
  private File destFile;
  private Tar tar;
  private Zip zip;

  /**
   * Executes this task.
   */
  public void execute ()
    throws BuildException
  {
    checkOs();
    check("destfile", destFile, "");

    try{
      if(os.equals(LINUX) || os.equals(UNIX)){
        Tar tar = getTar();

        Tar.TarFileSet antFiles = tar.createTarFileSet();
        antFiles.setDir(new File(FORMIC_HOME));
        antFiles.setIncludes("ant/**/*");
        antFiles.setExcludes("ant/bin/**/*");

        Tar.TarFileSet antBinFiles = tar.createTarFileSet();
        antBinFiles.setDir(new File(FORMIC_HOME));
        antBinFiles.setMode("755");
        antBinFiles.setIncludes("ant/bin/ant");

        tar.setDestFile(destFile);
        tar.setTaskName("tar");
        tar.setProject(getProject());
        tar.execute();
      }else{
        Zip zip = getZip();

        FileSet antFiles = new FileSet();
        antFiles.setDir(new File(FORMIC_HOME));
        antFiles.setIncludes("ant/**/*");
        zip.addFileset(antFiles);

        zip.setDestFile(destFile);
        zip.setTaskName("zip");
        zip.setProject(getProject());
        zip.execute();
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
   * Validate that the OS was supplied and that it is valid.
   */
  private void checkOs ()
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
  {
    if (_value == null){
      throw new BuildException(
          "Attribute '" + _name + "' is required.\n" + _message);
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
