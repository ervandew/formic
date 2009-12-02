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
package org.formic.wizard.impl.console;

import org.pietschy.wizard.WizardStep;

/**
 * Action listener for 'Finish' button.
 *
 * @author Eric Van Dewoestine
 */
public class FinishAction
  extends WizardAction
{
  /**
   * @see WizardAction#WizardAction(ConsoleWizard)
   */
  public FinishAction(ConsoleWizard wizard)
  {
    super(wizard);
  }

  /**
   * {@inheritDoc}
   * @see WizardAction#doAction()
   */
  public void doAction()
    throws Exception
  {
    WizardStep finishStep = getWizard().getModel().getActiveStep();
    finishStep.applyState();

    getWizard().close(false);
  }
}
