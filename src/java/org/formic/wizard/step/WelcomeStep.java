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

import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.formic.Installer;

import org.formic.wizard.gui.event.HyperlinkListener;

import org.formic.wizard.impl.console.ConsoleWizard;

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
  protected static final String ICON = "/images/32x32/home.png";

  /**
   * Constructs the welcome step.
   */
  public WelcomeStep (String name, Properties properties)
  {
    super(name, properties);
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
    String text = Installer.getString(getName() + ".text");
    String html = Installer.getString(getName() + ".html");

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    JComponent content = null;
    if(html != null){
      JEditorPane editor = new JEditorPane("text/html", html);
      editor.setEditable(false);
      editor.setOpaque(false);
      editor.addHyperlinkListener(new HyperlinkListener());
      editor.setBorder(null);
      editor.setFocusable(false);
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
  public charva.awt.Component initConsole ()
  {
    String text = Installer.getString(getName() + ".text");

    charvax.swing.JPanel panel = new charvax.swing.JPanel();
    panel.setLayout(new charva.awt.BorderLayout());

    charvax.swing.JTextArea area = new charvax.swing.JTextArea(text);
    area.setColumns(ConsoleWizard.getFrame().getSize().width - 20);
    area.setEditable(false);
    panel.add(area, charva.awt.BorderLayout.CENTER);

    return panel;
  }
}
