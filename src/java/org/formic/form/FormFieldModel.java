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
package org.formic.form;

import java.beans.PropertyChangeListener;

import com.jgoodies.binding.value.ValueModel;

/**
 * Represents the data model of a form field.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public interface FormFieldModel
  extends ValueModel
{
  /**
   * Property name used for property change events for property "value".
   */
  public static final String VALUE = "value";

  /**
   * Property name used for property change events for property "validator".
   */
  public static final String VALIDATOR = "validator";

  /**
   * Property name used for property change events for property "valid".
   */
  public static final String VALID = "valid";

  /**
   * Gets the field name this model is bound to.
   *
   * @return The field name.
   */
  public String getName ();

  /**
   * Gets the validator for this field.
   *
   * @return The Validator.
   */
  public Validator getValidator ();

  /**
   * Sets the validator for this field.
   *
   * @param validator The Validator.
   */
  public void setValidator (Validator validator);

  /**
   * Determines if the data in this field is valid.
   *
   * @return true if valid, false otherwise.
   */
  public boolean isValid ();

  /**
   * Adds the supplied listener.
   *
   * @param listener The listener.
   */
  public void addPropertyChangeListener (PropertyChangeListener listener);

  /**
   * Removes the supplied listener.
   *
   * @param listener The listener.
   */
  public void removePropertyChangeListener (PropertyChangeListener listener);
}
