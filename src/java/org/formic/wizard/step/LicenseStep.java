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
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URL;

import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.formic.Installer;

import org.formic.wizard.gui.event.HyperlinkListener;

/**
 * Step that displays a license agreement and requires the user to accept it to
 * proceed.
 * <p/>
 * <b>Properties</b>
 * <table class="properties">
 *   <tr>
 *     <th>Name</th><th>Description</th>
 *     <th>Required</th><th>Possible Values</th><th>Default</th>
 *   </tr>
 *   <tr>
 *     <td>license.url</td>
 *     <td>Defines the url containing the license content.</td>
 *     <td>true</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 * </table>
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class LicenseStep
  extends AbstractStep
{
  private static final String ICON = "/images/license.png";

  private static final String ACCEPT = "Accept";
  private static final String DECLINE = "Decline";

  /**
   * Constructs this step.
   */
  public LicenseStep (String _name, Properties _properties)
  {
    super(_name, _properties);

    if(getProperty("license.url") == null){
      throw new IllegalArgumentException(
          Installer.getString("license.url.required"));
    }
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
    JPanel panel = new JPanel();
    try{
      panel.setLayout(new BorderLayout());

      JEditorPane content =
        new JEditorPane(new URL(getProperty("license.url")));
      content.setEditable(false);
      content.addHyperlinkListener(new HyperlinkListener());

      JScrollPane scroll = new JScrollPane(
          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scroll.setViewportView(content);

      ActionListener listener = new ActionListener(){
        public void actionPerformed (ActionEvent _event){
          setValid(ACCEPT.equals(_event.getActionCommand()));
        }
      };

      JRadioButton accept = new JRadioButton(
          Installer.getString("license.accept"));
      accept.setActionCommand(ACCEPT);
      accept.addActionListener(listener);

      JRadioButton decline = new JRadioButton(
          Installer.getString("license.decline"));
      decline.setSelected(true);
      decline.setActionCommand(DECLINE);
      decline.addActionListener(listener);

      ButtonGroup group = new ButtonGroup();
      group.add(accept);
      group.add(decline);

      JPanel radioPanel = new JPanel(new GridLayout(0, 1));
      radioPanel.add(accept);
      radioPanel.add(decline);

      panel.add(scroll, BorderLayout.CENTER);
      panel.add(radioPanel, BorderLayout.SOUTH);

      setValid(false);
    }catch(Exception e){
      throw new RuntimeException(e);
    }
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
