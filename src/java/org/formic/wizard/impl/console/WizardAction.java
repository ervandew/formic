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

import charva.awt.event.ActionEvent;
import charva.awt.event.ActionListener;

import org.formic.util.dialog.console.ConsoleDialogs;

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
      ConsoleDialogs.showError(e);
    }
  }

  /**
   * Perform the action.
   */
  protected abstract void doAction ()
    throws Exception;
}
