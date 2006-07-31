/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2006  Eric Van Dewoestine
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
package org.formic.bootstrap.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Enumeration;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handles extraction of resources.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class Extractor
{
  /*public static void main (String[] args)
    throws Exception
  {
    readArchive("toolkit.jar", "tmp/tmp.zip");
    extractArchive("tmp/tmp.zip", "tmp/");
  }*/

  /**
   * Reads an archive from the classpath and writes it to the specified
   * destination.
   *
   * @param resource The resource representing the archive.
   * @param dest The destination to write the archive to.
   */
  public static void readArchive (String resource, String dest)
    throws Exception
  {
    FileOutputStream out = null;
    InputStream in = null;
    try{
      out = new FileOutputStream(dest);
      in = Extractor.class.getResourceAsStream(resource);

      copy(in, out);

      in.close();
      out.close();
    }finally{
      closeQuietly(in);
      closeQuietly(out);
    }
  }

  /**
   * Extracts the specified archive to the supplied destination.
   *
   * @param archive The archive to extract.
   * @param dest The directory to extract it to.
   */
  public static void extractArchive (
      String archive, String dest, ArchiveExtractionListener listener)
    throws Exception
  {
    if(!dest.endsWith("/")){
      dest = dest + '/';
    }

    ZipFile file = null;
    try{
      file = new ZipFile(archive);

      if(listener != null){
        listener.startExtraction(file.size());
      }

      Enumeration entries = file.entries();
      for(int ii = 0; entries.hasMoreElements(); ii++){
        ZipEntry entry = (ZipEntry)entries.nextElement();
        if(!entry.isDirectory()){
          // create parent directories if necessary.
          String name = dest + entry.getName();
          if(name.indexOf('/') != -1){
            File dir = new File(name.substring(0, name.lastIndexOf('/')));
            if(!dir.exists()){
              dir.mkdirs();
            }
          }

          if(listener != null){
            listener.startExtractingFile(ii, entry.getName());
          }

          FileOutputStream out = new FileOutputStream(name);
          InputStream in = file.getInputStream(entry);

          copy(in, out);

          in.close();
          out.close();

          if(listener != null){
            listener.finishExtractingFile(ii, entry.getName());
          }
        }
      }
    }finally{
      closeQuietly(file);
    }

    if(listener != null){
      listener.finishExtraction();
    }
  }

  /**
   * Copy the contents of the InputStream to the OutputStream.
   *
   * @param in The InputStream to read from.
   * @param out The OutputStream to write to.
   */
  private static void copy (InputStream in, OutputStream out)
    throws Exception
  {
    byte[] buffer = new byte[1024 * 4];
    int n = 0;
    while (-1 != (n = in.read(buffer))) {
      out.write(buffer, 0, n);
    }
  }

  /**
   * Close the supplied object and ignore any exceptions thrown by the close
   * method.
   *
   * @param object The object to close.
   */
  private static void closeQuietly (Object object)
    throws Exception
  {
    if(object != null){
      // for some reason internal sun JarURLInputStream fails on reflection.
      if(object instanceof InputStream){
        ((InputStream)object).close();
        return;
      }

      try{
        Method method =
          object.getClass().getDeclaredMethod("close", (Class[])null);
        method.invoke(object, (Object[])null);
      }catch(IllegalAccessException iae){
        throw iae;
      }catch(NoSuchMethodException nsme){
        throw nsme;
      }catch(InvocationTargetException ite){
        // ignore;
      }
    }
  }

  /**
   * Listener that can be notified of significant events when extracting an
   * archive.
   */
  public static interface ArchiveExtractionListener
  {
    /**
     * Invoked just before beginning to extract files.
     *
     * @param count The number of files to be extracted.
     */
    public void startExtraction (int count);

    /**
     * Invoked after all files have been extracted.
     */
    public void finishExtraction ();

    /**
     * Invoked when the supplied file is to be extracted.
     *
     * @param index The index of the file within the archive.
     * @param file The file name.
     */
    public void startExtractingFile (int index, String file);

    /**
     * Invoked when the supplied file has been completely extracted.
     *
     * @param index The index of the file within the archive.
     * @param file The file name.
     */
    public void finishExtractingFile (int index, String file);
  }
}
