/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2011  Eric Van Dewoestine
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
package org.formic.wizard.impl.gui;

import java.awt.event.ActionEvent;

import org.formic.util.dialog.gui.GuiDialogs;

import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardAction;
import org.pietschy.wizard.WizardStep;

/**
 * Action for canceling the installation.
 *
 * @author Eric Van Dewoestine
 */
public class CancelAction
  extends WizardAction
{
  private static final long serialVersionUID = 1L;

  protected CancelAction(Wizard model)
  {
    super("cancel", model);
  }

  /**
   * {@inheritDoc}
   * @see WizardAction#doAction(ActionEvent)
   */
  public void doAction(ActionEvent e)
  {
    if(isEnabled()){
      if(GuiDialogs.showConfirm("quit.confirm.title", "quit.confirm.text")){
        getWizard().cancel();
      }
    }else{
      getWizard().cancel();
    }
  }

  /**
   * {@inheritDoc}
   * @see WizardAction#updateState()
   */
  protected void updateState()
  {
    WizardStep step = getWizard().getModel().getActiveStep();
    if(step != null){
      setEnabled(!getWizard().getModel().isLastStep(step));
    }
  }
}
