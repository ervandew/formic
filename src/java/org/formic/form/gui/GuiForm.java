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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

  private boolean mandatoryBorderEnabled;
  private boolean mandatoryBackgroundEnabled = true;
  private boolean invalidBackgroundEnabled = true;

  private ValidationResult validationResult = new ValidationResult();
  private List visited = new ArrayList();
  private String visitingField;
  private String currentField;

  /**
   * Constructs a new instance.
   *
   */
  public GuiForm ()
  {
    FormLayout layout =
      new FormLayout("fill:pref:grow", "12dlu, 3dlu, fill:pref:grow");
    PanelBuilder builder = new PanelBuilder(layout, this);

    builder.setDefaultDialogBorder();
    builder.add(createMessagePanel());
    builder.nextLine();
    builder.nextLine();
    builder.add(contentPanel = new JPanel());

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
    if(text != null && text.trim().length() > 0){
      messageLabel.setIcon(icon);
      messageArea.setText("<html>" + text + "</html>");
      messagePanel.setVisible(true);
    }else{
      messagePanel.setVisible(false);
    }
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
  private JComponent createMessagePanel ()
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
    messagePanel.setPreferredSize(new java.awt.Dimension(100, 100));

    return messagePanel;
  }

  /**
   * Gets whether or not to visually mark required fields with a background
   * color.
   *
   * @return The true if enabled, false otherwise.
   */
  public boolean isMandatoryBackgroundEnabled ()
  {
    return this.mandatoryBackgroundEnabled;
  }

  /**
   * Sets whether or not to visually mark required fields with a background
   * color.
   *
   * @param enabled true to mark required fields, false otherwise.
   */
  public void setMandatoryBackgroundEnabled (boolean enabled)
  {
    this.mandatoryBackgroundEnabled = enabled;
    if(mandatoryBackgroundEnabled){
      ValidationComponentUtils
        .updateComponentTreeMandatoryAndBlankBackground(this);
    }
  }

  /**
   * Gets whether or not to visually mark required fields with a colored border.
   *
   * @return The true if enabled, false otherwise.
   */
  public boolean isMandatoryBorderEnabled ()
  {
    return this.mandatoryBorderEnabled;
  }

  /**
   * Sets whether or not to visually mark required fields with a colored border.
   *
   * @param enabled true to mark required fields, false otherwise.
   */
  public void setMandatoryBorderEnabled (boolean enabled)
  {
    this.mandatoryBorderEnabled = enabled;
    if(mandatoryBorderEnabled){
      ValidationComponentUtils
        .updateComponentTreeMandatoryBorder(this);
    }
  }

  /**
   * Gets whether or not to visually mark invalid fields with a background
   * color.
   *
   * @return The true if enabled, false otherwise.
   */
  public boolean isInvalidBackgroundEnabled ()
  {
    return this.invalidBackgroundEnabled;
  }

  /**
   * Sets whether or not to visually mark invalid fields with a background
   * color.
   *
   * @param enabled true to mark invalid fields, false otherwise.
   */
  public void setInvalidBackgroundEnabled (boolean enabled)
  {
    this.invalidBackgroundEnabled = enabled;
    if(invalidBackgroundEnabled){
      ValidationResult result = validationResult;
      for (Iterator ii = getModel().getFieldModels().iterator(); ii.hasNext();){
        FormFieldModel field = (FormFieldModel)ii.next();
        if(!visited.contains(field.getName())){
          result = removeMessages(field.getName(), result);
        }else if(field.getValue() == null && field.getName().equals(visitingField)){
          result = removeMessages(field.getName(), result);
        }else if(field.getName().equals(currentField)){
          setInputHint(field.getName());
        }
      }
      ValidationComponentUtils
        .updateComponentTreeSeverityBackground(this, result);
    }
  }

  /**
   * {@inheritDoc}
   * @see FormModel.FormFieldListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange (PropertyChangeEvent evt)
  {
    if(evt.getSource() instanceof FormFieldModel){
      // update invalid background
      if(FormFieldModel.VALID.equals(evt.getPropertyName())){
        FormFieldModel field = (FormFieldModel)evt.getSource();
        ValidationMessage message = null;
        if(Boolean.FALSE == evt.getNewValue()){
          String key = field.getValidator().getErrorMessage();
          String display = Installer.getString(field.getName(), field.getName());
          message = new SimpleValidationMessage(
              Installer.getString(key, (Object)display, field.getValue()),
              Severity.ERROR, field.getName());

          if(!validationResult.contains(message)){
            validationResult = removeMessages(field.getName(), validationResult);
            validationResult.add(message);
            setInvalidBackgroundEnabled(isInvalidBackgroundEnabled());
          }
        }else{
          validationResult = removeMessages(field.getName(), validationResult);
          setInvalidBackgroundEnabled(isInvalidBackgroundEnabled());
        }
      }else{
        // update mandatory background
        if ((evt.getOldValue() == null ||
              evt.getOldValue().toString().trim().length() == 0) &&
            evt.getNewValue() != null){
          setMandatoryBackgroundEnabled(isMandatoryBackgroundEnabled());
        }
      }
    }
  }

  /**
   * Since jgoodies ValidationResult does not support removal of keys, we must
   * create a new instance.
   *
   * @param key The key to the messages to be removed.
   * @return The updated ValidationResult.
   */
  private ValidationResult removeMessages (
      String key, ValidationResult validationResult)
  {
    ValidationResult result = new ValidationResult();
    for (Iterator ii = validationResult.getMessages().iterator(); ii.hasNext();){
      ValidationMessage message = (ValidationMessage)ii.next();
      if(!key.equals(message.key())){
        result.add(message);
      }
    }
    return result;
  }

  /**
   * Sets the appropriate input hint for the field with the given name.
   *
   * @param name The field name.
   */
  private void setInputHint (String name)
  {
    // see if the field has an error to be displayed.
    ValidationResult result = validationResult.subResult(name);
    FormFieldModel field = getModel().getFieldModel(name);
    if (!result.isEmpty() &&
        (field.getValue() != null || !field.getName().equals(visitingField)))
    {
      StringBuffer buffer = new StringBuffer();
      for (Iterator ii = result.getMessages().iterator(); ii.hasNext();){
        if(buffer.length() != 0){
          buffer.append("  ");
        }
        ValidationMessage message = (ValidationMessage)ii.next();
        buffer.append(message.formattedText());
      }
      showErrorMessage(buffer.toString());
    }else{
      // standard hint.
      showInfoMessage(Installer.getString(name + ".hint"));
    }
  }

  /**
   * FocusChangeHandler for diplaying validation errors, input hints, and
   * tracking what fields have been visited.
   */
  private final class FocusChangeHandler
    implements PropertyChangeListener
  {
    public void propertyChange (PropertyChangeEvent event)
    {
      String propertyName = event.getPropertyName();
      if (!"permanentFocusOwner".equals(propertyName)){
        return;
      }

      Component focusOwner = KeyboardFocusManager
        .getCurrentKeyboardFocusManager().getFocusOwner();
      if(focusOwner == null){
        visitingField = null;
        return;
      }

      // ignore buttons
      if (focusOwner instanceof JButton){
        visitingField = null;
        return;
      }

      if(focusOwner instanceof JComponent){
        String key = (String)
          ValidationComponentUtils.getMessageKey((JComponent)focusOwner);

        if(key != null){
          // track what fields have been visited.
          if(!visited.contains(key)){
            visitingField = key;
            visited.add(key);
          }else{
            visitingField = null;
          }
          currentField = key;

          // update invalid backgrounds
          setInvalidBackgroundEnabled(isInvalidBackgroundEnabled());

          setInputHint(key);
        }
      }else{
        visitingField = null;
      }
    }
  }
}
