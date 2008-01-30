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

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.formic.Installer;

import org.formic.wizard.form.validator.AggregateValidator;
import org.formic.wizard.form.validator.RequiredValidator;

/**
 * Utilities for decorating components based on their validator and state.
 *
 * @author Eric Van Dewoestine
 * @version $Revision$
 */
public class ValidationUtils
{
  private static final String REQUIRED = "required";
  private static final String VALIDATOR = "validator";
  private static final String VALIDATION_ERROR = "validation.error";

  private static final Color VALID_COLOR =
    new JTextField().getBackground();
  private static final Color ERROR_COLOR =
    Installer.getColor("validation.color.error");
  private static final Color REQUIRED_COLOR =
    Installer.getColor("validation.color.required");

  /**
   * Decorates the supplied field according to the associated validator.
   *
   * @param component The component to decorate.
   * @param validator The validator for the component.
   */
  public static void decorate (JComponent component, Validator validator)
  {
    if(validator != null){
      component.putClientProperty(VALIDATOR, validator);
      if(validator instanceof RequiredValidator ||
          (validator instanceof AggregateValidator &&
           ((AggregateValidator)validator).containsValidator(RequiredValidator.class))
        )
      {
        component.putClientProperty(REQUIRED, Boolean.TRUE);
        component.setBackground(REQUIRED_COLOR);
      }
    }
  }

  /**
   * Validates the supplied component.
   *
   * @param component The component.
   * @param value The component's value.
   * @return true if the field value is valid, false otherwise.
   */
  public static boolean validate (JComponent component, String value)
  {
    Validator validator = (Validator)component.getClientProperty(VALIDATOR);
    if(validator != null){
      boolean valid = validator.isValid(value);
      if(!valid){
        component.putClientProperty(VALIDATION_ERROR, validator.getErrorMessage());
        component.setBackground(ERROR_COLOR);
      }else{
        component.putClientProperty(VALIDATION_ERROR, null);
        component.setBackground(VALID_COLOR);
      }
      return valid;
    }
    return true;
  }

  /**
   * Determines if the supplied field is required.
   *
   * @param component The component.
   * @return true if required, false otherwise.
   */
  public static boolean isRequired (JComponent component)
  {
    Boolean bool = (Boolean)component.getClientProperty(REQUIRED);
    return bool != null ? bool.booleanValue() : false;
  }

  /**
   * Gets the validation error message for the supplied component.
   *
   * @param component The component.
   * @return The error message or null if none.
   */
  public static String getValidationError (JComponent component)
  {
    return (String)component.getClientProperty(VALIDATION_ERROR);
  }
}
