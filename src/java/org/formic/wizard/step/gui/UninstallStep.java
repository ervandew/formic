/**
 * Formic installer framework.
 * Copyright (C) 2012  Eric Van Dewoestine
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
package org.formic.wizard.step.gui;

import java.util.Properties;

import org.formic.wizard.step.shared.InstallAction;
import org.formic.wizard.step.shared.UninstallAction;

/**
 * Step that runs the background uninstall process and displays the progress for
 * the user.
 *
 * @author Eric Van Dewoestine
 */
public class UninstallStep
  extends InstallStep
{
  /**
   * Constructs the step.
   */
  public UninstallStep(String name, Properties properties)
  {
    super(name, properties);
  }

  /**
   * {@inheritDoc}
   * @see InstallStep#getAction()
   */
  protected InstallAction getAction()
  {
    return new UninstallAction(this);
  }
}
