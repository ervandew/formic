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

import java.awt.BorderLayout;

import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.formic.Installer;

import org.formic.wizard.gui.event.HyperlinkListener;

/**
 * Wizard step that displays a welcome message at the beginning of the
 * installation process.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class WelcomeStep
  extends AbstractStep
{
  private static final String ICON = "/images/home.png";

  private String name;

  /**
   * Constructs the welcome step.
   */
  public WelcomeStep (String _name, Properties _properties)
  {
    super(_name, _properties);
    name = _name;
  }

  /**
   * {@inheritDoc}
   * @see AbstractStep#getIconPath()
   */
  protected String getIconPath ()
  {
    String path = super.getIconPath();
    return path != null ? path : ICON;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initGui()
   */
  public JComponent initGui ()
  {
    String text = Installer.getString(name + ".text");
    String html = Installer.getString(name + ".html");

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    JComponent content = null;
    if(html != null){
      JEditorPane editor = new JEditorPane("text/html", html);
      editor.setEditable(false);
      editor.setOpaque(false);
      editor.addHyperlinkListener(new HyperlinkListener());
      editor.setBorder(null);
      content = editor;
    }else{
      JTextArea area = new JTextArea(text);
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
  public charvax.swing.JComponent initConsole ()
  {
    return null;
  }
}
