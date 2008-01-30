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

import java.io.InputStream;
import java.io.StringWriter;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Properties;

import charva.awt.BorderLayout;
import charva.awt.Component;

import charva.awt.event.ActionEvent;
import charva.awt.event.ActionListener;

import charvax.swing.BoxLayout;
import charvax.swing.ButtonGroup;
import charvax.swing.JPanel;
import charvax.swing.JRadioButton;
import charvax.swing.JScrollPane;
import charvax.swing.JTextArea;

import charvax.swing.border.TitledBorder;

import org.apache.commons.io.IOUtils;

import org.formic.Installer;

import org.formic.wizard.impl.console.ConsoleWizard;

import org.formic.wizard.step.AbstractConsoleStep;

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
  extends AbstractConsoleStep
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
   * @see org.formic.wizard.step.ConsoleStep#init()
   */
  public Component init ()
  {
    JPanel panel = new JPanel(new BorderLayout());

    InputStream in = null;
    StringWriter writer = new StringWriter();
    try{
      in = license.openStream();
      IOUtils.copy(in, writer);
    }catch(Exception e){
      throw new RuntimeException(e);
    }finally{
      IOUtils.closeQuietly(in);
    }

    JTextArea area = new JTextArea(writer.toString());
    area.setEditable(false);
    area.setColumns(ConsoleWizard.getFrame().getSize().width - 5);
    area.setRows(ConsoleWizard.getFrame().getSize().height - 12);

    scrollPane = new JScrollPane();
    scrollPane.setViewportView(area);
    scrollPane.setViewportBorder(
        new TitledBorder(Installer.getString("license.title")));

    ActionListener listener = new ActionListener(){
      public void actionPerformed (ActionEvent _event){
        setValid(ACCEPT.equals(_event.getActionCommand()));
      }
    };

    JRadioButton accept = new JRadioButton(
        Installer.getString("license.accept"));
    accept.setActionCommand(ACCEPT);
    accept.addActionListener(listener);
    //accept.addKeyListener(RadioButtonKeyListener.getInstance());

    JRadioButton decline = new JRadioButton(
        Installer.getString("license.decline"));
    decline.setActionCommand(DECLINE);
    decline.addActionListener(listener);
    //decline.addKeyListener(RadioButtonKeyListener.getInstance());

    ButtonGroup group = new ButtonGroup();
    group.add(accept);
    group.add(decline);

    JPanel radioPanel = new JPanel();
    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
    radioPanel.add(accept);
    radioPanel.add(decline);
    decline.setSelected(true);

    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(radioPanel, BorderLayout.SOUTH);

    setValid(false);

    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#displayed()
   */
  public void displayed ()
  {
    scrollPane.requestFocus();
  }
}
