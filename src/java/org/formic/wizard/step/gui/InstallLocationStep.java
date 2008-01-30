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

import java.awt.Component;

import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.formic.Installer;

import org.formic.wizard.form.GuiForm;

import org.formic.wizard.form.gui.component.FileChooser;

import org.formic.wizard.form.shared.Discoverer;

import org.formic.wizard.form.validator.ValidatorBuilder;

import org.formic.wizard.step.AbstractGuiStep;

/**
 * Step prompting the user to choose an install location.
 *
 * @author Eric Van Dewoestine
 * @version $Revision$
 */
public class InstallLocationStep
  extends AbstractGuiStep
{
  private FileChooser fileChooser;

  /**
   * Constructs the step.
   */
  public InstallLocationStep (String name, Properties properties)
  {
    super(name, properties);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.step.GuiForm#init()
   */
  public Component init ()
  {
    JPanel panel = new JPanel();

    GuiForm form = createForm();
    String location = fieldName("location");
    fileChooser = new FileChooser(JFileChooser.DIRECTORIES_ONLY);

    panel.setLayout(new MigLayout("wrap 2"));
    panel.add(form.createMessagePanel(), "span");
    panel.add(new JLabel(Installer.getString(location)));
    panel.add(fileChooser, "width 300!");

    form.bind(location, fileChooser.getTextField(),
        new ValidatorBuilder().required().validator());

    fileChooser.getTextField().setText(getDefaultValue());

    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#displayed()
   */
  public void displayed ()
  {
    fileChooser.grabFocus();
  }

  /**
   * Gets the default value to use for the file chooser text field.
   *
   * @return The default value.
   */
  protected String getDefaultValue ()
  {
    String discoverer = getProperty("discoverer");
    if(discoverer != null){
      return Discoverer.discover(discoverer);
    }
    return null;
  }
}
