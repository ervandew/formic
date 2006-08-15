package org.formic.form.console;

import javax.swing.text.PlainDocument;

import charvax.swing.JComponent;
import charvax.swing.JPasswordField;
import charvax.swing.JTextArea;
import charvax.swing.JTextField;

import com.jgoodies.binding.adapter.DocumentAdapter;

import org.formic.form.AbstractComponentFactory;
import org.formic.form.FormFieldModel;
import org.formic.form.FormModel;
import org.formic.form.Validator;

/**
 * Component factory for create components for a console based user interface.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class ConsoleComponentFactory
  extends AbstractComponentFactory
{
  private static final String MANDATORY_KEY = "validation.isMandatory";
  private static final String MESSAGE_KEY = "validation.messageKey";

  /**
   * Constructs a new instance using a default FormModel.
   *
   * @param name The form name (used as a prefix for field names and resource
   * keys).
   */
  public ConsoleComponentFactory (String name)
  {
    super(name);
  }

  /**
   * Constructs a new instance.
   *
   * @param model The model for this instance.
   */
  public ConsoleComponentFactory (FormModel model)
  {
    super(model);
  }

  /**
   * Creates a check box for the supplied field name.
   *
   * @param name The field name.
   * @param validator Validator used to validate the field.
   * @return The JCheckBox.
   */
  /*public JCheckBox createCheckBox (String name, Validator validator)
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
  /*public JComboBox createComboBox (
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
  /*public JComboBox createComboBox (
      String name, Validator validator, List list, ListCellRenderer renderer)
  {
    SelectionInList selection =
      new SelectionInList(list, getField(name, validator));
    return (JComboBox)component(
        BasicComponentFactory.createComboBox(selection, renderer), name);
  }

  /**
   * Creates a label for the supplied field name.
   *
   * @param name The field name.
   * @return The JLabel.
   */
  /*public JLabel createLabel (String name)
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
  /*public JLabel createLabel (String name, Format format)
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
  /*public JList createList (String name, Validator validator, List list)
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
  /*public JList createList (
      String name, Validator validator, List list, ListCellRenderer renderer)
  {
    SelectionInList selection =
      new SelectionInList(list, getField(name, validator));
    return (JList)component(
        BasicComponentFactory.createList(selection, renderer), name);
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
  /*public JRadioButton createRadioButton (
      String name, Validator validator, String value, String text)
  {
    return (JRadioButton)component(
        BasicComponentFactory.createRadioButton(
          getField(name, validator), value, text),
        name);
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
    JPasswordField field = new JPasswordField();
    field.setDocument(
        new DocumentAdapter(
          getField(name, validator), new PlainDocument(), true));
    return (JPasswordField)component(field, name);
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
    JTextArea area = new JTextArea();
    area.setDocument(
        new DocumentAdapter(
          getField(name, validator), new PlainDocument(), false));
    return (JTextArea)component(area, name);
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
    JTextField field = new JTextField();
    field.setDocument(
        new DocumentAdapter(
          getField(name, validator), new PlainDocument(), true));
    return (JTextField)component(field, name);
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

    FormFieldModel field = getFormModel().getFieldModel(name);
    if(field.isRequired()){
      component.putClientProperty(MANDATORY_KEY, Boolean.TRUE);
    }
    component.putClientProperty(MESSAGE_KEY,
        getFormModel().getName() + '.' + name);

    return component;
  }
}
