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

import java.awt.BorderLayout;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.io.IOUtils;

import org.formic.Installer;

import org.formic.event.gui.HyperlinkListener;

import org.formic.util.TemplateUtils;

import org.formic.wizard.impl.console.ConsoleWizard;

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
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public abstract class AbstractTemplateStep
  extends AbstractStep
{
  private String text;
  private String html;

  /**
   * Constructs the template step.
   */
  public AbstractTemplateStep (String name)
  {
    super(name);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initProperties(Properties)
   */
  public void initProperties (Properties properties)
  {
    super.initProperties(properties);

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
   * @see org.formic.wizard.WizardStep#initGui()
   */
  public JComponent initGui ()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    JComponent content = null;
    if(html != null){
      JEditorPane editor = new JEditorPane("text/html", processTemplate(html));
      editor.setEditable(false);
      editor.setOpaque(false);
      editor.addHyperlinkListener(new HyperlinkListener());
      editor.setBorder(null);
      editor.setFocusable(false);
      content = editor;
    }else{
      JTextArea area = new JTextArea(processTemplate(text));
      area.setEditable(false);
      content = area;
    }

    panel.add(content, BorderLayout.CENTER);
    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initConsole()
   */
  public charva.awt.Component initConsole ()
  {
    charvax.swing.JPanel panel = new charvax.swing.JPanel();
    panel.setLayout(new charva.awt.BorderLayout());

    charvax.swing.JTextArea area =
      new charvax.swing.JTextArea(processTemplate(text));
    area.setColumns(ConsoleWizard.getFrame().getSize().width - 20);
    area.setEditable(false);
    panel.add(area, charva.awt.BorderLayout.CENTER);

    return panel;
  }

  /**
   * Processes the supplied template file and returns the result.
   *
   * @param template The template file.
   * @return The template evaluation result.
   */
  protected String processTemplate (String template)
  {
    HashMap values = new HashMap();
    values.put("context", Installer.getContext());
    InputStream in = null;
    try{
      in = AbstractTemplateStep.class.getResourceAsStream(template);
      return TemplateUtils.evaluate(in, values);
    }catch(Exception e){
      throw new RuntimeException(e);
    }finally{
      IOUtils.closeQuietly(in);
    }
  }
}
