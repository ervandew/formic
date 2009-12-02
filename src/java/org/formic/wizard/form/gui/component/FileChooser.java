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
package org.formic.wizard.form.gui.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

import org.apache.commons.lang.StringUtils;

import org.formic.Installer;

/**
 * Component consisting of a text field and a button which launches a
 * JFileChooser which populates the text field with the chosen entry.
 *
 * @author Eric Van Dewoestine
 */
public class FileChooser
  extends JPanel
{
  private JTextField textField;
  private JButton button;
  private JFileChooser chooser;

  /**
   * Creates a new instance.
   */
  public FileChooser()
  {
    this(JFileChooser.FILES_AND_DIRECTORIES, null);
  }

  /**
   * Creates a new instance.
   */
  public FileChooser(int selectionMode)
  {
    this(selectionMode, null);
  }

  /**
   * Creates a new instance.
   */
  public FileChooser(int selectionMode, String choosable)
  {
    super(new BorderLayout());
    chooser = new JFileChooser(){
      // force "proper" behavior of <enter> when a button has focus
      protected boolean processKeyBinding(
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
    chooser.setFileSelectionMode(selectionMode);
    addChoosableFileFilters(choosable);

    textField = new JTextField();
    button = new JButton(Installer.getString("browse.text"));

    button.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
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
  public JTextField getTextField()
  {
    return textField;
  }

  /**
   * Gets the button which launches the JFileChooser.
   *
   * @return The button.
   */
  public JButton getButton()
  {
    return button;
  }

  /**
   * Gets the JFileChooser.
   *
   * @return The JFileChooser.
   */
  public JFileChooser getFileChooser()
  {
    return chooser;
  }

  /**
   * {@inheritDoc}
   * @see javax.swing.JComponent#grabFocus()
   */
  public void grabFocus()
  {
    textField.grabFocus();
  }

  /**
   * Add the list of choosable file patterns for this file chooser.
   * Eg. addChoosableFileFilters("jpg,png,gif")
   *
   * @param choosable The choosable string.
   */
  private void addChoosableFileFilters(String choosable)
  {
    if(choosable != null){
      String[] values = StringUtils.split(choosable, ',');
      for (int ii = 0; ii < values.length; ii++){
        String[] value = StringUtils.split(values[ii].trim(), ':');
        if(value.length > 1){
          chooser.addChoosableFileFilter(new ExtensionFileFilter(value[0], value[1]));
        }else{
          chooser.addChoosableFileFilter(new ExtensionFileFilter(value[0]));
        }
      }
    }
  }

  /**
   * Implemention of {@link FileFilter} that filters by extension.
   */
  private static class ExtensionFileFilter
    extends FileFilter
  {
    private String ext;
    private String desc;

    /**
     * Constructs a new instance.
     *
     * @param ext The ext for this instance.
     */
    public ExtensionFileFilter(String ext)
    {
      this(ext, null);
    }

    /**
     * Constructs a new instance.
     *
     * @param ext The ext for this instance.
     * @param desc The desc for this instance.
     */
    public ExtensionFileFilter(String ext, String desc)
    {
      this.ext = ext;
      this.desc = desc;
    }

    /**
     * {@inheritDoc}
     * @see FileFilter#accept(File)
     */
    public boolean accept(File f)
    {
      return f.isDirectory() || ext.equalsIgnoreCase(
          FilenameUtils.getExtension(f.getAbsolutePath()));
    }

    /**
     * {@inheritDoc}
     * @see FileFilter#getDescription()
     */
    public String getDescription()
    {
      return desc;
    }
  }
}
