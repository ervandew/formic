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
package org.formic.wizard.step.shared;

/**
 * Action for executing the uninstall task and monitoring its progress.
 *
 * @author Eric Van Dewoestine
 */
public class UninstallAction
  extends InstallAction
{
  /**
   * Constructs a new UninstallAction with the supplied InstallListener.
   *
   * @param listener The listener listening to uninstall progress events.
   */
  public UninstallAction(InstallListener listener)
  {
    super("uninstall", listener);
  }

  /**
   * Constructs a new UninstallAction with the supplied uninstall task name and
   * InstallListener.
   *
   * @param taskName The name of the uninstall task to run.
   * @param listener The listener listening to uninstall progress events.
   */
  public UninstallAction(String taskName, InstallListener listener)
  {
    super(taskName, listener);
  }
}
