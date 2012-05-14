/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2012  Eric Van Dewoestine
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
package org.formic.wizard.impl.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import net.java.swingfx.waitwithstyle.MGlassPaneContainer;
import net.java.swingfx.waitwithstyle.SingleComponentInfiniteProgress;

import org.formic.Installer;

import org.formic.util.dialog.gui.GuiDialogs;

import org.formic.wizard.WizardStep;

import org.formic.wizard.step.GuiStep;

import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.WizardModel;

/**
 * Wraps {@link GuiStep} implementations for use in the gui wizard framework.
 *
 * @author Eric Van Dewoestine
 */
public class GuiWizardStep
  extends PanelWizardStep
  implements PropertyChangeListener
{
  private static final long serialVersionUID = 1L;

  private static final String BUSY_TEXT = Installer.getString("busy.text");

  private GuiStep step;
  private Component component;
  private SingleComponentInfiniteProgress infiniteProgress;

  /**
   * Constructs a new instance.
   *
   * @param step The step for this instance.
   */
  public GuiWizardStep(GuiStep step)
  {
    super(step.getTitle(), step.getDescription());
    this.step = step;
    step.addPropertyChangeListener(this);

    // set complete to true by default.
    setComplete(true);
  }

  /**
   * {@inheritDoc}
   * @see PanelWizardStep#setBusy(boolean)
   */
  public void setBusy(boolean busy)
  {
    super.setBusy(busy);

    // hackish.
    Container grandparent = getParent().getParent().getParent();
    Container parent = getParent().getParent();

    if(step.isBusyAnimated()){
      if(busy){
        if(infiniteProgress == null){
          infiniteProgress = new SingleComponentInfiniteProgress(false);
          //infiniteProgress.setBackground(java.awt.Color.BLACK);
          //infiniteProgress.setForeground(java.awt.Color.WHITE);
          infiniteProgress.setFont(new Font(null, Font.BOLD, 15));

          grandparent.remove(parent);
          MGlassPaneContainer container = new MGlassPaneContainer(parent);
          grandparent.add(container, BorderLayout.CENTER);
          container.setGlassPane(infiniteProgress);

          String busyText = Installer.getString(step.getName() + ".busy");
          infiniteProgress.setText(busyText != null ? busyText : BUSY_TEXT);
        }

        infiniteProgress.setVisible(true);
      }else{
        if (infiniteProgress != null){
          infiniteProgress.setVisible(false);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#init(WizardModel)
   */
  public void init(WizardModel _model)
  {
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#prepare()
   */
  public void prepare()
  {
    try{
      if(component == null){
        component = step.init();

        setLayout(new BorderLayout());
        add(component, BorderLayout.CENTER);
      }

      step.prepare();
    }catch(Exception e){
      GuiDialogs.showError(e);
    }
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#applyState()
   */
  public void applyState()
    throws InvalidStateException
  {
    if (!step.proceed()){
      throw new InvalidStateException("", false);
    }
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#isComplete()
   */
  public boolean isComplete()
  {
    return step.isValid();
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#isBusy()
   */
  public boolean isBusy()
  {
    return step.isBusy();
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#getIcon()
   */
  public Icon getIcon()
  {
    return step.getIcon();
  }

  /**
   * {@inheritDoc}
   * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt)
  {
    if(WizardStep.VALID.equals(evt.getPropertyName())){
      setComplete(((Boolean)evt.getNewValue()).booleanValue());
    }else if(WizardStep.BUSY.equals(evt.getPropertyName())){
      boolean busy = ((Boolean)evt.getNewValue()).booleanValue();
      setBusy(busy);
    }

    // propagate up the listener chain.
    firePropertyChange(
        evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#abortBusy()
   */
  public void abortBusy()
  {
    step.abort();
    setBusy(false);
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.WizardStep#getPreferredSize()
   */
  public Dimension getPreferredSize()
  {
    return new Dimension(
        Installer.getDimension().width - 15,
        Installer.getDimension().height - 130);
  }

  /**
   * Gets the underlying step.
   *
   * @return The WizardStep.
   */
  public WizardStep getStep()
  {
    return step;
  }
}
