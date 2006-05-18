package org.formic.wizard.impl.gui;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.formic.Installer;

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
      int result = JOptionPane.showConfirmDialog(
          wizard,
          Installer.getString("quit.confirm.text"),
          Installer.getString("quit.confirm.title"),
          JOptionPane.YES_NO_OPTION);
      if(result == JOptionPane.OK_OPTION){
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
