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

import org.formic.wizard.form.ConsoleForm;

/**
 * Abstract super class for console wizard steps.
 *
 * @author Eric Van Dewoestine
 */
public abstract class AbstractConsoleStep
  extends AbstractStep
  implements ConsoleStep
{
  /**
   * {@inheritDoc}
   * @see AbstractStep#AbstractConsoleStep(String,Properties)
   */
  public AbstractConsoleStep(String name, Properties properties)
  {
    super(name, properties);
  }

  /**
   * Creates and returns a new ConsoleForm instance.
   *
   * @return The new ConsoleForm.
   */
  protected ConsoleForm createForm()
  {
    ConsoleForm form = new ConsoleForm();
    form.addPropertyChangeListener(this);
    return form;
  }
}
