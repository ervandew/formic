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

import java.text.Format;
import java.text.NumberFormat;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import com.jgoodies.binding.adapter.BasicComponentFactory;

import com.jgoodies.binding.list.SelectionInList;

import com.jgoodies.validation.view.ValidationComponentUtils;

import org.formic.Installer;

import org.formic.form.FormFieldModel;
import org.formic.form.FormModel;
import org.formic.form.Validator;

import org.formic.form.impl.FormModelImpl;

/**
 * Factory for creating components for use in forms.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class GuiComponentFactory
{
  public static final String FORM_FIELD = "formic.form.field";

  private FormModel model;

  /**
   * Constructs a new instance using a default FormModel.
   *
   * @param name The form name (used as a prefix for field names and resource
   * keys).
   */
  public GuiComponentFactory (String name)
  {
    this.model = new FormModelImpl(name);
  }

  /**
   * Constructs a new instance.
   *
   * @param model The model for this instance.
   */
  public GuiComponentFactory (FormModel model)
  {
    this.model = model;
  }

  /**
   * Gets the underlying FormModel.
   *
   * @return The FormModel.
   */
  public FormModel getFormModel ()
  {
    return model;
  }

  /**
   * Creates a check box for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @return The JCheckBox.
   */
  public JCheckBox createCheckBox (String name, Validator validator)
  {
    return (JCheckBox)component(
      BasicComponentFactory.createCheckBox(
        getField(name, validator),
        Installer.getString(model.getName() + '.' + name, name)),
      name);
  }

  /**
   * Creates a combo box for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param list List of values.
   * @return The JComboBox.
   */
  public JComboBox createComboBox (
      String name, Validator validator, List list)
  {
    SelectionInList selection =
      new SelectionInList(list, getField(name, validator));
    return (JComboBox)component(
        BasicComponentFactory.createComboBox(selection), name);
  }

  /**
   * Creates a combo box for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param list List of values.
   * @param renderer Renderer for rendering list elements.
   * @return The JComboBox.
   */
  public JComboBox createComboBox (
      String name, Validator validator, List list, ListCellRenderer renderer)
  {
    SelectionInList selection =
      new SelectionInList(list, getField(name, validator));
    return (JComboBox)component(
        BasicComponentFactory.createComboBox(selection, renderer), name);
  }

  /**
   * Creates a formatted date field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createDateField (
      String name, Validator validator)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createDateField(getField(name, validator)), name);
  }

  /**
   * Creates a formatted text field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param format The format used to convert values to and from text.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createFormattedTextField (
      String name, Validator validator, Format format)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createFormattedTextField(
          getField(name, validator), format),
        name);
  }

  /**
   * Creates a formatted text field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param formatter The formatter used to convert values to and from text.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createFormattedTextField (
      String name,
      Validator validator,
      JFormattedTextField.AbstractFormatter formatter)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createFormattedTextField(
          getField(name, validator), formatter),
        name);
  }

  /**
   * Creates a formatted text field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param factory The factory that provides formatters used to convert values
   * to and from text.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createFormattedTextField (
      String name,
      Validator validator,
      JFormattedTextField.AbstractFormatterFactory factory)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createFormattedTextField(
          getField(name, validator), factory),
        name);
  }

  /**
   * Creates a formatted text field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param mask The mask pattern used to create an instance of MaskFormatter
   * which can convert values to and from text.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createFormattedTextField (
      String name, Validator validator, String mask)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createFormattedTextField(
          getField(name, validator), mask),
        name);
  }

  /**
   * Creates a formatted integer field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createIntegerField (
      String name, Validator validator)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createIntegerField(getField(name, validator)),
        name);
  }

  /**
   * Creates a formatted integer field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param emptyNumber Integer that represents the empty string.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createIntegerField (
      String name, Validator validator, int emptyNumber)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createIntegerField(
          getField(name, validator), emptyNumber),
        name);
  }

  /**
   * Creates a formatted integer field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param format Format used to convert numbers to and from strings.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createIntegerField (
      String name, Validator validator, NumberFormat format)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createIntegerField(
          getField(name, validator), format),
        name);
  }

  /**
   * Creates a formatted integer field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param format Format used to convert numbers to and from strings.
   * @param emptyNumber Integer that represents the empty string.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createIntegerField (
      String name, Validator validator, NumberFormat format, int emptyNumber)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createIntegerField(
          getField(name, validator), format, emptyNumber),
        name);
  }

  /**
   * Creates a formatted integer field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param format Format used to convert numbers to and from strings.
   * @param emptyNumber Integer that represents the empty string.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createIntegerField (
      String name,
      Validator validator,
      NumberFormat format,
      Integer emptyNumber)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createIntegerField(
          getField(name, validator), format, emptyNumber),
        name);
  }

  /**
   * Creates a formatted long field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createLongField (
      String name, Validator validator)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createLongField(getField(name, validator)),
        name);
  }

  /**
   * Creates a formatted long field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param emptyNumber Long that represents the empty string.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createLongField (
      String name, Validator validator, long emptyNumber)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createLongField(
          getField(name, validator), emptyNumber),
        name);
  }

  /**
   * Creates a formatted long field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param format Format used to convert numbers to and from strings.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createLongField (
      String name, Validator validator, NumberFormat format)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createLongField(
          getField(name, validator), format),
        name);
  }

  /**
   * Creates a formatted long field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param format Format used to convert numbers to and from strings.
   * @param emptyNumber Long that represents the empty string.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createLongField (
      String name, Validator validator, NumberFormat format, long emptyNumber)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createLongField(
          getField(name, validator), format, emptyNumber),
        name);
  }

  /**
   * Creates a formatted long field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param format Format used to convert numbers to and from strings.
   * @param emptyNumber Long that represents the empty string.
   * @return The JFormattedTextField.
   */
  public JFormattedTextField createLongField (
      String name, Validator validator, NumberFormat format, Long emptyNumber)
  {
    return (JFormattedTextField)component(
        BasicComponentFactory.createLongField(
          getField(name, validator), format, emptyNumber),
        name);
  }

  /**
   * Creates a label for the supplied field name.
   *
   * @param name The field name.
   * @return The JLabel.
   */
  public JLabel createLabel (String name)
  {
    return (JLabel)component(
        BasicComponentFactory.createLabel(model.getFieldModel(name)), name);
  }

  /**
   * Creates a label for the supplied field name.
   *
   * @param name The field name.
   * @param format The format to use.
   * @return The JLabel.
   */
  public JLabel createLabel (String name, Format format)
  {
    return (JLabel)component(
        BasicComponentFactory.createLabel(model.getFieldModel(name), format),
        name);
  }

  /**
   * Creates a list for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param list List of values.
   * @return The JList.
   */
  public JList createList (String name, Validator validator, List list)
  {
    SelectionInList selection =
      new SelectionInList(list, getField(name, validator));
    return (JList)component(
        BasicComponentFactory.createList(selection), name);
  }

  /**
   * Creates a list for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param list List of values.
   * @param renderer Renderer for rendering list elements.
   * @return The JList.
   */
  public JList createList (
      String name, Validator validator, List list, ListCellRenderer renderer)
  {
    SelectionInList selection =
      new SelectionInList(list, getField(name, validator));
    return (JList)component(
        BasicComponentFactory.createList(selection, renderer), name);
  }

  /**
   * Creates a password field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @return The JPasswordField.
   */
  public JPasswordField createPasswordField (
      String name, Validator validator)
  {
    return (JPasswordField)component(
        BasicComponentFactory.createPasswordField(
          getField(name, validator), false),
        name);
  }

  /**
   * Creates a radio button for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @param value The value for the radio button.
   * @param text The text for the radio button.
   * @return The JRadioButton.
   */
  public JRadioButton createRadioButton (
      String name, Validator validator, String value, String text)
  {
    return (JRadioButton)component(
        BasicComponentFactory.createRadioButton(
          getField(name, validator), value, text),
        name);
  }

  /**
   * Creates a text area for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @return The JTextField.
   */
  public JTextArea createTextArea (String name, Validator validator)
  {
    return (JTextArea)component(
        BasicComponentFactory.createTextArea(
          getField(name, validator), false),
        name);
  }

  /**
   * Creates a text field for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @return The JTextField.
   */
  public JTextField createTextField (String name, Validator validator)
  {
    return (JTextField)component(
        BasicComponentFactory.createTextField(
          getField(name, validator), false),
        name);
  }

  /**
   * Gets the FormFieldModel for the named field.
   *
   * @param name The name of the field.
   * @param validator The validator for the field.
   * @return The FormFieldModel.
   */
  private FormFieldModel getField (String name, Validator validator)
  {
    return model.createFieldModel(name, validator);
  }

  /**
   * Sets any client properties on the component.
   *
   * @param component The component.
   * @param name The field name.
   * @return The component.
   */
  private JComponent component (JComponent component, String name)
  {
    component.setName(name);
    component.putClientProperty(FORM_FIELD, Boolean.TRUE);

    FormFieldModel field = model.getFieldModel(name);
    if(field.isRequired()){
      ValidationComponentUtils.setMandatory(component, true);
    }
    ValidationComponentUtils.setMessageKey(
        component, model.getName() + '.' + name);

    return component;
  }
}
