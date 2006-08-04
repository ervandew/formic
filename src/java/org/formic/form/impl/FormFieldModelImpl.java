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

import com.jgoodies.binding.value.AbstractValueModel;

import org.formic.form.FormFieldModel;
import org.formic.form.Validator;

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

  /**
   * Constructs a new instance.
   *
   * @param name The field name.
   */
  public FormFieldModelImpl (String name)
  {
    this.name = name;
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
    this.value = value;
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
    this.validator = validator;
  }
}
