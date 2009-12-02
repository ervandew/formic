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
package org.formic.util.event.console;

import charva.awt.event.KeyEvent;
import charva.awt.event.KeyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KeyListener for enabling usage of space bar for selecting a radio button like
 * the gui version supports.
 * <p/>
 * Charva currenlty does not fire KeyEvents for space, so will revisit this
 * later.
 *
 * @author Eric Van Dewoestine
 */
public class RadioButtonKeyListener
  implements KeyListener
{
  private static final Logger logger =
    LoggerFactory.getLogger(RadioButtonKeyListener.class);

  private static final RadioButtonKeyListener INSTANCE =
    new RadioButtonKeyListener();

  private RadioButtonKeyListener()
  {
  }

  /**
   * Gets the instance of this RadioButtonKeyListener.
   *
   * @return The RadioButtonKeyListener instance.
   */
  public static RadioButtonKeyListener getInstance()
  {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   * @see KeyListener#keyPressed(KeyEvent)
   */
  public void keyPressed(KeyEvent evt)
  {
    logger.info("### key pressed char = '" + evt.getKeyChar() + "'");
    logger.info("### key pressed code = '" + evt.getKeyCode() + "'");
  }

  /**
   * {@inheritDoc}
   * @see KeyListener#keyTyped(KeyEvent)
   */
  public void keyTyped(KeyEvent evt)
  {
  }

  /**
   * {@inheritDoc}
   * @see KeyListener#keyReleased(KeyEvent)
   */
  public void keyReleased(KeyEvent evt)
  {
    logger.info("### key released char = '" + evt.getKeyChar() + "'");
    logger.info("### key released code = '" + evt.getKeyCode() + "'");
  }
}
