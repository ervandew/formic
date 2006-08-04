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
package org.formic.form.gui;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Component consisting of a text field and a button which launches a
 * JFileChooser which populates the text field with the chosen entry.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class GuiFileChooser
  extends JPanel
{
  private JTextField textField;
  private JButton button;
  private JFileChooser chooser;

  /**
   * Gets the text field to hold the entry chosen from the JFileChooser.
   *
   * @return The text field.
   */
  public JTextField getTextField ()
  {
    return textField;
  }

  /**
   * Gets the button which launches the JFileChooser.
   *
   * @return The button.
   */
  public JButton getButton ()
  {
    return button;
  }

  /**
   * Gets the JFileChooser.
   *
   * @return The JFileChooser.
   */
  public JFileChooser getFileChooser ()
  {
    return chooser;
  }
}
