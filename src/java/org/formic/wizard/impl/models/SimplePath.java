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
package org.formic.wizard.impl.models;

import org.pietschy.wizard.WizardStep;

/**
 * Extension to original SimplePath that has support for a name property for
 * debugging purposes.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class SimplePath
  extends org.pietschy.wizard.models.SimplePath
{
  private String name;

  /**
   * Creates a new SimplePath with the specified name.
   *
   * @param name the name to give this path.
   */
  public SimplePath (String name)
  {
    super();
    this.name = name;
  }

  /**
   * Creates a new SimplePath with the specified name and adds the specified
   * step.
   *
   * @param name the name to give this path.
   * @param step the first step of the path.
   */
  public SimplePath (String name, WizardStep step)
  {
    super(step);
    this.name = name;
  }

  /**
   * Gets the name given to this path.
   *
   * @return The name.
   */
  public String getName ()
  {
    return name;
  }

  /**
   * {@inheritDoc}
   * @see Object#toString()
   */
  public String toString ()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Name: ").append(name);

    if(getSteps().size() > 0){
      buffer.append(" Steps: ");
      for (int ii = 0; ii < getSteps().size(); ii++){
        if(ii > 0){
          buffer.append(", ");
        }
        WizardStep step = (WizardStep)getSteps().get(ii);
        buffer.append(step.getName());
      }
    }

    return buffer.toString();
  }
}
