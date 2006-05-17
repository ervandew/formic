/**
 * Formic installer framework.
 * Copyright (C) 2004 - 2006  Eric Van Dewoestine
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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;

import org.formic.Log;

import org.pietschy.wizard.WizardStep;

import org.pietschy.wizard.models.Condition;
import org.pietschy.wizard.models.MultiPathModel;
import org.pietschy.wizard.models.Path;
import org.pietschy.wizard.models.PathVisitor;

/**
 * Extension to original BranchingPath that uses an internal insertion ordered
 * map for storing paths, and has support for a name property for debugging
 * purposes.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class BranchingPath
  extends org.pietschy.wizard.models.BranchingPath
{
  private ListOrderedMap paths = new ListOrderedMap();
  private String name;

  /**
   * Creates a new empty BranchingPath with the specified name.
   *
   * @param name the name to give this path.
   */
  public BranchingPath(String name)
  {
    super();
    this.name = name;
  }

  /**
   * Creates a new BranchingPath with the specified name and adds the specified
   * step.
   *
   * @param name the name to give this path.
   * @param step the first step of the path.
   */
  public BranchingPath(String name, WizardStep step)
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
   */
  public Path getNextPath(MultiPathModel model)
  {
    for (Iterator iter = paths.entrySet().iterator(); iter.hasNext();)
    {
      Map.Entry entry = (Map.Entry) iter.next();
      Condition condition = (Condition) entry.getKey();
      Path path = (Path)entry.getValue();
      if (condition.evaluate(model)){
        Log.debug("Condition for branch path '" + getPathName(path) +
            "' evaluated to true.");
        return path;
      }else{
        Log.debug("Condition for branch path '" + getPathName(path) +
            "' evaluated to false.");
      }
    }

    throw new IllegalStateException("No next path selected");
  }

  /**
   * {@inheritDoc}
   */
  public void addBranch(Path path, Condition condition)
  {
    paths.put(condition, path);
  }

  /**
   * Adds the specified path and condition at the supplied index.
   *
   * @param index The index to add the new path at.
   * @param path The path to add.
   * @param condition The condition for the path.
   */
  public void addBranch(int index, Path path, Condition condition)
  {
    paths.put(index, condition, path);
  }

  /**
   * {@inheritDoc}
   */
  public void visitBranches(PathVisitor visitor)
  {
    for (Iterator iter = paths.values().iterator(); iter.hasNext();){
      Path path = (Path)iter.next();
      path.acceptVisitor(visitor);
    }
  }

  /**
   * Gets the name of the supplied path.
   *
   * @param path The path.
   * @return The name.
   */
  private String getPathName (org.pietschy.wizard.models.Path path)
  {
    if(path instanceof SimplePath){
      return ((SimplePath)path).getName();
    }
    return ((BranchingPath)path).getName();
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

    if(paths.size() > 0){
      buffer.append(" Paths: ");
      StringBuffer pathNames = new StringBuffer();
      for (Iterator ii = paths.values().iterator(); ii.hasNext();){
        if(pathNames.length() > 0){
          pathNames.append(", ");
        }
        String pathName = null;
        Object path = ii.next();
        if(path instanceof SimplePath){
          pathName = ((SimplePath)path).getName();
        }else{
          pathName = ((BranchingPath)path).getName();
        }
        pathNames.append(pathName);
      }
      buffer.append(pathNames);
    }

    return buffer.toString();
  }
}
