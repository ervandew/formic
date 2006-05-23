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
package org.formic.wizard.impl.console;

/**
 * Action listener for 'Previous' button.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class PreviousAction
  extends WizardAction
{
  /**
   * @see WizardAction#WizardAction(ConsoleWizard)
   */
  public PreviousAction (ConsoleWizard wizard)
  {
    super(wizard);
  }

  /**
   * {@inheritDoc}
   * @see WizardAction#doAction()
   */
  public void doAction ()
    throws Exception
  {
    getWizard().getModel().previousStep();
  }
}
