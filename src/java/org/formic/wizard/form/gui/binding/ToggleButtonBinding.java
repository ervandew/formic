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
package org.formic.wizard.form.gui.binding;

import javax.swing.JToggleButton;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.formic.wizard.form.Form;
import org.formic.wizard.form.FormField;

/**
 * Class for binding JToggleButton to a Form.
 *
 * @author Eric Van Dewoestine
 * @version $Revision$
 */
public class ToggleButtonBinding
  extends FormField
  implements ChangeListener
{
  private JToggleButton component;
  private Form form;

  /**
   * Constructs a new instance.
   *
   * @param component The JToggleButton.
   * @param form The form the component is a part of.
   */
  public ToggleButtonBinding (JToggleButton component, Form form)
  {
    this.component = component;
    this.form = form;
  }

  public static FormField bind (JToggleButton component, Form form)
  {
    ToggleButtonBinding binding = new ToggleButtonBinding(component, form);
    component.addChangeListener(binding);
    return binding;
  }

  /**
   * {@inheritDoc}
   * @see ChangeListener#stateChanged(ChangeEvent)
   */
  public void stateChanged (ChangeEvent e)
  {
    form.setValue(this, component,
        component.isSelected() ? Boolean.TRUE : Boolean.FALSE, true);
  }
}
