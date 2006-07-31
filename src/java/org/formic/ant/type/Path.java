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
package org.formic.ant.type;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.taskdefs.Typedef;

/**
 * Represents a path in the installation process.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class Path
  extends Typedef
{
  private String name;
  private List steps = new ArrayList();

  /**
   * Gets the name of the path.
   *
   * @return The path name.
   */
  public String getName ()
  {
    return this.name;
  }

  /**
   * Sets the name of the path.
   *
   * @param name The path name.
   */
  public void setName (String name)
  {
    this.name = name;
  }

  /**
   * Adds the supplied step to this path.
   *
   * @param _step The step to add.
   */
  public void addConfiguredStep (Step _step)
  {
    steps.add(_step);
  }

  /**
   * Gets the configures steps.
   *
   * @return List of Step.
   */
  public List getSteps ()
  {
    return steps;
  }

  /**
   * Adds the supplied branch to this path.
   *
   * @param _branch The branch to add.
   */
  public void addConfiguredBranch (Branch _branch)
  {
    steps.add(_branch);
  }
}
