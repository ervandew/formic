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

import java.beans.PropertyChangeEvent;

import java.util.Iterator;

import javax.swing.JPanel;

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
  private boolean manditoryBackground;
  private boolean manditoryBorder;
  private boolean invalidBackground;

  private ValidationResult validationResult = new ValidationResult();

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
    this.manditoryBorder = manditoryBorder;
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
}
