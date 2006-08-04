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

import javax.swing.JPanel;

import org.formic.form.Form;
import org.formic.form.FormModel;

/**
 * Implementation of {@link Form} for graphical interfaces.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class GuiForm
  extends JPanel
  implements Form
{
  private FormModel model;

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
  }

  /**
   * {@inheritDoc}
   * @see Form#isFormValid()
   */
  public boolean isFormValid ()
  {
    return false;
  }
}
