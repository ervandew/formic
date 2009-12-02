/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008 Eric Van Dewoestine
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
package org.formic.wizard.form;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.formic.Installer;

import org.formic.wizard.form.gui.binding.TextComponentBinding;
import org.formic.wizard.form.gui.binding.ToggleButtonBinding;

/**
 * Object which binds swing / awt fields to form for persistance and validation
 * of the data.
 *
 * @author Eric Van Dewoestine
 */
public class GuiForm
  extends Form
  implements PropertyChangeListener
{
  private JPanel messagePanel;
  private JLabel messageLabel;
  private JLabel messageArea;

  private Icon infoIcon;
  private Icon errorIcon;
  private Icon warningIcon;

  public GuiForm()
  {
    // listen for change of focus on fields.
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addPropertyChangeListener(this);
  }

  /**
   * {@inheritDoc}
   * @see Form#setValue(FormField,JComponent,String,boolean)
   */
  public void setValue(
      FormField field, JComponent component, Object value, boolean valid)
  {
    super.setValue(field, component, value, valid);
    // May result in the last field's hint being displayed even if not desired.
    //focusField(component);
  }

  /**
   * Binds the supplied JTextComponent to this form.
   *
   * @param name The name of the field.
   * @param field The field to bind.
   */
  public void bind(String name, JTextComponent field)
  {
    bind(name, field, null);
  }

  /**
   * Binds the supplied JTextComponent to this form.
   *
   * @param name The name of the field.
   * @param field The field to bind.
   * @param validator The validator to use to validate the contents of the
   * field.
   */
  public void bind(String name, JTextComponent field, Validator validator)
  {
    ValidationUtils.decorate(field, validator);
    field.putClientProperty(NAME, name);
    addField(TextComponentBinding.bind(field, this),
        !ValidationUtils.isRequired(field));
  }

  /**
   * Binds the supplied JToggleButton to this form.
   *
   * @param name The name of the field.
   * @param field The field to bind.
   */
  public void bind(String name, JToggleButton field)
  {
    field.putClientProperty(NAME, name);
    addField(ToggleButtonBinding.bind(field, this), true);
  }

  /**
   * Creates a message panel for displaying field info, warnings, or errors.
   */
  public Component createMessagePanel()
  {
    messageArea = new JLabel();
    messageLabel = new JLabel();
    messageArea.setVisible(false);
    messageLabel.setVisible(false);

    messagePanel = new JPanel(new MigLayout());
    messagePanel.add(messageLabel);
    messagePanel.add(messageArea);

    int width = Installer.getDimension().width;
    messageArea.setPreferredSize(new Dimension(width - 75 - 16, 30));

    Dimension size = new Dimension(width - 75, 30);
    messagePanel.setPreferredSize(size);
    messagePanel.setMinimumSize(size);
    messagePanel.setMaximumSize(size);

    return messagePanel;
  }

  /**
   * Shows the associated hint for the supplied component.
   *
   * @param component The component.
   */
  public void showHint(JComponent component)
  {
    String name = (String)component.getClientProperty(NAME);
    if(name != null){
      showInfoMessage(Installer.getString(name + ".hint"));
    }
  }

  /**
   * Displays an info message to the user in the message panel if the message
   * panel is enabled.
   *
   * @param text The text to display.
   */
  public void showInfoMessage(String text)
  {
    if(infoIcon == null){
      infoIcon = new ImageIcon(Installer.getImage("form.info.icon"));
    }
    showMessage(text, infoIcon);
  }

  /**
   * Displays a warning message to the user in the message panel if the message
   * panel is enabled.
   *
   * @param text The text to display.
   */
  public void showWarningMessage(String text)
  {
    if(warningIcon == null){
      warningIcon = new ImageIcon(Installer.getImage("form.warning.icon"));
    }
    showMessage(text, warningIcon);
  }

  /**
   * Displays an error message to the user in the message panel if the message
   * panel is enabled.
   *
   * @param text The text to display.
   */
  public void showErrorMessage(String text)
  {
    if(errorIcon == null){
      errorIcon = new ImageIcon(Installer.getImage("form.error.icon"));
    }
    showMessage(text, errorIcon);
  }

  /**
   * Displays a message to the user in the message panel if the message panel is
   * enabled.
   *
   * @param text The text to display.
   * @param icon The icon to display.
   */
  public void showMessage(String text, Icon icon)
  {
    if(messagePanel != null){
      if(text != null && text.trim().length() > 0){
        messageLabel.setIcon(icon);
        messageArea.setText("<html>" + text + "</html>");
        messageLabel.setVisible(true);
        messageArea.setVisible(true);
      }else{
        messageLabel.setVisible(false);
        messageArea.setVisible(false);
      }
    }
  }

  /**
   * Fired when focus changes from one component to another.
   *
   * @param event The property change event.
   */
  public void propertyChange(PropertyChangeEvent event)
  {
    String propertyName = event.getPropertyName();
    if (!"permanentFocusOwner".equals(propertyName)){
      return;
    }

    Component focusOwner = KeyboardFocusManager
      .getCurrentKeyboardFocusManager().getFocusOwner();
    if(focusOwner == null || focusOwner instanceof JButton){
      return;
    }

    if(focusOwner instanceof JComponent){
      focusField((JComponent)focusOwner);
    }
  }

  /**
   * Invoked to update hints / error for the focused field.
   *
   * @param component The focused component.
   */
  public void focusField(JComponent component)
  {
    String error = ValidationUtils.getValidationError(component);
    if(error != null){
      String name = (String)component.getClientProperty(NAME);
      showErrorMessage(Installer.getString(error, Installer.getString(name)));
    }else{ // standard hint.
      showHint(component);
    }
  }
}
