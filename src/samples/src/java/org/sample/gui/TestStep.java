/**
 * Copyright (c) 2005 - 2008
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
package org.sample.gui;

import java.awt.Component;

import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.formic.Installer;

import org.formic.wizard.form.GuiForm;

import org.formic.wizard.form.validator.ValidatorBuilder;

import org.formic.wizard.step.AbstractGuiStep;

public class TestStep
  extends AbstractGuiStep
{
  /**
   * Constructs this step.
   */
  public TestStep (String name, Properties properties)
  {
    super(name, properties);
  }

  public Component init ()
  {
    JPanel panel = new JPanel();
    JTextField nameField = new JTextField();
    JTextField locationField = new JTextField();

    GuiForm form = createForm();

    String name = fieldName("name");
    String location = fieldName("location");
    panel.setLayout(new MigLayout("wrap 2"));
    panel.add(form.createMessagePanel(), "span");
    panel.add(new JLabel(Installer.getString(name)));
    panel.add(nameField, "width 150!");
    panel.add(new JLabel(Installer.getString(location)));
    panel.add(locationField, "width 150!");

    form.bind(name, nameField, new ValidatorBuilder().required().validator());
    form.bind(location, locationField);

    return panel;
  }
}
