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
package org.formic.wizard.step;

import java.util.Properties;

import com.jgoodies.forms.layout.FormLayout;

import org.formic.form.console.ConsoleForm;

import org.formic.form.gui.GuiComponentFactory;
import org.formic.form.gui.GuiForm;
import org.formic.form.gui.GuiFormBuilder;

/**
 * Wizard step that allows the user to choose a file or folder.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class FileChooserStep
  extends AbstractFormStep
{
  protected static final String ICON = "/images/32x32/folder.png";

  /**
   * Constructs the welcome step.
   */
  public FileChooserStep (String name, Properties properties)
  {
    super(name, properties);
  }

  /**
   * {@inheritDoc}
   * @see AbstractStep#getIconPath()
   */
  protected String getIconPath ()
  {
    String path = super.getIconPath();
    return path != null ? path : ICON;
  }

  /**
   * {@inheritDoc}
   * @see AbstractFormStep#initGuiForm()
   */
  public GuiForm initGuiForm ()
  {
    FormLayout layout = new FormLayout("pref, 4dlu, 80dlu");
    GuiFormBuilder builder = new GuiFormBuilder(getName(), layout);
    GuiComponentFactory factory = builder.getFactory();

    builder.setDefaultBorder();

    builder.append("file", factory.createTextField("file", null));
    builder.nextRow();
    builder.append("folder", factory.createTextField("folder", null),
        1, 1, "0, 0, 0, 40dlu");
    builder.nextRow();
    builder.append("foo", factory.createTextField("foo", null));

    return builder.getForm();
  }

  /**
   * {@inheritDoc}
   * @see AbstractFormStep#initConsoleForm()
   */
  public ConsoleForm initConsoleForm ()
  {
    return null;
  }
}
