/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2009  Eric Van Dewoestine
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

import org.formic.util.File;

import org.formic.wizard.form.Validator;

/**
 * Validator that determines if the user has write permissions to the value
 * which should be a valid file or directory path.
 *
 * @author Eric Van Dewoestine
 */
public class IsWritableValidator
  implements Validator
{
  public static final IsWritableValidator INSTANCE = new IsWritableValidator();

  private static final String MESSAGE = "validator.iswritable";

  /**
   * Prevent construction.
   */
  private IsWritableValidator()
  {
  }

  /**
   * {@inheritDoc}
   * @see Validator#isValid(Object)
   */
  public boolean isValid(Object value)
  {
    String file = (String)value;
    if(file != null && file.trim().length() > 0){
      return new File(file).canWrite();
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * @see Validator#getErrorMessage()
   */
  public String getErrorMessage()
  {
    return MESSAGE;
  }
}
