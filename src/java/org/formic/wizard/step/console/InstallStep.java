/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2011 Eric Van Dewoestine
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

import java.util.Properties;

import charva.awt.BorderLayout;
import charva.awt.Component;
import charva.awt.FlowLayout;

import charva.awt.event.ActionEvent;

import charvax.swing.AbstractAction;
import charvax.swing.BoxLayout;
import charvax.swing.JButton;
import charvax.swing.JLabel;
import charvax.swing.JPanel;
import charvax.swing.JProgressBar;
import charvax.swing.SwingUtilities;

import charvax.swing.border.TitledBorder;

import org.formic.Installer;

import org.formic.util.dialog.console.ConsoleDialogs;

import org.formic.wizard.step.AbstractConsoleStep;

import org.formic.wizard.step.shared.InstallAction;
import org.formic.wizard.step.shared.InstallAction.InstallListener;

/**
 * Step that runs the background install process and displays the progress for
 * the user.
 *
 * @author Eric Van Dewoestine
 */
public class InstallStep
  extends AbstractConsoleStep
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
   * @see org.formic.wizard.step.ConsoleStep#init()
   */
  public Component init()
  {
    overallLabel = new JLabel(Installer.getString("install.initialize"));
    overallProgress = new JProgressBar();
    overallProgress.setStringPainted(true);

    JPanel overallProgressPanel =
      new JPanel(new BorderLayout());
    overallProgressPanel.setBorder(
        new charvax.swing.border.TitledBorder("Overall Progress"));
    overallProgressPanel.add(overallProgress, BorderLayout.CENTER);

    taskLabel = new charvax.swing.JLabel();
    taskProgress = new charvax.swing.JProgressBar();
    taskProgress.setStringPainted(true);
    taskProgress.setIndeterminate(true);

    JPanel taskProgressPanel = new JPanel(new BorderLayout());
    taskProgressPanel.setBorder(new TitledBorder("Task Progress"));
    taskProgressPanel.add(taskProgress, BorderLayout.CENTER);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    //panel.setBorder(BorderFactory.createEmptyBorder(50, 125, 10, 125));

    panel.add(new JLabel());
    panel.add(overallLabel);
    panel.add(new JLabel());
    panel.add(overallProgressPanel);

    panel.add(new JLabel());

    panel.add(taskLabel);
    panel.add(new JLabel());
    panel.add(taskProgressPanel);
    panel.add(new JLabel());

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    showErrorButton = new JButton();
    showErrorButton.addActionListener(new ShowErrorAction());
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
    new Thread(){
      public void run()
      {
        try{
          InstallAction action = new InstallAction(InstallStep.this);
          action.execute();

          SwingUtilities.invokeLater(new Runnable(){
            public void run() {
              overallProgress.setValue(overallProgress.getMaximum());
              overallLabel.setText(Installer.getString("install.done"));
              taskProgress.setValue(taskProgress.getMaximum());
              taskLabel.setText(Installer.getString("install.done"));

              setCancelEnabled(false);
            }
          });
        }catch(Exception e){
          error = e;
          error.printStackTrace();
          SwingUtilities.invokeLater(new Runnable(){
            public void run() {
              ConsoleDialogs.showError(error);
              overallLabel.setText(
                  "install: " + Installer.getString("error.dialog.text"));
              showErrorButton.setVisible(true);
            }
          });
        }finally{
          SwingUtilities.invokeLater(new Runnable(){
            public void run() {
              taskProgress.setIndeterminate(false);
              setBusy(false);
            }
          });
        }
      }
    }.start();
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
    private static final long serialVersionUID = 1L;

    public ShowErrorAction(){
      super(Installer.getString("install.error.view"));
    }

    public void actionPerformed(ActionEvent e){
      ConsoleDialogs.showError(error);
    }
  }
}
