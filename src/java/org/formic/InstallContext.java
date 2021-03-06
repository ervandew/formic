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
package org.formic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.tools.ant.Project;

/**
 * Context that contains the current state of the install process including any
 * user inputed values.
 *
 * @author Eric Van Dewoestine
 */
public class InstallContext
{
  private Map values = new HashMap();

  /**
   * Determines if this context contains the supplied key.
   *
   * @param key The key.
   * @return true if contains the key, false otherwise.
   */
  public boolean containsKey(Object key)
  {
    return values.containsKey(key);
  }

  /**
   * Gets the value stored under the supplied key.
   *
   * @param key The key the value is stored under.
   * @return The value or null if not found.
   */
  public Object getValue(Object key)
  {
    return values.containsKey(key) ?
      values.get(key) : getProject().getProperties().get(key);
  }

  /**
   * Gets all keys that have the specified prefix.
   *
   * @param prefix The prefix.
   * @return array of keys.
   */
  public String[] getKeysByPrefix(String prefix)
  {
    return getKeysByPrefix(prefix, false);
  }

  /**
   * Gets all keys that have the specified prefix.
   *
   * @param prefix The prefix.
   * @return array of keys.
   */
  public String[] getKeysByPrefix(String prefix, boolean sort)
  {
    ArrayList results = new ArrayList();
    for (Iterator ii = values.keySet().iterator(); ii.hasNext();){
      String key = (String)ii.next();
      if(key.startsWith(prefix)){
        results.add(key);
      }
    }

    Hashtable properties = getProject().getProperties();
    for (Iterator ii = properties.keySet().iterator(); ii.hasNext();){
      String key = (String)ii.next();
      if(key.startsWith(prefix) && !results.contains(key)){
        results.add(key);
      }
    }

    if (sort){
      Collections.sort(results);
    }

    return (String[])results.toArray(new String[results.size()]);
  }

  /**
   * Stores a value using the supplied key.
   *
   * @param key The key to store the value under.
   * @param value The value to store.
   */
  public void setValue(Object key, Object value)
  {
    values.put(key, value);
    Installer.getProject().setProperty(
        key.toString(), value != null ? value.toString() : (String)value);
  }

  /**
   * Removes the value stored at the supplied key.
   *
   * @param key The key.
   * @return The value removed or null if none.
   */
  public Object removeValue(Object key)
  {
    return values.remove(key);
  }

  /**
   * Gets Iterator to iterate over all available keys.
   *
   * @return Iterator of available keys.
   */
  public Iterator keys()
  {
    return values.keySet().iterator();
  }

  /**
   * Gets the current ant project.
   *
   * @return The ant project.
   */
  public Project getProject()
  {
    return Installer.getProject();
  }
}
