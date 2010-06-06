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
package org.formic.ant;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import org.apache.tools.ant.types.Resource;

public class ClasspathResource
  extends Resource
{
  private URL url;

  public ClasspathResource(String name){
    super("/" + name);
    url = ClasspathResource.class.getResource(getName());
  }

  public URL getUrl()
  {
    return url;
  }

  /**
   * {@inheritDoc}
   * @see Resource#isExists()
   */
  public boolean isExists()
  {
    return url != null;
  }

  /**
   * {@inheritDoc}
   * @see Resource#getInputStream()
   */
  public InputStream getInputStream()
    throws IOException
  {
    return ClasspathResource.class.getResourceAsStream(getName());
  }
}
