/**
 * Formic installer framework.
 * Copyright (C) 2004 - 2006  Eric Van Dewoestine
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
package org.formic.wizard;

import java.beans.PropertyChangeListener;

import javax.swing.Icon;

/**
 * Represents a step in the wizard.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public interface WizardStep
{
  /**
   * Property name used for property changed events to isCancelEnabled.
   */
  public static final String CANCEL = "cancel";

  /**
   * Property name used for property changed events to isValid.
   */
  public static final String VALID = "valid";

  /**
   * Property name used for property changed events to isBusy.
   */
  public static final String BUSY = "busy";

  /**
   * Gets the name given to this step.
   *
   * @return The step name.
   */
  public String getName ();

  /**
   * Gets the title of this step.
   *
   * @return This step's title.
   */
  public String getTitle ();

  /**
   * Gets the description of this step.
   *
   * @return The step's description.
   */
  public String getDescription ();

  /**
   * Gets the icon to display for this step (gui only).
   *
   * @return The step's icon.
   */
  public Icon getIcon ();

  /**
   * Invoked the first time this step is to be displayed in gui mode.
   * <p/>
   * Used to layout the components for the gui version of the step.
   */
  public javax.swing.JComponent initGui ();

  /**
   * Invoked the first time this step is to be displayed in console mode.
   * <p/>
   * Used to layout the components for the console version of the step.
   */
  public charvax.swing.JComponent initConsole ();

  /**
   * Invoked before this step is displayed.
   * <p/>
   * This method is called prior to every display of the step, not just the first.
   */
  public void prepare ();

  /**
   * Invoked after this step is displayed.
   * <p/>
   * This method is called on every display of the step, not just the first.
   */
  public void displayed ();

  /**
   * Invoked prior to proceeding to the next step.
   */
  public void proceed ();

  /**
   * Invoked to abort any processing taking place by this step.
   */
  public void abort ();

  /**
   * Invoked to determine if the data supplied by the user for this step is
   * valid and if the user may proceed to the next step.
   */
  public boolean isValid ();

  /**
   * Invoked to determine if the step is busy working on a time consuming task.
   */
  public boolean isBusy ();

  /**
   * Invoked to determine if an animated infinite progress should be displayed
   * when busy.
   */
  public boolean isBusyAnimated ();

  /**
   * Invoked to determine if the previous button should be enabled.
   */
  public boolean isPreviousEnabled ();

  /**
   * Invoked to determine if the cancel button should be enabled.
   */
  public boolean isCancelEnabled ();

  /**
   * Adds the supplied PropertyChangeListener.
   *
   * @param listener The PropertyChangeListener.
   */
  public void addPropertyChangeListener (PropertyChangeListener listener);

  /**
   * Removes the supplied PropertyChangeListener.
   *
   * @param listener The PropertyChangeListener.
   */
  public void removePropertyChangeListener (PropertyChangeListener listener);
}
