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
   * @see org.formic.wizard.WizardStep#initGui()
   */
  public JComponent initGui ()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    JEditorPane content = new JEditorPane(
        "text/html", Installer.getString(name + ".text"));
    content.setEditable(false);
    content.setOpaque(false);
    content.addHyperlinkListener(new HyperlinkListener());
    content.setBorder(null);

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
