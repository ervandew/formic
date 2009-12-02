/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008  Eric Van Dewoestine
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Aggregates multiple resource bundles together.
 *
 * @author Eric Van Dewoestine
 */
public class ResourceBundleAggregate
  extends ResourceBundle
{
  private List bundles = new ArrayList();
  private Enumeration keys;

  /**
   * Add the supplied bundle to this aggregate.
   *
   * @param _bundle The bundle to add.
   */
  public void addBundle(ResourceBundle _bundle)
  {
    bundles.add(_bundle);
  }

  /**
   * {@inheritDoc}
   * @see java.util.ResourceBundle#handleGetObject(String)
   */
  protected Object handleGetObject(String _key)
  {
    for (int ii = 0; ii < bundles.size(); ii++){
      ResourceBundle bundle = (ResourceBundle)bundles.get(ii);
      try{
        Object obj = bundle.getObject(_key);
        return obj;
      }catch(MissingResourceException mre){
        // continue to next bundle.
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * @see java.util.ResourceBundle#getKeys()
   */
  public Enumeration getKeys()
  {
    if(keys == null){
      List keys = new ArrayList();
      for (int ii = 0; ii < bundles.size(); ii++){
        ResourceBundle bundle = (ResourceBundle)bundles.get(ii);
        keys.addAll(Collections.list(bundle.getKeys()));
      }
      this.keys = Collections.enumeration(keys);
    }
    return this.keys;
  }
}
