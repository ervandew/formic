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

import org.formic.wizard.form.Validator;

/**
 * Used to build a validator.
 *
 * @author Eric Van Dewoestine
 */
public class ValidatorBuilder
{
  private AggregateValidator validator;

  /**
   * Constructs a new instance.
   */
  public ValidatorBuilder()
  {
    validator = new AggregateValidator();
  }

  /**
   * Adds RequiredValidator to the aggregate valiator.
   *
   * @return This ValidatorBuilder instance for method chaining.
   */
  public ValidatorBuilder required()
  {
    validator(RequiredValidator.INSTANCE);
    return this;
  }

  /**
   * Adds FileExistsValidator to the aggregate validator.
   *
   * @return This ValidatorBuilder instance for method chaining.
   */
  public ValidatorBuilder fileExists()
  {
    validator(FileExistsValidator.INSTANCE);
    return this;
  }

  /**
   * Adds IsDirectoryValidator to the aggregate validator.
   *
   * @return This ValidatorBuilder instance for method chaining.
   */
  public ValidatorBuilder isDirectory()
  {
    validator(IsDirectoryValidator.INSTANCE);
    return this;
  }

  /**
   * Adds IsFileValidator to the aggregate validator.
   *
   * @return This ValidatorBuilder instance for method chaining.
   */
  public ValidatorBuilder isFile()
  {
    validator(IsFileValidator.INSTANCE);
    return this;
  }

  /**
   * Adds IsWritableValidator to the aggregate validator.
   *
   * @return This ValidatorBuilder instance for method chaining.
   */
  public ValidatorBuilder isWritable()
  {
    validator(IsWritableValidator.INSTANCE);
    return this;
  }

  /**
   * Adds the supplied validator to the aggregate validator.
   *
   * @param validator The validator to add.
   * @return This ValidatorBuilder instance for method chaining.
   */
  public ValidatorBuilder validator(Validator validator)
  {
    this.validator.addValidator(validator);
    return this;
  }

  /**
   * Gets the validator built from the previous method calls.
   *
   * @return The validator.
   */
  public Validator validator()
  {
    return validator;
  }
}
