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

  private String name;
  private String title;
  private String description;
  private String iconPath;
  private Icon icon;
  private boolean cancel = true;
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
    name = _name;
    title = Installer.getString(_name + ".title");
    description = Installer.getString(_name + ".description");
    iconPath = Installer.getString(_name + ".icon");
    properties = _properties;

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
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#getIcon()
   */
  public Icon getIcon ()
  {
    if(icon == null){
      String path = getIconPath();
      path = path != null ? path : DEFAULT_ICON;

      icon = new ImageIcon(Installer.getImage(path));
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
   * Sets whether the cancel button is enabled.
   */
  public void setCancelEnabled (boolean _cancel)
  {
    changeSupport.firePropertyChange(CANCEL, cancel, cancel = _cancel);
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
   * {@inheritDoc}
   * @see WizardStep#isPreviousEnabled()
   */
  public boolean isPreviousEnabled ()
  {
    return true;
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
}
