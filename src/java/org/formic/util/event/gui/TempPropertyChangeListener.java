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
package org.formic.util.event.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporary listener that queues up events.
 *
 * @author Eric Van Dewoestine
 */
public class TempPropertyChangeListener
  implements PropertyChangeListener
{
  private List events = new ArrayList();

  /**
   * {@inheritDoc}
   * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt)
  {
    events.add(evt);
  }

  /**
   * Get the queued up events.
   *
   * @return Array of events.
   */
  public PropertyChangeEvent[] getEvents()
  {
    return (PropertyChangeEvent[])
      events.toArray(new PropertyChangeEvent[events.size()]);
  }

  /**
   * Clears the queued events.
   */
  public void clear()
  {
    events.clear();
  }
}
