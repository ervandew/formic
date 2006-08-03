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
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.InputStream;
import java.io.StringWriter;

import java.net.URL;

import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.apache.commons.io.IOUtils;

import org.formic.Installer;

import org.formic.wizard.gui.event.HyperlinkListener;

import org.formic.wizard.impl.console.ConsoleWizard;

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
  private static final String LICENSE = "license.url";
  private static final String ICON = "/images/32x32/license.png";

  private static final String ACCEPT = "Accept";
  private static final String DECLINE = "Decline";

  private JScrollPane guiScrollPane;
  private charvax.swing.JScrollPane consoleScrollPane;

  /**
   * Constructs this step.
   */
  public LicenseStep (String name, Properties properties)
  {
    super(name, properties);

    if(getProperty(LICENSE) == null){
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

      JEditorPane content = new JEditorPane(getLicenseUrl());
      content.setEditable(false);
      content.addHyperlinkListener(new HyperlinkListener());

      guiScrollPane = new JScrollPane(
          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      guiScrollPane.setViewportView(content);

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

      panel.add(guiScrollPane, BorderLayout.CENTER);
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
  public charva.awt.Component initConsole ()
  {
    charvax.swing.JPanel panel =
      new charvax.swing.JPanel(new charva.awt.BorderLayout());

    InputStream in = null;
    StringWriter writer = new StringWriter();
    try{
      in = getLicenseUrl().openStream();
      IOUtils.copy(in, writer);
    }catch(Exception e){
      throw new RuntimeException(e);
    }finally{
      IOUtils.closeQuietly(in);
    }

    charvax.swing.JTextArea area = new charvax.swing.JTextArea(writer.toString());
    area.setEditable(false);
    area.setColumns(ConsoleWizard.getFrame().getSize().width - 5);
    area.setRows(ConsoleWizard.getFrame().getSize().height - 12);

    consoleScrollPane = new charvax.swing.JScrollPane();
    consoleScrollPane.setViewportView(area);
    consoleScrollPane.setViewportBorder(
        new charvax.swing.border.TitledBorder(
          Installer.getString("license.title")));

    charva.awt.event.ActionListener listener = new charva.awt.event.ActionListener(){
      public void actionPerformed (charva.awt.event.ActionEvent _event){
        setValid(ACCEPT.equals(_event.getActionCommand()));
      }
    };

    charvax.swing.JRadioButton accept = new charvax.swing.JRadioButton(
        Installer.getString("license.accept"));
    accept.setActionCommand(ACCEPT);
    accept.addActionListener(listener);
    //accept.addKeyListener(RadioButtonKeyListener.getInstance());

    charvax.swing.JRadioButton decline = new charvax.swing.JRadioButton(
        Installer.getString("license.decline"));
    decline.setActionCommand(DECLINE);
    decline.addActionListener(listener);
    //decline.addKeyListener(RadioButtonKeyListener.getInstance());

    charvax.swing.ButtonGroup group = new charvax.swing.ButtonGroup();
    group.add(accept);
    group.add(decline);

    charvax.swing.JPanel radioPanel = new charvax.swing.JPanel();
    radioPanel.setLayout(
        new charvax.swing.BoxLayout(radioPanel, charvax.swing.BoxLayout.Y_AXIS));
    radioPanel.add(accept);
    radioPanel.add(decline);
    decline.setSelected(true);

    panel.add(consoleScrollPane, BorderLayout.CENTER);
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
    if(Installer.isConsoleMode()){
      consoleScrollPane.requestFocus();
    }else{
      guiScrollPane.grabFocus();
    }
  }

  /**
   * Gets the URL of the configured license.
   *
   * @return The URL of the license.
   */
  private URL getLicenseUrl ()
    throws Exception
  {
    String license = getProperty(LICENSE);
    if(license.indexOf("://") != -1){
      return new URL(license);
    }
    return getClass().getResource(license);
  }
}
