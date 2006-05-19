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

import java.awt.Toolkit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.net.URL;

import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.formic.Installer;

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
  private static final String DEFAULT_ICON = "/images/wizard.png";

  protected WizardModel wizardModel;

  private String title;
  private String description;
  private String iconPath;
  private Icon icon;
  private boolean valid = true;
  private boolean busy;
  private Properties properties;

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
    iconPath = Installer.getString(_name + ".icon");
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
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#getIcon()
   */
  public Icon getIcon ()
  {
    if(icon == null){
      String path = getIconPath();
      path = path != null ? path : DEFAULT_ICON;
      URL url = AbstractStep.class.getResource(path);

      icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
    }
    return icon;
  }

  /**
   * Gets the configured path to the icon.
   *
   * @return The path to the icon resource or null if none.
   */
  protected String getIconPath ()
  {
    return iconPath;
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
  public void proceed ()
  {
  }

  /**
   * {@inheritDoc}
   * @see WizardStep#abort()
   */
  public void abort ()
  {
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
   */
  public void setValid (boolean _valid)
  {
    changeSupport.firePropertyChange(VALID, valid, valid = _valid);
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
