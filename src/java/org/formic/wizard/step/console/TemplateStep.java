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
package org.formic.wizard.step.console;

import java.util.Properties;

import charva.awt.BorderLayout;
import charva.awt.Component;

import charvax.swing.JPanel;
import charvax.swing.JScrollPane;
import charvax.swing.JTextArea;

import charvax.swing.border.EmptyBorder;

import org.formic.Installer;

import org.formic.wizard.impl.console.ConsoleWizard;

import org.formic.wizard.step.AbstractConsoleStep;

import org.formic.wizard.step.shared.TemplateStepAction;

/**
 * Wizard step that displays an evaluated velocity template in a text area.
 * <p/>
 * <b>Resource</b>
 * <table class="properties">
 *   <tr>
 *     <th>Name</th><th>Description</th>
 *     <th>Required</th><th>Possible Values</th><th>Default</th>
 *   </tr>
 *   <tr>
 *     <td>text</td>
 *     <td>Resource containing the template to display.</td>
 *     <td>yes</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 * </table>
 *
 * @author Eric Van Dewoestine
 * @version $Revision$
 */
public class TemplateStep
  extends AbstractConsoleStep
{
  private String text;
  private TemplateStepAction action;
  private JTextArea area;

  /**
   * Constructs the template step.
   */
  public TemplateStep (String name, Properties properties)
  {
    super(name, properties);
    this.action = new TemplateStepAction();

    String textKey = getName() + ".text";
    text = Installer.getString(textKey);
    if(text == null){
      throw new IllegalArgumentException(
          Installer.getString(RESOURCE_REQUIRED, textKey, getName()));
    }
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.step.ConsoleStep#init()
   */
  public Component init ()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    area = new JTextArea();
    area.setColumns(ConsoleWizard.getFrame().getSize().width - 20);
    area.setRows(ConsoleWizard.getFrame().getSize().height - 10);
    area.setEditable(false);
    area.setText(action.processTemplate(text));

    JScrollPane scroll = new JScrollPane();
    scroll.setViewportView(area);
    panel.add(scroll, BorderLayout.CENTER);
    panel.setBorder(new EmptyBorder(1, 1, 1, 1));

    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#displayed()
   */
  public void displayed ()
  {
    area.setText(action.processTemplate(text));
    area.setCaretPosition(0);
  }
}
