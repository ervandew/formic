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
package org.formic.wizard.step;

import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import foxtrot.Job;
import foxtrot.Worker;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;

import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.CallTarget;

import org.formic.Installer;

import org.formic.wizard.gui.dialog.Dialogs;

/**
 * Step that runs the background install process and displays the progress for
 * the user.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class InstallStep
  extends AbstractStep
  implements BuildListener
{
  private static final String ICON = "/images/install.png";

  private JProgressBar overallProgress;
  private JProgressBar taskProgress;
  private JLabel overallLabel;
  private JLabel taskLabel;

  private List tasks = new ArrayList();

  private String targetName = "install";

  private JButton showErrorButton;
  private Throwable error;

  /**
   * Constructs this step.
   */
  public InstallStep (String _name, Properties _properties)
  {
    super(_name, _properties);
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

    panel.setBorder(BorderFactory.createEmptyBorder(50, 125, 10, 125));

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
   * @see org.formic.wizard.WizardStep#initConsole()
   */
  public charva.awt.Component initConsole ()
  {
    return null;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#isBusyAnimated()
   */
  public boolean isBusyAnimated ()
  {
    return false;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#isPreviousEnabled()
   */
  public boolean isPreviousEnabled ()
  {
    return false;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#displayed()
   */
  public void displayed ()
  {
    try{
      setBusy(true);
      Worker.post(new foxtrot.Task(){
        public Object run ()
          throws Exception
        {
          Target target = (Target)
            Installer.getProject().getTargets().get(targetName);
          registerListener(target);
          execute(target);

          overallProgress.setValue(overallProgress.getMaximum());
          overallLabel.setText(Installer.getString("install.done"));

          taskProgress.setValue(taskProgress.getMaximum());
          taskLabel.setText(Installer.getString("install.done"));

          setCancelEnabled(false);
          return null;
        }
      });
      setBusy(false);
    }catch(Exception e){
      error = e;
      error.printStackTrace();
      Dialogs.showError(error);
      overallLabel.setText(
          targetName + ": " + Installer.getString("error.dialog.text"));
      showErrorButton.setVisible(true);
    }finally{
      taskProgress.setIndeterminate(false);
    }
  }

  /**
   * Registers the listener to monitor build progress.
   *
   * @param target The install target.
   */
  private void registerListener (Target target)
  {
    registerTasks(target.getTasks());

    overallProgress.setMaximum(this.tasks.size());
    overallProgress.setValue(0);

    Installer.getProject().addBuildListener(this);
  }

  /**
   * Register the supplied array of tasks.
   *
   * @param tasks The tasks to register.
   */
  private void registerTasks (Task[] tasks)
  {
    for (int ii = 0; ii < tasks.length; ii++){
      Task task = tasks[ii];

      if(task instanceof UnknownElement){
        UnknownElement ue = (UnknownElement)task;
        ue.maybeConfigure();
        task = ((UnknownElement)task).getTask();
      }
      this.tasks.add(task);

      if (task instanceof Ant ||
          task instanceof CallTarget)
      {
        Project project = null;
        String[] targets = null;

        if(task instanceof Ant){
          project = ((Ant)task).getTargetProject();
          targets = ((Ant)task).getTargetNames();
        }else{
          project = ((CallTarget)task).getTargetProject();
          targets = ((CallTarget)task).getTargetNames();
        }
        project.addBuildListener(this);

        for (int jj = 0; jj < targets.length; jj++){
          Target target = (Target)project.getTargets().get(targets[jj]);
          registerTasks(target.getTasks());
        }
      }
      // TODO:  For  AntCallBack, AntFetch, and RunTarget, recursively determine
      // task list.
    }
  }

  /**
   * Executes the install target.
   *
   * @param target The install target.
   */
  private void execute (Target target)
    throws BuildException
  {
    if(target == null){
      throw new IllegalArgumentException(
        Installer.getString("install.target.not.found"));
    }
    target.execute();
    tasks.clear();
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#buildStarted(BuildEvent)
   */
  public void buildStarted (BuildEvent e)
  {
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#buildFinished(BuildEvent)
   */
  public void buildFinished (BuildEvent e)
  {
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#targetStarted(BuildEvent)
   */
  public void targetStarted (BuildEvent e)
  {
    targetName = e.getTarget().getName();
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#targetFinished(BuildEvent)
   */
  public void targetFinished (BuildEvent e)
  {
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#taskStarted(BuildEvent)
   */
  public void taskStarted (BuildEvent e)
  {
    overallLabel.setText(targetName + " - " + e.getTask().getTaskName());
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#taskFinished(BuildEvent)
   */
  public void taskFinished (BuildEvent e)
  {
    int index = tasks.indexOf(e.getTask());
    if(index > 0){
      overallProgress.setValue(index + 1);
    }
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#messageLogged(BuildEvent)
   */
  public void messageLogged (BuildEvent e)
  {
    taskLabel.setText(e.getMessage());
  }

  private class ShowErrorAction
    extends AbstractAction
  {
    public ShowErrorAction ()
    {
      super(
          Installer.getString("install.error.view"),
          new ImageIcon(Installer.getImage("/images/error_small.png")));
    }

    public void actionPerformed (ActionEvent e){
      Dialogs.showError(error);
    }
  }
}
