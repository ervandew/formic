package org.formic.wizard.impl.console;

import charva.awt.event.ActionEvent;
import charva.awt.event.ActionListener;

import org.formic.wizard.console.dialog.Dialogs;

/**
 * Abstract super class for wizard actions.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public abstract class WizardAction
  implements ActionListener
{
  private ConsoleWizard wizard;

  /**
   * Constructs a new instance.
   *
   * @param wizard The ConsoleWizard.
   */
  public WizardAction (ConsoleWizard wizard)
  {
    this.wizard = wizard;
  }

  /**
   * Gets the wizard for this instance.
   *
   * @return The wizard.
   */
  public ConsoleWizard getWizard ()
  {
    return this.wizard;
  }

  /**
   * {@inheritDoc}
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed (ActionEvent event)
  {
    try{
      doAction();
    }catch(Exception e){
      Dialogs.showError(e);
    }
  }

  /**
   * Perform the action.
   */
  protected abstract void doAction ()
    throws Exception;
}
