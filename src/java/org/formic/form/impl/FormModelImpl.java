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
package org.formic.form.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.jgoodies.binding.beans.Model;

import foxtrot.Job;
import foxtrot.Worker;

import org.formic.form.FormFieldModel;
import org.formic.form.FormModel;

/**
 * Implementation of {@link FormModel}.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class FormModelImpl
  extends Model
  implements FormModel, PropertyChangeListener
{
  private String name;
  private Map fields = new HashMap();
  private Map validFields = new HashMap();
  private boolean valid;

  /**
   * Constructs a new instance.
   *
   * @param name The name for this instance.
   */
  public FormModelImpl (String name)
  {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   * @see FormModel#getName()
   */
  public String getName ()
  {
    return this.name;
  }

  /**
   * {@inheritDoc}
   * @see FormModel#getFieldModel(String)
   */
  public FormFieldModel getFieldModel (String name)
  {
    name = this.name + '.' + name;
    FormFieldModel field = (FormFieldModel)fields.get(name);
    if(field == null){
      field = new FormFieldModelImpl(name);
      field.addPropertyChangeListener(this);
      fields.put(name, field);
      validFields.put(name, Boolean.FALSE);
    }
    return field;
  }

  /**
   * {@inheritDoc}
   * @see FormModel#isValid()
   */
  public boolean isValid ()
  {
    if (fields.size() == 0){
      return valid = true;
    }

    for (Iterator ii = validFields.values().iterator(); ii.hasNext();){
      if(ii.next() == Boolean.FALSE){
        return valid = false;
      }
    }

    return valid = true;
  }

  /**
   * {@inheritDoc}
   * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange (final PropertyChangeEvent evt)
  {
    if(FormFieldModel.VALID.equals(evt.getPropertyName())){
      Boolean valid = (Boolean)evt.getNewValue();
      FormFieldModel field = (FormFieldModel)evt.getSource();
      validFields.put(field.getName(), valid);
      firePropertyChange(FORM_VALID, this.valid, this.valid = isValid());
    }
  }
}
