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
package org.formic.wizard.step;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Iterator;

import javax.swing.JComponent;

import charva.awt.Component;

import org.apache.log4j.Logger;

import org.formic.form.Form;
import org.formic.form.FormModel;

import org.formic.form.console.ConsoleForm;

import org.formic.form.gui.GuiForm;

/**
 * Abstract wizard step for form based panels.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public abstract class AbstractFormStep
  extends AbstractStep
  implements PropertyChangeListener
{
  private static final Logger logger = Logger.getLogger(AbstractFormStep.class);

  private Form form;

  /**
   * @see AbstractStep#AbstractStep(String)
   */
  public AbstractFormStep (String name)
  {
    super(name);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initGui()
   */
  public final JComponent initGui ()
  {
    form = initGuiForm();
    setValid(form.getModel().isValid());

    form.getModel().addPropertyChangeListener(this);

    return (JComponent)form;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initConsole()
   */
  public final Component initConsole ()
  {
    form = initConsoleForm();
    setValid(form.getModel().isValid());

    form.getModel().addPropertyChangeListener(this);

    return (Component)form;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#proceed()
   */
  public void proceed ()
  {
    // FIXME: put form values into installation context.
if(form != null){
for (Iterator ii = form.getModel().getFieldModels().iterator(); ii.hasNext();){
  logger.info("commit field - " + ii.next());
}
}
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#isValid()
   */
  public boolean isValid ()
  {
    return form != null ? form.getModel().isValid() : true;
  }

  /**
   * Initializes and returns the gui form.
   *
   * @return The GuiForm instance.
   */
  protected abstract GuiForm initGuiForm ();

  /**
   * Initializes and returns the console form.
   *
   * @return The ConsoleForm instance.
   */
  protected abstract ConsoleForm initConsoleForm ();

  /**
   * {@inheritDoc}
   * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange (PropertyChangeEvent evt)
  {
    if(FormModel.FORM_VALID.equals(evt.getPropertyName())){
      Boolean value = (Boolean)evt.getNewValue();
      setValid(value.booleanValue());
    }
  }
}
