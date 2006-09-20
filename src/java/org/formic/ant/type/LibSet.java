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
package org.formic.ant.type;

import org.apache.tools.ant.types.FileSet;

/**
 * FileSet implementation for library files (jars).
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class LibSet
  extends FileSet
{
  public LibSet()
  {
    super();
  }

  protected LibSet (LibSet libset)
  {
    super(libset);
  }

  /**
   * Return a LibSet that has the same basedir and same patternsets
   * as this one.
   */
  public Object clone()
  {
    if (isReference()) {
      return ((LibSet) getRef(getProject())).clone();
    } else {
      return super.clone();
    }
  }
}
