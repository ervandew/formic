/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008  Eric Van Dewoestine
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
package org.formic.util.dialog.console;

import java.io.PrintWriter;
import java.io.StringWriter;

import charva.awt.BorderLayout;

import charva.awt.event.ActionEvent;
import charva.awt.event.ActionListener;

import charvax.swing.JButton;
import charvax.swing.JDialog;
import charvax.swing.JPanel;
import charvax.swing.JScrollPane;
import charvax.swing.JTextArea;

import charvax.swing.border.TitledBorder;

import org.apache.commons.lang.WordUtils;

import org.formic.Installer;

import org.formic.wizard.impl.console.ConsoleWizard;

/**
 * Error dialog for notifying user of errors.
 *
 * @author Eric Van Dewoestine
 */
public class ConsoleErrorDialog
  extends JDialog
{
  /**
   * Constructs a new ConsoleErrorDialog.
   *
   * @param title The dialog title.
   * @param message The error message.
   * @param thrown The error detail.
   */
  ConsoleErrorDialog(String title, String message, Throwable thrown)
  {
    super(ConsoleWizard.getFrame(), title);

    JPanel panel = new JPanel(new BorderLayout());

    JTextArea messageArea = new JTextArea(WordUtils.wrap(message, 50), 3, 50);
    messageArea.setEditable(false);

    JScrollPane messagePane = new JScrollPane(messageArea);
    panel.add(messagePane, BorderLayout.NORTH);

    if(thrown != null){
      StringWriter stackTrace = new StringWriter();
      thrown.printStackTrace(new PrintWriter(stackTrace));
      JTextArea detailsArea = new JTextArea(stackTrace.toString(), 10, 50);
      detailsArea.setEditable(false);

      JScrollPane detailsPane = new JScrollPane(detailsArea);
      detailsPane.setViewportBorder(
          new TitledBorder(Installer.getString("error.dialog.details")));

      panel.add(detailsPane, BorderLayout.CENTER);
      setSize(60, 20);
    }else{
      setSize(60, 10);
    }

    JButton closeButton = new JButton(Installer.getString("ok.text"));
    closeButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent _event){
        setVisible(false);
      }
    });
    panel.add(closeButton, BorderLayout.SOUTH);

    getContentPane().add(panel);

    setLocationRelativeTo(ConsoleWizard.getFrame());

    closeButton.requestFocus();
  }
}
