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
package org.formic.wizard.form.gui.binding;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import foxtrot.Task;
import foxtrot.Worker;

import org.formic.wizard.form.Form;
import org.formic.wizard.form.FormField;
import org.formic.wizard.form.ValidationUtils;

/**
 * Class for binding a JTextComponent to a Form.
 *
 * @author Eric Van Dewoestine
 * @version $Revision$
 */
public class TextComponentBinding
  extends FormField
  implements DocumentListener
{
  private JTextComponent component;
  private Form form;

  /**
   * Constructs a new instance.
   *
   * @param component The JTextComponent.
   * @param form The form the component is a part of.
   */
  public TextComponentBinding (JTextComponent component, Form form)
  {
    this.component = component;
    this.form = form;
  }

  public static FormField bind (JTextComponent component, Form form)
  {
    TextComponentBinding binding = new TextComponentBinding(component, form);
    component.getDocument().addDocumentListener(binding);
    return binding;
  }

  /**
   * {@inheritDoc}
   * @see DocumentListener#insertUpdate(DocumentEvent)
   */
  public void insertUpdate (DocumentEvent e)
  {
    textUpdated(e);
  }

  /**
   * {@inheritDoc}
   * @see DocumentListener#removeUpdate(DocumentEvent)
   */
  public void removeUpdate (DocumentEvent e)
  {
    textUpdated(e);
  }

  /**
   * {@inheritDoc}
   * @see DocumentListener#changedUpdate(DocumentEvent)
   */
  public void changedUpdate (DocumentEvent e)
  {
    textUpdated(e);
  }

  private void textUpdated (final DocumentEvent e)
  {
    try{
      Worker.post(new Task(){
        public Object run () throws Exception {
          Document document = e.getDocument();
          String value = document.getText(0, document.getLength());
          boolean valid = ValidationUtils.validate(component, value);
          form.setValue(TextComponentBinding.this, component, value, valid);
          return null;
        }
      });
    }catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
}
