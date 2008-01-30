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
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.formic.Installer;

import org.formic.util.event.gui.HyperlinkListener;

import org.formic.wizard.step.AbstractGuiStep;

/**
 * Step that displays a license agreement and requires the user to accept it to
 * proceed.
 * <p/>
 * <b>Resource</b>
 * <table class="properties">
 *   <tr>
 *     <th>Name</th><th>Description</th>
 *     <th>Required</th><th>Possible Values</th><th>Default</th>
 *   </tr>
 *   <tr>
 *     <td>url</td>
 *     <td>Defines the url containing the license content.</td>
 *     <td>true</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 * </table>
 *
 * @author Eric Van Dewoestine
 * @version $Revision$
 */
public class LicenseStep
  extends AbstractGuiStep
{
  private static final String ACCEPT = "Accept";
  private static final String DECLINE = "Decline";

  private URL license;
  private JScrollPane scrollPane;

  /**
   * Constructs this step.
   */
  public LicenseStep (String name, Properties properties)
  {
    super(name, properties);

    String licenseKey = getName() + ".url";
    String location = Installer.getString(licenseKey);
    if(location == null){
      throw new IllegalArgumentException(
          Installer.getString(RESOURCE_REQUIRED, licenseKey, getName()));
    }

    try{
      license = location.indexOf("://") != -1 ?
        new URL(location) : getClass().getResource(location);
    }catch(MalformedURLException mue){
      throw new IllegalArgumentException(Installer.getString(
            RESOURCE_INVALID, new Object[]{licenseKey, getName(), mue.getMessage()}));
    }
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.step.GuiStep#init()
   */
  public Component init ()
  {
    JPanel panel = new JPanel();
    try{
      panel.setLayout(new BorderLayout());

      JEditorPane content = new JEditorPane(license);
      content.setEditable(false);
      content.addHyperlinkListener(new HyperlinkListener());

      scrollPane = new JScrollPane(
          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setViewportView(content);

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

      panel.add(scrollPane, BorderLayout.CENTER);
      panel.add(radioPanel, BorderLayout.SOUTH);

      setValid(false);
    }catch(Exception e){
      throw new RuntimeException(e);
    }
    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#displayed()
   */
  public void displayed ()
  {
    ((JComponent)scrollPane.getViewport().getView()).grabFocus();
  }
}
