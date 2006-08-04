/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2006  Eric Van Dewoestine
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
package org.formic.event.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;

/**
 * KeyListener for buttons.
 * <p/>
 * Used to handle by passing default button if enter is pressed while on a
 * selected button.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class ButtonKeyListener
  implements KeyListener
{
  private static ButtonKeyListener INSTANCE = new ButtonKeyListener();

  private ButtonKeyListener ()
  {
  }

  /**
   * Gets the ButtonKeyListener instance
   *
   * @return The instance.
   */
  public static ButtonKeyListener getInstance ()
  {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   * @see KeyListener#keyTyped(KeyEvent)
   */
  public void keyTyped (KeyEvent e)
  {
  }

  /**
   * {@inheritDoc}
   * @see KeyListener#keyPressed(KeyEvent)
   */
  public void keyPressed (KeyEvent e)
  {
    if(e.getKeyCode() == KeyEvent.VK_ENTER){
      ((JButton)e.getSource()).doClick();

      // prevent default button from being called as well.
      e.consume();
    }
  }

  /**
   * {@inheritDoc}
   * @see KeyListener#keyReleased(KeyEvent)
   */
  public void keyReleased (KeyEvent e)
  {
  }
}
