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

import java.util.Stack;

import org.pietschy.wizard.WizardStep;

import org.pietschy.wizard.models.Path;
import org.pietschy.wizard.models.SimplePath;

/**
 * Extension to original MultiPathModel that supports paths containing no steps.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class MultiPathModel
  extends org.pietschy.wizard.models.MultiPathModel
{
  private Stack history = new Stack();
  private Path firstPath = null;

  public MultiPathModel(Path firstPath)
  {
    super(firstPath);
    this.firstPath = firstPath;
  }

  /**
   * {@inheritDoc}
   */
  public void nextStep()
  {
    WizardStep currentStep = getActiveStep();
    Path currentPath = getPathForStep(currentStep);

// CHANGE
// NEW CODE
    if (currentPath.getSteps().size() == 0 ||
        currentPath.isLastStep(currentStep))
    {
      Path nextPath = getNextPath(currentPath);
      while(nextPath.getSteps().size() == 0){
        nextPath = getNextPath(nextPath);
      }
      setActiveStep(nextPath.firstStep());
    }
// OLD CODE
    /*if (currentPath.isLastStep(currentStep))
    {
      Path nextPath = currentPath.getNextPath(this);
      setActiveStep(nextPath.firstStep());
    }*/
// END CHANGE
    else
    {
      setActiveStep(currentPath.nextStep(currentStep));
    }

    history.push(currentStep);
  }

  /**
   * Gets the next path.
   */
  private Path getNextPath (Path path)
  {
    if(path instanceof SimplePath){
      return ((SimplePath)path).getNextPath();
    }
    return ((BranchingPath)path).getNextPath(this);
  }

  /**
   * {@inheritDoc}
   */
  public void previousStep()
  {
    WizardStep step = (WizardStep) history.pop();
    setActiveStep(step);
  }

  /**
   * {@inheritDoc}
   */
  public void lastStep()
  {
    history.push(getActiveStep());
    WizardStep lastStep = getLastPath().lastStep();
    setActiveStep(lastStep);
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardModel#isLastVisible()
   */
  public boolean isLastVisible ()
  {
    return false;
  }

  /**
   * Determines if the supplied step is the first step.
   *
   * @param step The step.
   * @return true if the first step, false otherwise.
   */
  public boolean isFirstStep (WizardStep step)
  {
    Path path = getPathForStep(step);
    return path.equals(getFirstPath()) && path.isFirstStep(step);
  }

  /**
   * {@inheritDoc}
   */
  public void reset()
  {
    history.clear();
    WizardStep firstStep = firstPath.firstStep();
    setActiveStep(firstStep);
    history.push(firstStep);
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.AbstractWizardModel#setPreviousAvailable(boolean)
   */
  public void setPreviousAvailable (boolean available)
  {
    super.setPreviousAvailable(available);
  }
}
