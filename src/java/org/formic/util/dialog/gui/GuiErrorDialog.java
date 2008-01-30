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
package org.formic.util.dialog.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 * Error dialog for notifying user of errors.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class GuiErrorDialog
  extends JDialog
{
  private static Icon ERROR;
  private static Icon SHOW_ICON;
  private static String SHOW_LABEL;
  private static Icon HIDE_ICON;
  private static String HIDE_LABEL;

  private static final Dimension DETAILS_SIZE = new Dimension(400, 150);

  private JPanel panel;
  private JButton detailsButton;
  private JScrollPane detailsPane;

  private Dimension smallSize;
  private Dimension largeSize;

  /**
   * Constructs a new GuiErrorDialog.
   *
   * @param title The dialog title.
   * @param message The error message.
   * @param thrown The error detail.
   * @param detail The error detail if no exception supplied.
   */
  GuiErrorDialog (String title, String message, Throwable thrown, String detail)
  {
    super(JOptionPane.getRootFrame(), title, true);

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    panel = new JPanel(new BorderLayout());

    JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
    info.add(new JLabel(ERROR));
    if(message != null && message.length() > 100){
      message = message.substring(0, 97) + "...";
    }
    info.add(new JLabel(message));
    panel.add(info, BorderLayout.NORTH);

    if(thrown != null || detail != null){
      JTextArea detailsArea = new JTextArea();
      detailsArea.setEditable(false);
      detailsArea.setBackground(Color.WHITE);

      if(thrown != null){
        StringWriter stackTrace = new StringWriter();
        thrown.printStackTrace(new PrintWriter(stackTrace));
        detailsArea.setText(stackTrace.toString());
      }else{
        detailsArea.setText(detail);
      }
      detailsArea.setCaretPosition(0);

      detailsPane = new JScrollPane();
      detailsPane.setViewportView(detailsArea);
      detailsPane.setVisible(false);

      JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      detailsButton = new JButton(new DetailsAction());
      detailsButton.setPreferredSize(new Dimension(
            detailsButton.getPreferredSize().width,
            Math.max(detailsButton.getPreferredSize().height, 30)));
      buttons.add(detailsButton);

      panel.add(detailsPane, BorderLayout.CENTER);
      panel.add(buttons, BorderLayout.SOUTH);
    }

    getContentPane().add(panel);

    Dimension size = panel.getPreferredSize();
    int width = size.width + 50;
    int height = size.height + 50;
    setSize(width, height);
    setResizable(false);

    smallSize = getSize();
    largeSize = new Dimension(
        Math.max(DETAILS_SIZE.width, smallSize.width) + 50,
        smallSize.height + DETAILS_SIZE.height);
    setLocationRelativeTo(JOptionPane.getRootFrame());
  }

  /**
   * Sets the resources for the error dialogs.
   *
   * @param bundle The ResourceBundle.
   */
  static void setBundle (ResourceBundle bundle)
  {
    if(bundle != null){
      ERROR = new ImageIcon(GuiDialogs.getImage(bundle, "error.dialog.image"));

      SHOW_ICON = new ImageIcon(
          GuiDialogs.getImage(bundle, "error.dialog.details.show.icon"));
      SHOW_LABEL = bundle.getString("error.dialog.details.show.label");

      HIDE_ICON = new ImageIcon(
          GuiDialogs.getImage(bundle, "error.dialog.details.hide.icon"));
      HIDE_LABEL = bundle.getString("error.dialog.details.hide.label");
    }
  }

  /**
   * Handles showing or hiding of the error details.
   */
  private class DetailsAction
    extends AbstractAction
  {
    private boolean visible;

    /**
     * Constructs a new instance.
     */
    public DetailsAction ()
    {
      super(SHOW_LABEL, SHOW_ICON);
    }

    /**
     * {@inheritDoc}
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed (ActionEvent e)
    {
      detailsPane.setVisible(visible = !visible);

      setResizable(visible);
      if(visible){
        detailsButton.setText(HIDE_LABEL);
        detailsButton.setIcon(HIDE_ICON);
        setSize(largeSize);
      }else{
        detailsButton.setText(SHOW_LABEL);
        detailsButton.setIcon(SHOW_ICON);
        setSize(smallSize);
      }

      GuiErrorDialog.this.validate();
    }
  }
}
