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
package org.formic.wizard.impl.gui;

import javax.swing.JButton;

import org.formic.util.event.gui.ButtonKeyListener;

import org.pietschy.wizard.Wizard;

/**
 * Extension to default button bar.
 *
 * @author Eric Van Dewoestine
 */
public class ButtonBar
  extends org.pietschy.wizard.ButtonBar
{
  private JButton previousButton;
  private JButton nextButton;
  private JButton cancelButton;
  private JButton finishButton;

  /**
   * @see org.pietschy.wizard.ButtonBar#ButtonBar(Wizard)
   */
  public ButtonBar(Wizard wizard)
  {
    super(wizard);
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.ButtonBar#layoutButtons(JButton,JButton,JButton,JButton,JButton,JButton,JButton)
   */
  protected void layoutButtons(
      JButton help,
      JButton previous, JButton next,
      JButton last, JButton finish,
      JButton cancel, JButton close)
  {
    super.layoutButtons(help, previous, next, last, finish, cancel, close);
    this.previousButton = previous;
    previousButton.addKeyListener(ButtonKeyListener.getInstance());

    this.nextButton = next;
    nextButton.addKeyListener(ButtonKeyListener.getInstance());

    this.finishButton = finish;
    finishButton.addKeyListener(ButtonKeyListener.getInstance());

    this.cancelButton = cancel;
    cancelButton.addKeyListener(ButtonKeyListener.getInstance());
  }

  /**
   * Gets the previousButton for this instance.
   *
   * @return The previousButton.
   */
  public JButton getPreviousButton()
  {
    return this.previousButton;
  }

  /**
   * Gets the nextButton for this instance.
   *
   * @return The nextButton.
   */
  public JButton getNextButton()
  {
    return this.nextButton;
  }

  /**
   * Gets the cancelButton for this instance.
   *
   * @return The cancelButton.
   */
  public JButton getCancelButton()
  {
    return this.cancelButton;
  }

  /**
   * Gets the finishButton for this instance.
   *
   * @return The finishButton.
   */
  public JButton getFinishButton()
  {
    return this.finishButton;
  }
}
