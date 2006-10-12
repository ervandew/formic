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
import java.beans.PropertyChangeSupport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.jgoodies.binding.beans.Model;

import org.formic.form.FormFieldModel;
import org.formic.form.FormModel;
import org.formic.form.Validator;

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
  private PropertyChangeSupport formFieldSupport =
    new PropertyChangeSupport(this);

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
   * @see FormModel#createFieldModel(String,Validator)
   */
  public FormFieldModel createFieldModel (String name, Validator validator)
  {
    name = resolveName(name);
    FormFieldModel field = (FormFieldModel)fields.get(name);
    if(field == null){
      field = new FormFieldModelImpl(name, validator, this);
      fields.put(name, field);
      validFields.put(name, field.isValid() ? Boolean.TRUE : Boolean.FALSE);
    }
    return field;
  }

  /**
   * {@inheritDoc}
   * @see FormModel#getFieldModel(String)
   */
  public FormFieldModel getFieldModel (String name)
  {
    return (FormFieldModel)fields.get(resolveName(name));
  }

  /**
   * {@inheritDoc}
   * @see FormModel#getFieldModels()
   */
  public Collection getFieldModels ()
  {
    return fields.values();
  }

  /**
   * {@inheritDoc}
   * @see FormModel#isValid()
   */
  public boolean isValid ()
  {
    if (validFields.size() == 0){
      return true;
    }

    for (Iterator ii = validFields.values().iterator(); ii.hasNext();){
      if(ii.next() == Boolean.FALSE){
        return false;
      }
    }

    return true;
  }

  /**
   * {@inheritDoc}
   * @see FormModel#addFormFieldListener(FormFieldListener)
   */
  public void addFormFieldListener (FormFieldListener listener)
  {
    formFieldSupport.addPropertyChangeListener(listener);
  }

  /**
   * {@inheritDoc}
   * @see FormModel#removeFormFieldListener(FormFieldListener)
   */
  public void removeFormFieldListener (FormFieldListener listener)
  {
    formFieldSupport.removePropertyChangeListener(listener);
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

    if(evt.getSource() instanceof FormFieldModel){
      formFieldSupport.firePropertyChange(evt);
    }
  }

  /**
   * Resolve the given name into the proper field name for this model.
   *
   * @param name The orginal name.
   * @return The resolved name.
   */
  private String resolveName (String name)
  {
    if(!name.startsWith(this.name + '.')){
      return this.name + '.' + name;
    }
    return name;
  }
}
