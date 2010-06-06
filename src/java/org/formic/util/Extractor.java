/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2010  Eric Van Dewoestine
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
package org.formic.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * Handles extraction of zip resources.
 *
 * @author Eric Van Dewoestine
 */
public class Extractor
{
  /**
   * Reads an archive from the classpath and extracts it to the specified
   * destination.
   *
   * @param resource The resource representing the archive.
   * @param dest The destination to extract the archive to.
   */
  public static void extractResource(String resource, File dest)
    throws IOException
  {
    File archive = File.createTempFile(
        FilenameUtils.getBaseName(resource),
        "." + FilenameUtils.getExtension(resource));
    Extractor.readArchive(resource, archive);
    Extractor.extractArchive(archive, dest, null);

    // delete temp archive.
    archive.delete();
  }

  /**
   * Reads an archive from the classpath and writes it to the specified
   * destination.
   *
   * @param resource The resource representing the archive.
   * @param dest The destination to write the archive to.
   */
  public static void readArchive(String resource, File dest)
    throws IOException
  {
    FileOutputStream out = null;
    InputStream in = null;
    try{
      out = new FileOutputStream(dest);
      in = Extractor.class.getResourceAsStream(resource);

      if (in == null){
        throw new IOException("Resource not found: " + resource);
      }

      IOUtils.copy(in, out);

      in.close();
      out.close();
    }finally{
      IOUtils.closeQuietly(in);
      IOUtils.closeQuietly(out);
    }
  }

  /**
   * Extracts the specified archive to the supplied destination.
   *
   * @param archive The archive to extract.
   * @param dest The directory to extract it to.
   */
  public static void extractArchive(
      File archive, File dest, ArchiveExtractionListener listener)
    throws IOException
  {
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
          String name = dest + "/" + entry.getName();
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

          IOUtils.copy(in, out);

          in.close();
          out.close();

          if(listener != null){
            listener.finishExtractingFile(ii, entry.getName());
          }
        }
      }
    }finally{
      try{
        file.close();
      }catch(Exception ignore){
      }
    }

    if(listener != null){
      listener.finishExtraction();
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
    public void startExtraction(int count);

    /**
     * Invoked after all files have been extracted.
     */
    public void finishExtraction();

    /**
     * Invoked when the supplied file is to be extracted.
     *
     * @param index The index of the file within the archive.
     * @param file The file name.
     */
    public void startExtractingFile(int index, String file);

    /**
     * Invoked when the supplied file has been completely extracted.
     *
     * @param index The index of the file within the archive.
     * @param file The file name.
     */
    public void finishExtractingFile(int index, String file);
  }
}
