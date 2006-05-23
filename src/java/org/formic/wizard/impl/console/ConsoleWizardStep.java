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
package org.formic.wizard.impl.console;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Icon;

import charvax.swing.JComponent;

import org.formic.wizard.WizardStep;

import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.WizardModel;

/**
 * Wraps {@link WizardStep} implementations for use in the console wizard framework.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class ConsoleWizardStep
  implements org.pietschy.wizard.WizardStep
{
  private WizardStep step;
  private JComponent component;
  private boolean complete;
  private boolean busy;

  private PropertyChangeSupport changeSupport;

  /**
   * Constructs a new instance.
   *
   * @param step The step for this instance.
   */
  public ConsoleWizardStep (WizardStep step)
  {
    this.step = step;
    changeSupport = new PropertyChangeSupport(this);
  }

  /**
   * Gets the step for this instance.
   *
   * @return The step.
   */
  public WizardStep getStep ()
  {
    return this.step;
  }

  /**
   * Initialize the step.
   */
  public JComponent init ()
  {
    component = step.initConsole();

    return component;
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#getName()
   */
  public String getName ()
  {
    return step.getTitle();
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#getSummary()
   */
  public String getSummary ()
  {
    return step.getDescription();
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#getIcon()
   */
  public Icon getIcon ()
  {
    return null;
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#getView()
   */
  public java.awt.Component getView ()
  {
    return null;
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#isComplete()
   */
  public boolean isComplete ()
  {
    return complete;
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#isBusy()
   */
  public boolean isBusy ()
  {
    return busy;
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#init(WizardModel)
   */
  public void init (WizardModel _model)
  {
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#prepare()
   */
  public void prepare ()
  {
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#applyState()
   */
  public void applyState ()
    throws InvalidStateException
  {
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#abortBusy()
   */
  public void abortBusy ()
  {
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#getPreferredSize()
   */
  public java.awt.Dimension getPreferredSize ()
  {
    return null;
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#addPropertyChangeListener(PropertyChangeListener)
   */
  public void addPropertyChangeListener (PropertyChangeListener listener)
  {
    changeSupport.addPropertyChangeListener(listener);
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#removePropertyChangeListener(PropertyChangeListener)
   */
  public void removePropertyChangeListener (PropertyChangeListener listener)
  {
    changeSupport.removePropertyChangeListener(listener);
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#addPropertyChangeListener(String,PropertyChangeListener)
   */
  public void addPropertyChangeListener (
      String property, PropertyChangeListener listener)
  {
    changeSupport.addPropertyChangeListener(property, listener);
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#removePropertyChangeListener(String,PropertyChangeListener)
   */
  public void removePropertyChangeListener (
      String property, PropertyChangeListener listener)
  {
    changeSupport.removePropertyChangeListener(property, listener);
  }
}
