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

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.PanelBuilder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import org.formic.Installer;

/**
 * Eases the construction of gui based forms.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class GuiFormBuilder
{
  private GuiForm form;
  private GuiComponentFactory factory;
  private PanelBuilder builder;
  private CellConstraints cc;

  /**
   * Constructs a new instance with the supplied layout.
   *
   * @param layout The layout.
   */
  public GuiFormBuilder (FormLayout layout)
  {
    form = new GuiForm();
    factory = new GuiComponentFactory();
    form.setModel(factory.getFormModel());

    builder = new PanelBuilder(layout, form);
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
   * Adds a seprator at the current cursor position.
   *
   * @param text The text or resource key to the text to be displayed in the
   * separator.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder addSeparator (String text)
  {
    builder.addSeparator(Installer.getString(text, text));
    return this;
  }

  /**
   * Adds the supplied component to this form.
   *
   * @param component The component to add.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder add (JComponent component)
  {
    builder.add(component);
    return this;
  }

  /**
   * Adds the supplied component to this form.
   *
   * @param component The component to add.
   * @param colspan The column span of the component.
   * @param rowspan The row span of the component.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder add (JComponent component, int colspan, int rowspan)
  {
    builder.add(component,
        cc.xywh(builder.getColumn(), builder.getRow(), colspan, rowspan));

    if(builder.getColumnCount() > builder.getColumn() + colspan){
      builder.nextColumn(colspan + 1);
    }

    if(builder.getRowCount() > builder.getRow() + rowspan){
      builder.nextRow(rowspan + 1);
    }

    return this;
  }

  /**
   * Adds the supplied label and component to this form.
   *
   * @param label The label.
   * @param component The component to add.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder add (JLabel label, JComponent component)
  {
    add(label);
    add(component);
    return this;
  }

  /**
   * Adds the supplied component to this form.
   *
   * @param label The label.
   * @param component The component to add.
   * @param colspan The column span of the component.
   * @param rowspan The row span of the component.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder add (
      JLabel label, JComponent component, int colspan, int rowspan)
  {
    add(label);
    add(component, colspan, rowspan);
    return this;
  }

  /**
   * Adds the supplied label and component to this form.
   *
   * @param label The text to place in a label.
   * @param component The component to add.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder add (String label, JComponent component)
  {
    add(new JLabel(Installer.getString(label, label)));
    add(component);
    return this;
  }

  /**
   * Adds the supplied component to this form.
   *
   * @param label The text to place in a label.
   * @param component The component to add.
   * @param colspan The column span of the component.
   * @param rowspan The row span of the component.
   *
   * @return This builder instance for method chaining.
   */
  public GuiFormBuilder add (
      String label, JComponent component, int colspan, int rowspan)
  {
    add(new JLabel(Installer.getString(label, label)));
    add(component, colspan, rowspan);
    return this;
  }
}
