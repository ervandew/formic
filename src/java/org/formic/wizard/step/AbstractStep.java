/**
 * Copyright (c) 2004 - 2006
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.formic.wizard.step;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.Properties;

import javax.swing.Icon;

import org.formic.Installer;

import org.formic.wizard.ComponentFactory;
import org.formic.wizard.WizardStep;

import org.pietschy.wizard.WizardModel;

/**
 * Abstract super class for wizard steps.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public abstract class AbstractStep
  implements WizardStep
{
  protected WizardModel wizardModel;

  private String title;
  private String description;
  private boolean valid = true;
  private Properties properties;
  private ComponentFactory componentFactory;

  private PropertyChangeSupport changeSupport;

  /**
   * Constructs a new instance.
   *
   * @param _properties The properties for this step.
   */
  public AbstractStep (String _name, Properties _properties)
  {
    title = Installer.getString(_name + ".title");
    description = Installer.getString(_name + ".description");
    properties = _properties;

    changeSupport = new PropertyChangeSupport(this);
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
   * Gets the value for the supplied property.
   *
   * @param _name The property name.
   * @return The property value.
   */
  public String getProperty (String _name)
  {
    return properties.getProperty(_name);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#getIcon()
   */
  public Icon getIcon ()
  {
    return null;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initGui()
   */
  public void initGui ()
  {
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initConsole()
   */
  public void initConsole ()
  {
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
   * @see org.formic.wizard.WizardStep#proceed()
   */
  public void proceed ()
  {
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#valid()
   */
  public boolean valid ()
  {
    return valid;
  }

  /**
   * Sets whether this step's data is valid.
   */
  public void setValid (boolean _valid)
  {
    changeSupport.firePropertyChange("valid", valid, valid = _valid);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#getComponentFactory()
   */
  public ComponentFactory getComponentFactory ()
  {
    return componentFactory;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#setComponentFactory(ComponentFactory)
   */
  public void setComponentFactory (ComponentFactory _componentFactory)
  {
    componentFactory = _componentFactory;
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
}
