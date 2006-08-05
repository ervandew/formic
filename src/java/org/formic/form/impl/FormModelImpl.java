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
  private boolean valid;
  private Map fields = new HashMap();

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
    }
    return field;
  }

  /**
   * {@inheritDoc}
   * @see FormModel#isValid()
   */
  public boolean isValid ()
  {
    return fields.size() > 0 ? valid : true;
  }

  /**
   * {@inheritDoc}
   * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange (final PropertyChangeEvent evt)
  {
    if(FormFieldModel.FIELD_VALID.equals(evt.getPropertyName())){
      boolean valid = ((Boolean)Worker.post(new Job(){
        public Object run (){
          for (Iterator ii = fields.values().iterator(); ii.hasNext();){
            FormFieldModel field = (FormFieldModel)ii.next();
            if(!field.equals(evt.getSource()) && !field.isValid()){
              return Boolean.FALSE;
            }
          }
          return Boolean.TRUE;
        }
      })).booleanValue();
      firePropertyChange(FORM_VALID, this.valid, this.valid = valid);
    }
  }
}
