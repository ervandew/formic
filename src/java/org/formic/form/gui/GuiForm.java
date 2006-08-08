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

import java.awt.Component;
import java.awt.KeyboardFocusManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;

import com.jgoodies.validation.message.SimpleValidationMessage;

import com.jgoodies.validation.view.ValidationComponentUtils;

import org.formic.Installer;

import org.formic.form.Form;
import org.formic.form.FormFieldModel;
import org.formic.form.FormModel;

/**
 * Implementation of {@link Form} for graphical interfaces.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class GuiForm
  extends JPanel
  implements Form, FormModel.FormFieldListener
{
  private FormModel model;

  private JPanel contentPanel;
  private JPanel messagePanel;
  private JLabel messageLabel;
  private JLabel messageArea;

  private Icon infoIcon;
  private Icon errorIcon;
  private Icon warningIcon;

  private boolean manditoryBackground;
  private boolean invalidBackground;

  private ValidationResult validationResult = new ValidationResult();

  /**
   * Constructs a new instance.
   *
   */
  public GuiForm ()
  {
    FormLayout layout =
      new FormLayout("fill:pref:grow", "15dlu, 5dlu, fill:pref:grow");
    PanelBuilder builder = new PanelBuilder(layout, this);

    this.contentPanel = new JPanel();
    this.messagePanel = createMessagePanel();

    builder.setDefaultDialogBorder();
    builder.add(messagePanel);
    builder.nextLine();
    builder.nextLine();
    builder.add(contentPanel);

    // listen for focus events and display tips if any.
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addPropertyChangeListener(new FocusChangeHandler());
  }

  /**
   * {@inheritDoc}
   * @see Form#getModel()
   */
  public FormModel getModel ()
  {
    return model;
  }

  /**
   * {@inheritDoc}
   * @see Form#setModel(FormModel)
   */
  public void setModel (FormModel model)
  {
    this.model = model;
    model.addFormFieldListener(this);
  }

  /**
   * Displays an info message to the user in the message panel if the message
   * panel is enabled.
   *
   * @param text The text to display.
   */
  public void showInfoMessage (String text)
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
  public void showWarningMessage (String text)
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
  public void showErrorMessage (String text)
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
  public void showMessage (String text, Icon icon)
  {
    messageLabel.setIcon(icon);
    messageArea.setText(text);
    messagePanel.setVisible(text != null && text.trim().length() > 0);
  }

  /**
   * Gets the contentPanel for this instance.
   *
   * @return The contentPanel.
   */
  public JPanel getContentPanel ()
  {
    return this.contentPanel;
  }

  /**
   * Creates the message panel.
   */
  private JPanel createMessagePanel ()
  {
    messageArea = new JLabel();
    messageLabel = new JLabel();

    FormLayout layout = new FormLayout("pref, 2dlu, default", "pref");
    PanelBuilder builder = new PanelBuilder(layout);
    CellConstraints cc = new CellConstraints();
    builder.add(messageLabel, cc.xy(1, 1));
    builder.add(messageArea,  cc.xy(3, 1));

    messagePanel = builder.getPanel();
    messagePanel.setVisible(false);

    return messagePanel;
  }

  /**
   * Sets whether or not to visually mark required fields with a background
   * color.
   *
   * @param manditoryBackground true to mark required fields, false otherwise.
   */
  public void setManditoryBackground (boolean manditoryBackground)
  {
    this.manditoryBackground = manditoryBackground;
    if(manditoryBackground){
      ValidationComponentUtils
        .updateComponentTreeMandatoryAndBlankBackground(this);
    }
  }

  /**
   * Sets whether or not to visually mark required fields with a colored border.
   *
   * @param manditoryBorder true to mark required fields, false otherwise.
   */
  public void setManditoryBorder (boolean manditoryBorder)
  {
    if(manditoryBorder){
      ValidationComponentUtils
        .updateComponentTreeMandatoryBorder(this);
    }
  }

  /**
   * Sets whether or not to visually mark invalid fields with a background
   * color.
   *
   * @param invalidBackground true to mark invalid fields, false otherwise.
   */
  public void setInvalidBackground (boolean invalidBackground)
  {
    this.invalidBackground = invalidBackground;
    if(invalidBackground){
      ValidationComponentUtils
        .updateComponentTreeSeverityBackground(this, validationResult);
    }
  }

  /**
   * {@inheritDoc}
   * @see FormModel.FormFieldListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange (PropertyChangeEvent evt)
  {
    if(evt.getSource() instanceof FormFieldModel){
      // update manditory background
      setManditoryBackground(manditoryBackground);

      // update invalid background
      if(FormFieldModel.VALID.equals(evt.getPropertyName())){
        FormFieldModel field = (FormFieldModel)evt.getSource();
        ValidationMessage message = null;
        if(Boolean.FALSE == evt.getNewValue()){
          String key = field.getValidator().getErrorMessage();
          String display = Installer.getString(field.getName(), field.getName());
          message = new SimpleValidationMessage(
              Installer.getString(key, (Object)display),
              Severity.ERROR, field.getName());

          validationResult.add(message);
        }else{
          removeMessages(field.getName());
        }
        setInvalidBackground(invalidBackground);
      }
    }
  }

  /**
   * Since jgoodies ValidationResult does not support removal of keys, we must
   * create a new instance.
   *
   * @param key The key to the messages to be removed.
   */
  private void removeMessages (String key)
  {
    ValidationResult result = new ValidationResult();
    for (Iterator ii = validationResult.getMessages().iterator(); ii.hasNext();){
      ValidationMessage message = (ValidationMessage)ii.next();
      if(!key.equals(message.key())){
        result.add(message);
      }
    }
    this.validationResult = result;
  }

  /**
   * Displays an input hint for components that get the focus permanently.
   */
  private final class FocusChangeHandler
    implements PropertyChangeListener
  {
    public void propertyChange (PropertyChangeEvent _event)
    {
      String propertyName = _event.getPropertyName();
      if (!"permanentFocusOwner".equals(propertyName)){
        return;
      }

      Component focusOwner = KeyboardFocusManager
        .getCurrentKeyboardFocusManager().getFocusOwner();
      if(focusOwner == null){
        return;
      }

      // ignore buttons
      if (focusOwner instanceof JButton){
        return;
      }

      String focusHint = null;
      if(focusOwner instanceof JComponent){
        focusHint = (String)
          ValidationComponentUtils.getMessageKey((JComponent)focusOwner);
        focusHint = Installer.getString(focusHint + ".hint");
      }

      showInfoMessage(focusHint);
    }
  }
}
