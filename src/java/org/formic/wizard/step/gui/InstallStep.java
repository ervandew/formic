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
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;

import java.text.Collator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.formic.InstallContext;

import foxtrot.Worker;

import org.formic.Installer;

import org.formic.util.dialog.gui.GuiDialogs;

import org.formic.wizard.step.AbstractGuiStep;

import org.formic.wizard.step.shared.InstallAction;
import org.formic.wizard.step.shared.InstallAction.InstallListener;

/**
 * Step that runs the background install process and displays the progress for
 * the user.
 *
 * @author Eric Van Dewoestine
 */
public class InstallStep
  extends AbstractGuiStep
  implements InstallListener
{
  protected JProgressBar overallProgress;
  protected JProgressBar taskProgress;
  protected JLabel overallLabel;
  protected JLabel taskLabel;

  private JButton showErrorButton;
  private Throwable error;

  /**
   * Constructs the step.
   */
  public InstallStep(String name, Properties properties)
  {
    super(name, properties);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.step.GuiStep#init()
   */
  public Component init()
  {
    overallLabel = new JLabel(Installer.getString("install.initialize"));
    overallLabel.setAlignmentX(0.0f);
    overallProgress = new JProgressBar();
    overallProgress.setAlignmentX(0.0f);
    overallProgress.setStringPainted(true);

    taskLabel = new JLabel();
    taskLabel.setAlignmentX(0.0f);
    taskProgress = new JProgressBar();
    taskProgress.setAlignmentX(0.0f);
    taskProgress.setStringPainted(true);
    taskProgress.setIndeterminate(true);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.setBorder(BorderFactory.createEmptyBorder(50, 25, 10, 25));

    panel.add(overallProgress);
    panel.add(Box.createRigidArea(new Dimension(0, 5)));
    panel.add(overallLabel);

    panel.add(Box.createRigidArea(new Dimension(0, 20)));

    panel.add(taskProgress);
    panel.add(Box.createRigidArea(new Dimension(0, 5)));
    panel.add(taskLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 25)));

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    buttons.setAlignmentX(0.0f);
    showErrorButton = new JButton(new ShowErrorAction());
    showErrorButton.setVisible(false);
    buttons.add(showErrorButton);
    panel.add(buttons);

    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#isBusyAnimated()
   */
  public boolean isBusyAnimated()
  {
    return false;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#displayed()
   */
  public void displayed()
  {
    setBusy(true);
    setPreviousEnabled(false);
    try{
      Worker.post(new foxtrot.Task(){
        public Object run()
          throws Exception
        {
          InstallAction action = new InstallAction(InstallStep.this);
          action.execute();

          return null;
        }
      });

      SwingUtilities.invokeLater(new Runnable(){
        public void run(){
          overallProgress.setValue(overallProgress.getMaximum());
          overallLabel.setText(Installer.getString("install.done"));
          taskProgress.setValue(taskProgress.getMaximum());
          taskLabel.setText(Installer.getString("install.done"));
        }
      });

      setValid(true);
      setCancelEnabled(false);
    }catch(Exception e){
      setError(e);
    }finally{
      taskProgress.setIndeterminate(false);
      setBusy(false);
    }
  }

  /**
   * Invoked when an error occurs during execution of this step.
   *
   * @param error The Throwable error that occured.
   */
  protected void setError(Throwable error)
  {
    this.error = error;
    error.printStackTrace();

    System.out.println("Installer Context (Please include in bug reports):");
    InstallContext context = Installer.getContext();
    ArrayList values = new ArrayList();
    for(Iterator ii = context.keys(); ii.hasNext();){
      Object key = ii.next();
      values.add(key + "=" + context.getValue(key));
    }
    Collections.sort(values, Collator.getInstance());
    for(Iterator ii = values.iterator(); ii.hasNext();){
      System.out.println("  " + ii.next());
    }

    GuiDialogs.showError(error);
    overallLabel.setText("install: " + Installer.getString("error.dialog.text"));
    showErrorButton.setVisible(true);
    setValid(false);
  }

  /**
   * {@inheritDoc}
   * @see InstallListener#installStarted(int)
   */
  public void installStarted(int tasks)
  {
    overallProgress.setMaximum(tasks);
    overallProgress.setValue(0);
  }

  /**
   * {@inheritDoc}
   * @see InstallListener#taskStarted(String)
   */
  public void taskStarted(String info)
  {
    overallLabel.setText(info);
  }

  /**
   * {@inheritDoc}
   * @see InstallListener#taskFinished(int)
   */
  public void taskFinished(int index)
  {
    overallProgress.setValue(index);
  }

  /**
   * {@inheritDoc}
   * @see InstallListener#messageLogged(String)
   */
  public void messageLogged(String message)
  {
    taskLabel.setText(message);
  }

  private class ShowErrorAction
    extends AbstractAction
  {
    public ShowErrorAction()
    {
      super(Installer.getString("install.error.view"),
          new ImageIcon(Installer.getImage("/images/16x16/error.png")));
    }

    public void actionPerformed(ActionEvent e){
      GuiDialogs.showError(error);
    }
  }
}
