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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.formic.wizard.form.Validator;

/**
 * Aggregates one or more validators.
 *
 * @author Eric Van Dewoestine
 */
public class AggregateValidator
  implements Validator
{
  private List validators = new ArrayList();
  private String message;

  /**
   * Adds the supplied validator to this aggregate.
   *
   * @param validator The validator to add.
   */
  public void addValidator(Validator validator)
  {
    validators.add(validator);
  }

  /**
   * {@inheritDoc}
   * @see Validator#isValid(Object)
   */
  public boolean isValid(Object value)
  {
    for (Iterator ii = validators.iterator(); ii.hasNext();){
      Validator validator = (Validator)ii.next();
      if(!validator.isValid(value)){
        message = validator.getErrorMessage();
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * @see Validator#getErrorMessage()
   */
  public String getErrorMessage()
  {
    return message;
  }

  /**
   * Determines if this aggregate contains the supplied validator.
   *
   * @param validator The validator to test for.
   * @return true if this aggregate contains the supplied validator, false
   * otherwise.
   */
  public boolean containsValidator(Validator validator)
  {
    if(validators.contains(validator)){
      return true;
    }

    for (Iterator ii = validators.iterator(); ii.hasNext();){
      Object val = ii.next();
      if(val instanceof AggregateValidator){
        if(((AggregateValidator)val).containsValidator(validator)){
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Determines if this aggregate contains the supplied validator.
   *
   * @param validator The validator to test for.
   * @return true if this aggregate contains the supplied validator, false
   * otherwise.
   */
  public boolean containsValidator(Class validator)
  {
    for (Iterator ii = validators.iterator(); ii.hasNext();){
      Object val = ii.next();
      if(val instanceof AggregateValidator){
        if(((AggregateValidator)val).containsValidator(validator)){
          return true;
        }
      }else if(val.getClass().equals(validator)){
        return true;
      }
    }
    return false;
  }
}
