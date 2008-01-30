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
package org.formic.wizard.step.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.lang.StringUtils;

import org.formic.Installer;

import org.formic.util.event.gui.HyperlinkListener;

import org.formic.wizard.step.AbstractGuiStep;

import org.formic.wizard.step.shared.TemplateStepAction;

/**
 * Wizard step that displays an evaluated velocity template in either a text
 * area or html panel depending on the template's content type.
 * <p/>
 * <b>Resource</b>
 * <table class="properties">
 *   <tr>
 *     <th>Name</th><th>Description</th>
 *     <th>Required</th><th>Possible Values</th><th>Default</th>
 *   </tr>
 *   <tr>
 *     <td>text</td>
 *     <td>Resource containing the text version of the template.</td>
 *     <td>one or both of text and html</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 *   <tr>
 *     <td>html</td>
 *     <td>Resource containing the html version of the template.</td>
 *     <td>one or both of text and html</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 * </table>
 *
 * @author Eric Van Dewoestine
 * @version $Revision$
 */
public class TemplateStep
  extends AbstractGuiStep
{
  private String text;
  private String html;
  private TemplateStepAction action;
  private JComponent content;

  /**
   * Constructs the template step.
   */
  public TemplateStep (String name, Properties properties)
  {
    super(name, properties);
    this.action = new TemplateStepAction();

    String textKey = getName() + ".text";
    String htmlKey = getName() + ".html";

    text = Installer.getString(textKey);
    html = Installer.getString(htmlKey);

    if(text == null && html == null){
      throw new IllegalArgumentException(
          Installer.getString(RESOURCE_REQUIRED,
            "(" + textKey + "|" + htmlKey + ")", getName()));
    }
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.step.GuiStep#init()
   */
  public Component init ()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    if(html != null){
      JEditorPane editor = new JEditorPane("text/html", StringUtils.EMPTY);
      editor.setEditable(false);
      editor.setOpaque(false);
      editor.addHyperlinkListener(new HyperlinkListener());
      editor.setBorder(null);
      editor.setFocusable(false);
      content = editor;
      panel.add(editor, BorderLayout.CENTER);
    }else{
      JTextArea area = new JTextArea();
      area.setEditable(false);
      content = area;
      panel.add(area, BorderLayout.CENTER);
    }

    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#displayed()
   */
  public void displayed ()
  {
    if(content instanceof JEditorPane){
      ((JEditorPane)content).setText(action.processTemplate(html));
    }else{
      ((JTextArea)content).setText(action.processTemplate(text));
    }
  }
}
