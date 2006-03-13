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
   * Gets the component factory that can be used to create components for this
   * step.
   *
   * @return ComponentFactory suitable for creating swing components if running
   * in gui mode, or console components if running in console mode.
   */
  public ComponentFactory getComponentFactory ();

  /**
   * Used to inject the ComponentFactory to use.
   *
   * @param _componentFactory ComponentFactory.
   */
  public void setComponentFactory (ComponentFactory _componentFactory);

  /**
   * Invoked the first time this step is to be displayed.
   * <p/>
   * Should be used to layout the components for this step.
   *
   * @return The step display (gui mode should return a javax.swing.JComponent,
   * console mode should return charvax.swing.JComponent).
   */
  public Object init ();

  /**
   * Invoked the first time this step is to be displayed (gui only).
   * <p/>
   * This should be used to perform any setup that is specific to the gui.
   */
  public void initGui ();

  /**
   * Invoked the first time this step is to be displayed (console only).
   * <p/>
   * This should be used to perform any setup that is specific to the console.
   */
  public void initConsole ();

  /**
   * Invoked before this step is displayed.
   * <p/>
   * This method is called on every display of the step, not just the first.
   */
  public void prepare ();

  /**
   * Invoked prior to proceeding to the next step.
   */
  public void proceed ();

  /**
   * Invoked to determine if the data supplied by the user for this step is
   * valid and if the user may proceed to the next step.
   */
  public boolean valid ();

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
