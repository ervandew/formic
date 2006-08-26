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
package org.formic.form.console.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import charvax.swing.ComboBoxModel;
import charvax.swing.DefaultComboBoxModel;
import charvax.swing.ListModel;

import com.jgoodies.binding.list.SelectionInList;

import com.jgoodies.binding.value.ValueModel;

/**
 * Adapter for binding a ValueModel to a JComboBox.
 * <p/>
 * Based heavily on the jgoodies equivelant for the swing JComboBox.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class ComboBoxAdapter
  extends DefaultComboBoxModel
  implements ComboBoxModel
{
  private final SelectionInList selectionInList;
  private final ValueModel selectionHolder;

  public ComboBoxAdapter (List items, ValueModel selectionHolder)
  {
    this(new SelectionInList(items), selectionHolder, false);
  }

  public ComboBoxAdapter (Object[] items, ValueModel selectionHolder)
  {
    this(new SelectionInList(items), selectionHolder, false);
  }

  public ComboBoxAdapter (ValueModel listHolder, ValueModel selectionHolder)
  {
    this(new SelectionInList(listHolder), selectionHolder, false);
  }

  public ComboBoxAdapter (SelectionInList selectionInList)
  {
    this(selectionInList, null, true);
  }

  protected ComboBoxAdapter(SelectionInList selectionInList,
      ValueModel selectionHolder,
      boolean ignoreNullSelectionHolder)
  {
    if ((!ignoreNullSelectionHolder) && (selectionHolder == null)) {
      throw new NullPointerException("The selection holder must not be null.");
    } else if (selectionInList == null) {
      throw new NullPointerException("The SelectionInList must not be null.");
    }
    this.selectionInList = selectionInList;
    this.selectionHolder = selectionHolder;
    getSelectionHolder().addValueChangeListener(new SelectionChangeHandler());
    selectionInList.addListDataListener(new ListDataChangeHandler());
  }


  /**
   * {@inheritDoc}
   * @see ComboBoxModel#getSelectedItem()
   */
  public Object getSelectedItem()
  {
    return getSelectionHolder().getValue();
  }

  /**
   * {@inheritDoc}
   * @see ComboBoxModel#setSelectedItem(Object)
   */
  public void setSelectedItem (Object object)
  {
    getSelectionHolder().setValue(object);
  }

  /**
   * {@inheritDoc}
   * @see ListModel#getSize()
   */
  public int getSize ()
  {
    return selectionInList.getSize();
  }

  /**
   * {@inheritDoc}
   * @see ListModel#getElementAt(int)
   */
  public Object getElementAt (int index)
  {
    return selectionInList.getElementAt(index);
  }

  /**
   * Looks up and returns the ValueModel that holds the combo's selection.
   * If this adapter has been constructed with a separate selection holder,
   * this holder is returned. Otherwise the selection is held by the
   * SelectionInList, and so the SelectionInList is returned.
   */
  private ValueModel getSelectionHolder ()
  {
    return selectionHolder != null ? selectionHolder : selectionInList;
  }

  /**
   * Listens to selection changes and fires a contents change event.
   */
  private class SelectionChangeHandler
    implements PropertyChangeListener
  {
    public void propertyChange (PropertyChangeEvent evt)
    {
      fireContentsChanged(ComboBoxAdapter.this, -1, -1);
    }
  }

  /**
   * Handles ListDataEvents in the list model.
   */
  private class ListDataChangeHandler
    implements ListDataListener
  {
    public void intervalAdded (ListDataEvent evt)
    {
      fireIntervalAdded(ComboBoxAdapter.this, evt.getIndex0(), evt.getIndex1());
    }

    public void intervalRemoved (ListDataEvent evt)
    {
      fireIntervalRemoved(
          ComboBoxAdapter.this, evt.getIndex0(), evt.getIndex1());
    }

    public void contentsChanged (ListDataEvent evt)
    {
      fireContentsChanged(
          ComboBoxAdapter.this, evt.getIndex0(), evt.getIndex1());
    }
  }
}
