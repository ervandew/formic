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

/**
 * Model which represents the available fields in the form.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public interface FormModel
{
  /**
   * Property name used for property change events for {@link #isValid()}.
   */
  public static final String FORM_VALID = "formValid";

  /**
   * Gets the form's name.<br/>
   * The form name is used as a prefix for field names and resource
   * keys.
   *
   * @return The form's name.
   */
  public String getName ();

  /**
   * Creates the FormFieldModel for the given field name if one does not already
   * exist.
   *
   * @param name The field name.
   * @param validator The possibly null validator for this field.
   * @return The FormFieldModel.
   */
  public FormFieldModel createFieldModel (String name, Validator validator);

  /**
   * Gets the FormFieldModel for the given field name.
   *
   * @param name The field name.
   * @return The FormFieldModel or null if not found.
   */
  public FormFieldModel getFieldModel (String name);

  /**
   * Determines if the currently entered data is valid.
   *
   * @return true if the data is all valid, false otherwise.
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

  /**
   * Adds the supplied listener.
   *
   * @param listener The listener.
   */
  public void addFormFieldListener (FormFieldListener listener);

  /**
   * Removes the supplied listener.
   *
   * @param listener The listener.
   */
  public void removeFormFieldListener (FormFieldListener listener);

  /**
   * Defines a listener that listens for property change events to underlying
   * form fields.
   */
  public static interface FormFieldListener
    extends PropertyChangeListener
  {
  }
}
