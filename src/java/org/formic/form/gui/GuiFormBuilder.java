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

import java.awt.Insets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JLabel;

import javax.swing.border.Border;

import com.jgoodies.forms.builder.DefaultFormBuilder;

import com.jgoodies.forms.factories.FormFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

import org.apache.commons.lang.StringUtils;

import org.formic.Installer;

/**
 * Eases the construction of gui based forms.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class GuiFormBuilder
{
  public static final String DLU = "dlu";
  public static final String PX = "px";
  public static final String PT = "pt";
  public static final String IN = "in";
  public static final String CM = "cm";
  public static final String MM = "mm";

  private static final Pattern INSET_PATTERN =
    Pattern.compile("^\\s*([0-9]+)\\s*([a-zA-Z]*)\\s*$");

  private String name;
  private GuiForm form;
  private GuiComponentFactory factory;
  private DefaultFormBuilder builder;
  private CellConstraints cc;

  /**
   * Constructs a new instance with the supplied layout.
   *
   * @param name The form name (used as a prefix for resolving resource keys).
   * @param layout The layout.
   */
  public GuiFormBuilder (String name, FormLayout layout)
  {
    this.name = name;
    form = new GuiForm();
    factory = new GuiComponentFactory(name);
    form.setModel(factory.getFormModel());

    builder = new DefaultFormBuilder(layout, form);
    cc = new CellConstraints();
  }

  /**
   * Gets the built form.
   *
   * @return The form.
   */
  public GuiForm getForm ()
  {
    return form;
  }

  /**
   * Gets the factory for this instance.
   *
   * @return The factory.
   */
  public GuiComponentFactory getFactory ()
  {
    return this.factory;
  }

  /**
   * Sets the factory for this instance.
   *
   * @param factory The factory.
   */
  public void setFactory (GuiComponentFactory factory)
  {
    this.factory = factory;
  }

  /**
   * Advances the builders cursor to the next row.
   *
   * @return This builder to facilitate method chaining.
   */
  public GuiFormBuilder nextRow ()
  {
    builder.nextLine();
    return this;
  }

  /**
   * Sets the border of the form.
   *
   * @param border The border to use.
   */
  public void setBorder (Border border)
  {
    builder.setBorder(border);
  }

  /**
   * Sets a default border.
   */
  public void setDefaultBorder ()
  {
    builder.setDefaultDialogBorder();
  }

  /**
   * Appends a seprator at the current cursor position.
   *
   * @param text The text or resource key to the text to be displayed in the
   * separator.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder appendSeparator (String text)
  {
    builder.appendSeparator(
        Installer.getString(name + '.' + text, text));
    return this;
  }

  /**
   * Appends the supplied component to this form.
   *
   * @param component The component to append.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder append (JComponent component)
  {
    return append(component, 1, 1);
  }

  /**
   * Appends the supplied component to this form.
   *
   * @param component The component to append.
   * @param colspan The column span of the component.
   * @param rowspan The row span of the component.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder append (JComponent component, int colspan, int rowspan)
  {
    return append(component, colspan, rowspan, null);
  }

  /**
   * Appends the supplied component to this form.
   *
   * @param component The component to append.
   * @param colspan The column span of the component.
   * @param rowspan The row span of the component.
   * @param insets The insets to use when adding the component.
   * Example: "0 5dlu 0 5dlu"
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder append (
      JComponent component, int colspan, int rowspan, String insets)
  {
    if(component.getClientProperty(GuiComponentFactory.FORM_FIELD) != null){
      return append(component.getName(), component, colspan, rowspan, insets);
    }

    ensureCursorColumnInGrid();
    ensureHasGapRow(builder.getLineGapSpec());
    ensureHasComponentLine();

    if(insets != null){
      builder.add(component,
          new CellConstraints(
            builder.getColumn(), builder.getRow(),
            colspan, rowspan,
            CellConstraints.DEFAULT, CellConstraints.DEFAULT,
            parseInsets(insets)));
    }else{
      builder.add(component,
          cc.xywh(builder.getColumn(), builder.getRow(), colspan, rowspan));
    }

    if(builder.getColumnCount() > builder.getColumn() + colspan){
      builder.nextColumn(colspan + 1);
    }

    return this;
  }

  /**
   * Appends the supplied label and component to this form.
   *
   * @param label The label.
   * @param component The component to append.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder append (JLabel label, JComponent component)
  {
    return append(label, component, 1, 1);
  }

  /**
   * Appends the supplied component to this form.
   *
   * @param label The label.
   * @param component The component to append.
   * @param colspan The column span of the component.
   * @param rowspan The row span of the component.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder append (
      JLabel label, JComponent component, int colspan, int rowspan)
  {
    append(label);
    component.putClientProperty(GuiComponentFactory.FORM_FIELD, null);
    append(component, colspan, rowspan);
    return this;
  }

  /**
   * Appends the supplied label and component to this form.
   *
   * @param label The text to place in a label.
   * @param component The component to append.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder append (String label, JComponent component)
  {
    return append(label, component, 1, 1, null);
  }

  /**
   * Appends the supplied component to this form.
   *
   * @param label The text to place in a label.
   * @param component The component to append.
   * @param colspan The column span of the component.
   * @param rowspan The row span of the component.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder append (
      String label, JComponent component, int colspan, int rowspan)
  {
    return append(label, component, 1, 1, null);
  }

  /**
   * Appends the supplied component to this form.
   *
   * @param label The text to place in a label.
   * @param component The component to append.
   * @param colspan The column span of the component.
   * @param rowspan The row span of the component.
   * @param insets The insets to use when adding the component.
   * Example: "0 5dlu 0 5dlu"
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder append (
      String label,
      JComponent component,
      int colspan,
      int rowspan,
      String insets)
  {
    append(new JLabel(Installer.getString(name + '.' + label, label)));
    component.putClientProperty(GuiComponentFactory.FORM_FIELD, null);
    append(component, colspan, rowspan, insets);
    return this;
  }

  /**
   * Parses the supplied insets spec and returns an equivelant Insets instance.
   *
   * @param spec The insets spec.
   * @return The Insets instance.
   */
  private Insets parseInsets (String spec)
  {
    int top = 0;
    int left = 0;
    int bottom = 0;
    int right = 0;

    if(spec != null && spec.trim().length() > 0){
      String[] values = StringUtils.split(spec, ',');
      if(values.length != 4){
        throw new RuntimeException(
            Installer.getString("inset.spec.invalid.length",
              new Object[]{spec, new Integer(values.length)}));
      }
      top = parseInset(values[0]);
      left = parseInset(values[1]);
      bottom = parseInset(values[2]);
      right = parseInset(values[3]);
    }

    return new Insets(top, left, bottom, right);
  }

  /**
   * Parses the supplied inset and returns the proper number of pixels that it
   * represents.
   *
   * @param value The value.
   * @return The number of pixels.
   */
  private int parseInset (String value)
  {
    Matcher matcher = INSET_PATTERN.matcher(value);
    matcher.matches();

    String count = matcher.group(1);
    String measurement = matcher.group(2).toLowerCase();

    if(!StringUtils.isNumeric(count)){
      throw new RuntimeException(
          Installer.getString("inset.spec.invalid.unit",
            new Object[]{value, count}));
    }

    int units = Integer.parseInt(count);
    if(DLU.equals(measurement) || measurement.trim().length() == 0){
      return Sizes.dialogUnitXAsPixel(units, getForm());
    }else if(PX.equals(measurement)){
      return units;
    }else if(PT.equals(measurement)){
      return Sizes.pointAsPixel(units, getForm());
    }else if(IN.equals(measurement)){
      return Sizes.inchAsPixel(units, getForm());
    }else if(CM.equals(measurement)){
      return Sizes.centimeterAsPixel(units, getForm());
    }else if(MM.equals(measurement)){
      return Sizes.millimeterAsPixel(units, getForm());
    }

    throw new RuntimeException(
        Installer.getString("inset.spec.invalid.measurement",
          new Object[]{value, measurement}));
  }

  // taken from DefaultFormBuilder

  /**
   * Ensures that the cursor is in the grid. In case it's beyond the
   * form's right hand side, the cursor is moved to the leading column
   * of the next line.
   */
  private void ensureCursorColumnInGrid() {
    if ((builder.isLeftToRight() &&
          (builder.getColumn() > builder.getColumnCount())) ||
        (!builder.isLeftToRight() && (builder.getColumn() < 1))) {
      builder.nextLine();
     }
  }

  /**
   * Ensures that we have a gap row before the next component row.
   * Checks if the current row is the given <code>RowSpec</code>
   * and appends this row spec if necessary.
   *
   * @param gapRowSpec  the row specification to check for
   */
  private void ensureHasGapRow(RowSpec gapRowSpec) {
    if ((builder.getRow() == 1) || (builder.getRow() <= builder.getRowCount()))
      return;

    if (builder.getRow() <= builder.getRowCount()) {
      RowSpec rowSpec = builder.getLayout().getRowSpec(builder.getRow());
      if ((rowSpec == gapRowSpec))
        return;
    }
    builder.appendRow(gapRowSpec);
    builder.nextLine();
  }

  /**
   * Ensures that the form has a component row. Adds a component row
   * if the cursor is beyond the form's bottom.
   */
  private void ensureHasComponentLine() {
    if (builder.getRow() <= builder.getRowCount()) return;
    builder.appendRow(FormFactory.PREF_ROWSPEC);
    if (builder.isRowGroupingEnabled()) {
      builder.getLayout().addGroupedRow(builder.getRow());
    }
  }
}
