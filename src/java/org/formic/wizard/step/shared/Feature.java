/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008 Eric Van Dewoestine
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Represents an available feature.
 *
 * @author Eric Van Dewoestine
 */
public class Feature
{
  public static final String ENABLED_PROPERTY = "enabled";

  private String key;
  private String title;
  private String info;
  private boolean enabled;
  private PropertyChangeSupport propertyChangeSupport;
  private String[] dependencies;

  /**
   * Constructs a new instance.
   *
   * @param key The key for this instance.
   * @param enabled True if the feature is enabled by default, false
   * otherwise.
   */
  public Feature(String key, boolean enabled)
  {
    this.key = key;
    this.enabled = enabled;
    this.propertyChangeSupport = new PropertyChangeSupport(this);
  }

  /**
   * Constructs a new instance.
   *
   * @param key The key for this instance.
   * @param enabled True if the feature is enabled by default, false
   * otherwise.
   * @param dependencies Array of other feature keys, that are required for
   * this feature to be installed.
   */
  public Feature(String key, boolean enabled, String[] dependencies)
  {
    this(key, enabled);
    this.dependencies = dependencies;
  }

  /**
   * Gets the key for this instance.
   *
   * @return The key.
   */
  public String getKey()
  {
    return this.key;
  }

  /**
   * Determines if this instance is enabled.
   *
   * @return The enabled.
   */
  public boolean isEnabled()
  {
    return this.enabled;
  }

  /**
   * Sets whether or not this instance is enabled.
   *
   * @param enabled True if enabled, false otherwise.
   */
  public void setEnabled(boolean enabled)
  {
    propertyChangeSupport.firePropertyChange(
        ENABLED_PROPERTY, this.enabled, this.enabled = enabled);
  }

  /**
   * Gets the dependencies for this instance.
   *
   * @return The dependencies.
   */
  public String[] getDependencies()
  {
    return this.dependencies;
  }

  /**
   * Sets the dependencies for this instance.
   *
   * @param dependencies The dependencies.
   */
  public void setDependencies(String[] dependencies)
  {
    this.dependencies = dependencies;
  }

  /**
   * Gets the info for this instance.
   *
   * @return The info.
   */
  public String getInfo()
  {
    return this.info;
  }

  /**
   * Sets the info for this instance.
   *
   * @param info The info.
   */
  public void setInfo(String info)
  {
    this.info = info;
  }

  /**
   * Sets the title for this feature.
   *
   * @param title The title.
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * {@inheritDoc}
   * @see Object#toString()
   */
  public String toString()
  {
    return title;
  }

  /**
   * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
   */
  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  /**
   * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
   */
  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
}
