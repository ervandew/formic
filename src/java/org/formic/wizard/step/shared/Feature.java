/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2014 Eric Van Dewoestine
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
  private String info;
  private boolean enabled;
  private boolean available = true;
  private PropertyChangeSupport propertyChangeSupport;
  private String[] dependencies;
  private String[] exclusives;

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
   * Constructs a new instance.
   *
   * @param key The key for this instance.
   * @param enabled True if the feature is enabled by default, false
   * otherwise.
   * @param dependencies Array of other feature keys, that are required for
   * this feature to be installed.
   * @param exclusives Array of other feature keys, that cannot be enabled at
   * the same time as this feature.
   */
  public Feature(
      String key,
      boolean enabled,
      String[] dependencies,
      String[] exclusives)
  {
    this(key, enabled);
    this.dependencies = dependencies;
    this.exclusives = exclusives;
  }

  /**
   * Gets the key for this feature.
   *
   * @return The key.
   */
  public String getKey()
  {
    return this.key;
  }

  /**
   * Determines if this feature is enabled.
   *
   * @return The enabled.
   */
  public boolean isEnabled()
  {
    return this.enabled;
  }

  /**
   * Sets whether or not this feature is enabled.
   *
   * @param enabled True if enabled, false otherwise.
   */
  public void setEnabled(boolean enabled)
  {
    propertyChangeSupport.firePropertyChange(
        ENABLED_PROPERTY, this.enabled, this.enabled = enabled);
  }

  /**
   * Determines if this feature is available for installation.
   *
   * @return The enabled.
   */
  public boolean isAvailable()
  {
    return this.available;
  }

  /**
   * Sets whether or not this feature is available for installation.
   *
   * @param available True if available, false otherwise.
   */
  public void setAvailable(boolean available)
  {
    this.available = available;
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
   * Determines if this feature has the supplied feature as a dependency.
   *
   * @param key The key of the feature.
   * @return True if the feature is a dependency, false otherwise.
   */
  public boolean hasDependency(String key)
  {
    if (dependencies == null || key == null){
      return false;
    }

    for(int ii = 0; ii < dependencies.length; ii++){
      if (dependencies[ii].equals(key)){
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the array of features that cannot be selected when this feature is
   * selected.
   *
   * @return Array of feature names.
   */
  public String[] getExclusives()
  {
    return this.exclusives;
  }

  /**
   * Sets the array of features that cannot be selected when this feature is
   * selected.
   *
   * @param exclusives Array of feature names.
   */
  public void setExclusives(String[] exclusives)
  {
    this.exclusives = exclusives;
  }

  /**
   * Determines if this feature has the supplied feature in its exclusive list.
   *
   * @param key The key of the feature.
   * @return True if the feature is in the exclusive list, false otherwise.
   */
  public boolean hasExclusive(String key)
  {
    if (exclusives == null || key == null){
      return false;
    }

    for(int ii = 0; ii < exclusives.length; ii++){
      if (exclusives[ii].equals(key)){
        return true;
      }
    }
    return false;
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
