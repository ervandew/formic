package org.formic.wizard.impl.gui;

import java.awt.event.ActionEvent;

import org.formic.Installer;

import org.formic.wizard.gui.dialog.Dialogs;

import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardAction;
import org.pietschy.wizard.WizardStep;

/**
 * Action for canceling the installation.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class CancelAction
  extends WizardAction
{
  protected CancelAction(Wizard model)
  {
    super("cancel", model);
  }

  /**
   * {@inheritDoc}
   * @see WizardAction#doAction(ActionEvent)
   */
  public void doAction (ActionEvent e)
  {
    if(isEnabled()){
      if(Dialogs.showConfirm("quit.confirm.title", "quit.confirm.text")){
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
