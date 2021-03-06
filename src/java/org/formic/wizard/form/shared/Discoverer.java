/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008 Eric Van Dewoestine
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
package org.formic.wizard.form.shared;

/**
 * Defines classes that can be used to discover a possible default value for a
 * form field based on the user's environment.
 *
 * @author Eric Van Dewoestine
 */
public abstract class Discoverer
{
  /**
   * Invoked to discover the default value.
   *
   * @return The default value or null if none.
   */
  public abstract String discover();

  /**
   * Given a discoverer class name, instantiates it and executes its discover
   * method.
   *
   * @param discoverer The discoverer class name.
   * @return The discovered value.
   */
  public static String discover(String discoverer)
  {
    try{
      Discoverer instance = (Discoverer)
        Class.forName(discoverer).newInstance();
      return instance.discover();
    }catch(Exception e){
      throw new RuntimeException(e);
    }
  }
}
