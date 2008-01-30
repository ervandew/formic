/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008  Eric Van Dewoestine
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
package org.formic.wizard.form.validator;

import java.lang.reflect.Array;

import java.util.Collection;
import java.util.Map;

import org.formic.wizard.form.Validator;

/**
 * Implementation of {@link Validator} that validates that the value is not null
 * or empty.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class RequiredValidator
  implements Validator
{
  public static final RequiredValidator INSTANCE = new RequiredValidator();

  private static final String MESSAGE = "validator.required";

  /**
   * Prevent construction.
   */
  private RequiredValidator ()
  {
  }

  /**
   * {@inheritDoc}
   * @see Validator#isValid(Object)
   */
  public boolean isValid (Object value)
  {
    if (value instanceof String){
      return ((String)value).trim().length() > 0;
    }

    if (value != null && value.getClass().isArray()){
      return Array.getLength(value) > 0;
    }

    if (value instanceof Collection){
      return ((Collection)value).size() > 0;
    }

    if (value instanceof Map){
      return ((Map)value).size() > 0;
    }

    return value != null;
  }

  /**
   * {@inheritDoc}
   * @see Validator#getErrorMessage()
   */
  public String getErrorMessage ()
  {
    return MESSAGE;
  }

  /**
   * {@inheritDoc}
   * @see Object#hashCode()
   */
  public int hashCode ()
  {
    return 11;
  }

  /**
   * {@inheritDoc}
   * @see Object#equals(Object)
   */
  public boolean equals (Object obj)
  {
    if(obj == this){
      return true;
    }

    return obj instanceof RequiredValidator;
  }
}
