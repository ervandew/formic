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
package org.formic.form.validator;

import org.formic.form.Validator;

/**
 * Used to build a validator.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class ValidatorBuilder
{
  public static final Validator REQUIRED = new RequiredValidator();

  private AggregateValidator validator;

  /**
   * Constructs a new instance.
   */
  public ValidatorBuilder ()
  {
    validator = new AggregateValidator();
  }

  /**
   * Adds RequiredValidator to the chain.
   *
   * @return This ValidatorBuilder instance for method chaining.
   */
  public ValidatorBuilder required ()
  {
    validator(REQUIRED);
    return this;
  }

  /**
   * Adds the supplied validator to the chain.
   *
   * @param validator The validator to add.
   * @return This ValidatorBuilder instance for method chaining.
   */
  public ValidatorBuilder validator (Validator validator)
  {
    this.validator.addValidator(validator);
    return this;
  }

  /**
   * Gets the validator built from the previous method calls.
   *
   * @return The validator.
   */
  public Validator validator ()
  {
    return validator;
  }
}
