/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008  Eric Van Dewoestine
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
import java.beans.PropertyChangeSupport;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.formic.InstallContext;
import org.formic.Installer;

import org.formic.wizard.WizardStep;

import org.formic.wizard.form.Form;

import org.pietschy.wizard.WizardModel;

/**
 * Abstract super class for wizard steps.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision: 282 $
 */
public abstract class AbstractStep
  implements WizardStep, PropertyChangeListener
{
  protected static final String PROPERTY_REQUIRED = "property.required";
  protected static final String PROPERTY_TYPE_INVALID = "property.type.invalid";
  protected static final String PROPERTY_CLASS_NOT_FOUND = "property.class.not.found";
  protected static final String RESOURCE_REQUIRED = "resource.required";
  protected static final String RESOURCE_NOT_FOUND = "resource.not.found";
  protected static final String RESOURCE_INVALID = "resource.invalid";

  protected WizardModel wizardModel;
  protected Form form;

  private String name;
  private String title;
  private String description;
  private Properties properties;
  private boolean cancel = true;
  private boolean previous = true;
  private boolean valid = true;
  private boolean busy;

  private PropertyChangeSupport changeSupport;

  /**
   * Constructs a new instance.
   *
   * @param name The name of the step.
   * @param properties The step properties.
   */
  public AbstractStep (String name, Properties properties)
  {
    this.name = name;
    this.properties = properties;
    title = Installer.getString(name + ".title");
    description = Installer.getString(name + ".description");
    changeSupport = new PropertyChangeSupport(this);
  }

  /**
   * {@inheritDoc}
   * @see WizardStep#getName()
   */
  public String getName ()
  {
    return this.name;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#getTitle()
   */
  public String getTitle ()
  {
    return title;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#getDescription()
   */
  public String getDescription ()
  {
    return description;
  }

  /**
   * Gets a property by name.
   *
   * @param name The property name.
   * @return The property value or null if not found.
   */
  public String getProperty (String name)
  {
    return properties.getProperty(name);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#prepare()
   */
  public void prepare ()
  {
  }

  /**
   * {@inheritDoc}
   * @see WizardStep#displayed()
   */
  public void displayed ()
  {
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#proceed()
   */
  public boolean proceed ()
  {
    if(form != null){
      InstallContext context = Installer.getContext();
      Map values = form.getValues();
      for (Iterator ii = values.keySet().iterator(); ii.hasNext();){
        String key = (String)ii.next();
        Object value = values.get(key);
        if (value != null){
          context.setValue(key, value);
        }
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * @see WizardStep#abort()
   */
  public void abort ()
  {
  }

  /**
   * Sets whether the cancel button is enabled.
   */
  public void setCancelEnabled (boolean _cancel)
  {
    changeSupport.firePropertyChange(CANCEL, cancel, cancel = _cancel);
  }

  /**
   * Sets whether the previous button is enabled.
   */
  public void setPreviousEnabled (boolean _previous)
  {
    changeSupport.firePropertyChange(PREVIOUS, previous, previous = _previous);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#isValid()
   */
  public boolean isValid ()
  {
    return valid;
  }

  /**
   * Sets whether this step's data is valid.
   *
   * @param valid true if the data is valid, false otherwise.
   */
  public void setValid (boolean valid)
  {
    changeSupport.firePropertyChange(VALID, this.valid, this.valid = valid);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#isBusy()
   */
  public boolean isBusy ()
  {
    return busy;
  }

  /**
   * Sets whether this step is busy.
   */
  public void setBusy (boolean _busy)
  {
    changeSupport.firePropertyChange(BUSY, busy, busy = _busy);
  }

  /**
   * {@inheritDoc}
   * @see WizardStep#isBusyAnimated()
   */
  public boolean isBusyAnimated ()
  {
    return true;
  }

  /**
   * {@inheritDoc}
   * @see WizardStep#isPreviousEnabled()
   */
  public boolean isPreviousEnabled ()
  {
    return previous;
  }

  /**
   * {@inheritDoc}
   * @see WizardStep#isCancelEnabled()
   */
  public boolean isCancelEnabled ()
  {
    return cancel;
  }

  /**
   * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
   */
  public void addPropertyChangeListener (PropertyChangeListener listener)
  {
    changeSupport.addPropertyChangeListener(listener);
  }

  /**
   * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
   */
  public void removePropertyChangeListener (PropertyChangeListener listener)
  {
    changeSupport.removePropertyChangeListener(listener);
  }

  /**
   * {@inheritDoc}
   * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange (PropertyChangeEvent evt)
  {
    if(Form.VALID.equals(evt.getPropertyName())){
      Boolean value = (Boolean)evt.getNewValue();
      setValid(value.booleanValue());
    }
  }

  /**
   * Generate a fully qualified field name.
   *
   * @param name The unqualified field name.
   * @return The fully qualified field name.
   */
  protected String fieldName (String name)
  {
    return this.name + '.' + name;
  }
}
