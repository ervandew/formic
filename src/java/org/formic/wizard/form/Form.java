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
package org.formic.wizard.form;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

/**
 * Base class for forms.
 *
 * @author Eric Van Dewoestine
 * @version $Revision$
 */
public class Form
{
  public static final String NAME = "name";
  public static final String VALID = "valid";

  private Map values = new HashMap();

  private List fields = new ArrayList();
  private List invalidFields = new ArrayList();

  private PropertyChangeSupport propertyChangeSupport;

  /**
   * Construct a new instance.
   */
  public Form ()
  {
    propertyChangeSupport = new PropertyChangeSupport(this);
  }

  /**
   * Sets a form value.
   *
   * @param field The form field.
   * @param component The component.
   * @param value The value of the field.
   * @param valid true if the field is value is valid, false otherwise.
   */
  public void setValue (
      FormField field, JComponent component, Object value, boolean valid)
  {
    String name = (String)component.getClientProperty(NAME);
    boolean formValid = isValid();

    invalidFields.remove(field);
    if(valid){
      values.put(name, value);
    }else{
      values.remove(name);
      invalidFields.add(field);
    }

    if(formValid != isValid()){
      propertyChangeSupport.firePropertyChange(VALID, formValid, isValid());
    }
  }

  /**
   * Gets as map of all the form values.
   *
   * @return Map containing all the form values.
   */
  public Map getValues ()
  {
    return values;
  }

  /**
   * Determines if the form is valid.
   *
   * @return true if valid false otherwise.
   */
  public boolean isValid ()
  {
    return invalidFields.size() == 0;
  }

  /**
   * Adds the supplied field to this form.
   *
   * @param field The form field.
   * @param valid true if the form is currently valid, false otherwise.
   */
  protected void addField (FormField field, boolean valid)
  {
    fields.add(field);
    if(!valid){
      boolean formValid = isValid();
      invalidFields.add(field);
      if(formValid != isValid()){
        propertyChangeSupport.firePropertyChange(VALID, formValid, isValid());
      }
    }
  }

  /**
   * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
   */
  public void addPropertyChangeListener (PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  /**
   * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
   */
  public void removePropertyChangeListener (PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
}
