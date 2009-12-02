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
package org.formic.wizard.step;

import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.formic.Installer;

import org.formic.wizard.form.GuiForm;

/**
 * Abstract super class for gui wizard steps.
 *
 * @author Eric Van Dewoestine
 */
public abstract class AbstractGuiStep
  extends AbstractStep
  implements GuiStep
{
  private static final String DEFAULT_ICON = "/images/32x32/wizard.png";

  private Icon icon;
  private String iconPath;

  /**
   * @see AbstractStep#AbstractStep(String,Properties)
   */
  public AbstractGuiStep(String name, Properties properties)
  {
    super(name, properties);
    iconPath = Installer.getString(name + ".icon");
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.step.GuiStep#getIcon()
   */
  public Icon getIcon()
  {
    if(icon == null){
      String path = getIconPath();
      path = path != null ? path : DEFAULT_ICON;

      icon = new ImageIcon(Installer.getImage(path));
    }
    return icon;
  }

  /**
   * Gets the configured path to the icon.
   *
   * @return The path to the icon resource or null if none.
   */
  protected String getIconPath()
  {
    return iconPath;
  }

  /**
   * Creates and returns a new GuiForm instance.
   *
   * @return The new GuiForm.
   */
  protected GuiForm createForm()
  {
    form = new GuiForm();
    form.addPropertyChangeListener(this);
    return (GuiForm)form;
  }
}
