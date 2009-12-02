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
package org.formic.ant.type;

import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

import org.formic.Log;

/**
 * Represents a branch to another path if the child condition(s) evaluate to
 * true.
 *
 * @author Eric Van Dewoestine
 */
public class Branch
  extends ConditionBase
  implements Condition
{
  private String path;

  /**
   * Gets the path of the branch.
   *
   * @return The branch path.
   */
  public String getPath()
  {
    return this.path;
  }

  /**
   * Sets the path of the branch.
   *
   * @param path The branch path.
   */
  public void setPath(String path)
  {
    this.path = path;
  }

  /**
   * {@inheritDoc}
   * @see org.apache.tools.ant.taskdefs.condition.Condition#eval()
   */
  public boolean eval()
    throws BuildException
  {
    return ((Condition)getConditions().nextElement()).eval();
  }
}
