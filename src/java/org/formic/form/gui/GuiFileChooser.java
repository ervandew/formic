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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.formic.Installer;

import org.formic.form.Validator;

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
   * Creates a new instance.
   *
   * @param factory The gui factory.
   * @param name The field name.
   * @param validator Validator used to validate the field.
   */
  public GuiFileChooser (
      GuiComponentFactory factory, String name, Validator validator)
  {
    super(new BorderLayout());
    chooser = new JFileChooser(){
      // force "proper" behavior of <enter> when a button has focus
      protected boolean processKeyBinding (
        KeyStroke key, KeyEvent event, int condition, boolean pressed)
      {
        if(event.getKeyCode() == KeyEvent.VK_ENTER){
          Component focusOwner = KeyboardFocusManager
            .getCurrentKeyboardFocusManager().getFocusOwner();
          // if a button has focus, click it.
          if(focusOwner instanceof JButton){
            ((JButton)focusOwner).doClick();
            return true;
          }
        }
        return super.processKeyBinding(key, event, condition, pressed);
      }
    };
    textField = factory.createTextField(name, validator);
    button = new JButton(Installer.getString("browse.text"));

    button.addActionListener(new ActionListener(){
      public void actionPerformed (ActionEvent event){
        int result = chooser.showOpenDialog(getParent());
        if(result == JFileChooser.APPROVE_OPTION){
          textField.setText(chooser.getSelectedFile().getPath());
        }
      }
    });
    add(textField, BorderLayout.CENTER);
    add(button, BorderLayout.EAST);
  }

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

  /**
   * {@inheritDoc}
   * @see javax.swing.JComponent#grabFocus()
   */
  public void grabFocus ()
  {
    textField.grabFocus();
  }
}
