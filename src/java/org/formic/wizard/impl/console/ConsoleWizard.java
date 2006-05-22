/**
 * Formic installer framework.
 * Copyright (C) 2004 - 2006  Eric Van Dewoestine
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
package org.formic.wizard.impl.console;

import charva.awt.BorderLayout;
import charva.awt.Dimension;
import charva.awt.FlowLayout;
import charva.awt.Toolkit;

import charva.awt.event.ActionEvent;
import charva.awt.event.ActionListener;
import charva.awt.event.WindowAdapter;
import charva.awt.event.WindowEvent;

import charvax.swing.BorderFactory;
import charvax.swing.JButton;
import charvax.swing.JFrame;
import charvax.swing.JLabel;
import charvax.swing.JOptionPane;
import charvax.swing.JPanel;
import charvax.swing.JScrollPane;
import charvax.swing.JSeparator;

import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

import org.formic.Installer;

import org.formic.wizard.Wizard;

import org.formic.wizard.console.dialog.Dialogs;

import org.pietschy.wizard.WizardModel;

/**
 * Wizard for console installers.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class ConsoleWizard
  implements Wizard
{
  private static JFrame frame;

  private Semaphore semaphore = new Semaphore(1);

  private WizardModel model;
  private boolean canceled;

  private JButton previousButton;
  private JButton nextButton;
  private JButton lastButton;
  private JButton finishButton;
  private JButton cancelButton;
  private JButton closeButton;

  /**
   * Constructs a new instance.
   */
  public ConsoleWizard (WizardModel _model)
  {
    model = _model;
    try{
      semaphore.acquire();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.Wizard#showWizard()
   */
  public void showWizard ()
  {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    int width = Installer.getDimension().width / 7;
    int height = Installer.getDimension().height / 12;

    String error = null;
    if(screen.width < width){
      error = Installer.getString("console.width.min",
          new Integer(screen.width), new Integer(width));
    }

    if(screen.height < height){
      error = Installer.getString("console.height.min",
          new Integer(screen.height), new Integer(height));
    }

    Dimension dimension = new Dimension(width, height);
    // Below will screw up error dialog in some dimensions.
        /*Math.max(width, screen.width),
        Math.max(height, screen.height));*/

    frame = new JFrame(Installer.getString("title"));
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter(){
      public void WindowClosing (WindowEvent _event){
        canceled = true;
      }
    });

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(createInfoPanel(), BorderLayout.NORTH);
    mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
    if(error == null){
      mainPanel.add(createStepPanel(), BorderLayout.CENTER);
      frame.setSize(dimension);
    }else{
      frame.setSize(screen);
    }

    frame.add(mainPanel);
    frame.setVisible(true);

    Dialogs.showError(new Exception("test"));

    if(error != null){
      Dialogs.showError(error);
      close(true);
    }
  }

  /**
   * Creates the info panel on the installation wizard.
   *
   * @return The info panel.
   */
  private JPanel createInfoPanel()
  {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(
        BorderFactory.createLineBorder(Toolkit.getDefaultForeground()));

    JLabel title = new JLabel("Title");
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    titlePanel.add(title);

    JLabel summary = new JLabel("Summary");
    JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    summaryPanel.add(summary);

    panel.add(titlePanel, BorderLayout.NORTH);
    panel.add(summaryPanel, BorderLayout.CENTER);

    return panel;
  }

  /**
   * Creates the step panel on the installation wizard.
   *
   * @return The step panel.
   */
  private JPanel createStepPanel()
  {
    JScrollPane pane = new JScrollPane();
    //pane.setViewportView(new JLabel("Step"));

    JPanel panel = new JPanel(new BorderLayout());
    //panel.add(pane, BorderLayout.CENTER);
    return panel;
  }

  /**
   * Creates the button panel on the installation wizard.
   *
   * @return The button panel.
   */
  private JPanel createButtonPanel()
  {
    JPanel panel = new JPanel(new BorderLayout());

    previousButton = new JButton("Previous");
    nextButton = new JButton("Next");
    finishButton = new JButton("Finish");

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed (ActionEvent _event){
        close(true);
      }
    });

    JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 3));
    buttonBar.add(previousButton);
    buttonBar.add(nextButton);
    buttonBar.add(finishButton);
    buttonBar.add(cancelButton);

    panel.add(new JSeparator(), BorderLayout.NORTH);
    panel.add(buttonBar, BorderLayout.CENTER);

    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.Wizard#waitFor()
   */
  public void waitFor ()
  {
    try{
      semaphore.acquire();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.Wizard#wasCanceled()
   */
  public boolean wasCanceled ()
  {
    return canceled;
  }

  /**
   * Close the wizard.
   *
   * @param _canceled true if the wizard was canceled.
   */
  public void close (boolean _canceled)
  {
    canceled = _canceled;
    //frame.setVisible(false);
    Toolkit.getDefaultToolkit().close();
    try{
      semaphore.release();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Gets the frame for this instance.
   *
   * @return The frame.
   */
  public static JFrame getFrame ()
  {
    return frame;
  }
}
