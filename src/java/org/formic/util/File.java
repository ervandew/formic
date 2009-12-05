/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2009  Eric Van Dewoestine
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

import java.io.IOException;

import java.net.URI;

import org.apache.tools.ant.taskdefs.condition.Os;

/**
 * Extension to {@link java.io.File} which accounts for java bug
 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6203387">6203387</a>.
 *
 * @author Eric Van Dewoestine
 */
public class File
  extends java.io.File
{
  /**
   * @see java.io.File#File(String)
   */
  public File(String pathname)
  {
    super(pathname);
  }

  /**
   * @see java.io.File#File(String,String)
   */
  public File(String parent, String child)
  {
    super(parent, child);
  }

  /**
   * @see java.io.File#File(File,String)
   */
  public File(File parent, String child)
  {
    super(parent, child);
  }

  /**
   * @see java.io.File#File(URI)
   */
  public File(URI uri)
  {
    super(uri);
  }

  /**
   * {@inheritDoc}
   */
  public boolean canWrite()
    throws SecurityException
  {
    if(Os.isFamily(Os.FAMILY_WINDOWS)){
      if (isDirectory()){
        try{
          java.io.File temp = java.io.File.createTempFile("formic", ".txt", this);
          temp.delete();
          return true;
        }catch(IOException ioe){
          return false;
        }
      }
      // FIXME: this won't be reliable on windows but aside from invoking
      // native code, how can we determine if the file is writable by the user
      // w/out altering the file?
      return super.canWrite();
    }
    return super.canWrite();
  }
}
