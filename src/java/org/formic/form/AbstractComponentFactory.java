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

import org.formic.form.impl.FormModelImpl;

/**
 * Abstract class containing shared functionality for component factory
 * implementations.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public abstract class AbstractComponentFactory
{
  public static final String FORM_FIELD = "formic.form.field";

  private FormModel model;

  /**
   * Constructs a new instance using a default FormModel.
   *
   * @param name The form name (used as a prefix for field names and resource
   * keys).
   */
  public AbstractComponentFactory (String name)
  {
    this(new FormModelImpl(name));
  }

  /**
   * Constructs a new instance.
   *
   * @param model The model for this instance.
   */
  public AbstractComponentFactory (FormModel model)
  {
    this.model = model;
  }

  /**
   * Gets the underlying FormModel.
   *
   * @return The FormModel.
   */
  public FormModel getFormModel ()
  {
    return model;
  }

  /**
   * Gets the FormFieldModel for the named field.
   *
   * @param name The name of the field.
   * @param validator The validator for the field.
   * @return The FormFieldModel.
   */
  protected FormFieldModel getField (String name, Validator validator)
  {
    return getField(name, validator, false);
  }

  /**
   * Gets the FormFieldModel for the named field.
   *
   * @param name The name of the field.
   * @param validator The validator for the field.
   * @param isPath true if the value will be a path for which all file
   * seperators will be automatically converted to system independent slashes.
   * @return The FormFieldModel.
   */
  protected FormFieldModel getField (
      String name, Validator validator, boolean isPath)
  {
    return model.createFieldModel(name, validator, isPath);
  }
}
