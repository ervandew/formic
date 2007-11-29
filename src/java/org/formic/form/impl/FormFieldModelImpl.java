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

import com.jgoodies.binding.beans.DelayedPropertyChangeHandler;

import com.jgoodies.binding.value.AbstractValueModel;

import foxtrot.AsyncTask;
import foxtrot.AsyncWorker;

import org.formic.form.FormFieldModel;
import org.formic.form.Validator;

import org.formic.form.validator.AggregateValidator;
import org.formic.form.validator.RequiredValidator;

/**
 * Implementation of {@link FormFieldModel}.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class FormFieldModelImpl
  extends AbstractValueModel
  implements FormFieldModel
{
  private String name;
  private Object value;
  private Validator validator;
  private boolean isPath;
  private boolean valid = true;

  /**
   * Constructs a new instance.
   *
   * @param name The field name.
   * @param validator The validator for this field.
   * @param listener Initial listener for property change events.
   * @param isPath true if the value will be a path for which all file
   * seperators will be automatically converted to system independent slashes.
   */
  public FormFieldModelImpl (
      String name,
      Validator validator,
      PropertyChangeListener listener,
      boolean isPath)
  {
    this.name = name;
    this.validator = validator;
    this.isPath = isPath;
    addPropertyChangeListener(new DelayedValidator());
    addPropertyChangeListener(listener);

    this.valid = validator != null ? validator.isValid(value) : true;
    // results in flash by errors if related component grabs focus when form is
    // displayed.
    //setValid(validator != null ? validator.isValid(value) : true);
  }

  /**
   * Gets the name for this instance.
   *
   * @return The name.
   */
  public String getName ()
  {
    return this.name;
  }

  /**
   * {@inheritDoc}
   * @see com.jgoodies.binding.value.ValueModel#getValue()
   */
  public Object getValue ()
  {
    return this.value;
  }

  /**
   * {@inheritDoc}
   * @see com.jgoodies.binding.value.ValueModel#setValue(Object)
   */
  public void setValue (Object value)
  {
    if (isPath){
      value = ((String)value).replace('\\', '/');
    }
    firePropertyChange(VALUE, this.value, this.value = value);
  }

  /**
   * {@inheritDoc}
   * @see FormFieldModel#getValidator()
   */
  public Validator getValidator ()
  {
    return validator;
  }

  /**
   * {@inheritDoc}
   * @see FormFieldModel#setValidator(Validator)
   */
  public void setValidator (Validator validator)
  {
    firePropertyChange(VALIDATOR, this.validator, this.validator = validator);
  }

  /**
   * {@inheritDoc}
   * @see FormFieldModel#isValid()
   */
  public boolean isValid ()
  {
    return validator != null ? valid : true;
  }

  /**
   * {@inheritDoc}
   * @see FormFieldModel#isRequired()
   */
  public boolean isRequired ()
  {
    if(validator != null){
      if(validator instanceof RequiredValidator){
        return true;
      }else if(validator instanceof AggregateValidator){
        return ((AggregateValidator)validator)
          .containsValidator(RequiredValidator.INSTANCE);
      }
    }
    return false;
  }

  /**
   * Sets whether or not this field is valid.
   *
   * @param valid True if valid, false otherwise.
   */
  public void setValid (boolean valid)
  {
    firePropertyChange(VALID, !valid, this.valid = valid);
  }

  /**
   * {@inheritDoc}
   * @see Object#toString()
   */
  public String toString ()
  {
    return new StringBuffer()
      .append(name).append(':').append(getValue()).toString();
  }

  /**
   * Property change handler that receives changes on a delay and then validates
   * the value in the background.
   */
  private class DelayedValidator
    extends DelayedPropertyChangeHandler
  {
    /**
     * {@inheritDoc}
     * @see DelayedPropertyChangeHandler#delayedPropertyChange(PropertyChangeEvent)
     */
    public void delayedPropertyChange (PropertyChangeEvent evt)
    {
      String property = evt.getPropertyName();
      if(VALUE.equals(property)){
        validate(FormFieldModelImpl.this.getValidator(), evt.getNewValue());
      }else if(VALIDATOR.equals(property)){
        validate((Validator)evt.getNewValue(),
            FormFieldModelImpl.this.getValue());
      }
    }

    /**
     * Validate the current value against the current validator.
     */
    private void validate (final Validator validator, final Object value)
    {
      if(validator != null){
        // run validation asynchronously in the background.
        AsyncWorker.post(new AsyncTask(){
          private boolean valid;

          // invoked on background thread.
          public Object run () {
            valid = validator.isValid(value);
            return Boolean.valueOf(valid);
          }

          // invoked on EDT
          public void finish () {
            FormFieldModelImpl.this.setValid(valid);
          }
        });
      }else{
        FormFieldModelImpl.this.setValid(true);
      }
    }
  }
}
