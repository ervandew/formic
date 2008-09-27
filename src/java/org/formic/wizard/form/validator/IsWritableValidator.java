/**
 * Copyright (c) 2005 - 2008
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.formic.wizard.form.validator;

import java.io.File;

import org.formic.wizard.form.Validator;

/**
 * Validator that determines if the user has write permissions to the value
 * which should be a valid file or directory path.
 *
 * @author Eric Van Dewoestine
 * @version $Revision$
 */
public class IsWritableValidator
  implements Validator
{
  public static final IsWritableValidator INSTANCE = new IsWritableValidator();

  private static final String MESSAGE = "validator.iswritable";

  /**
   * Prevent construction.
   */
  private IsWritableValidator ()
  {
  }

  /**
   * {@inheritDoc}
   * @see Validator#isValid(Object)
   */
  public boolean isValid (Object value)
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
  public String getErrorMessage ()
  {
    return MESSAGE;
  }
}
