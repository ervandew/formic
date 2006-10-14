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

import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.regex.Pattern;

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

import org.formic.InstallContext;
import org.formic.Installer;

import org.formic.ant.util.AntUtils;

import org.formic.dialog.console.ConsoleDialogs;

import org.formic.dialog.gui.GuiDialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final String ICON = "/images/32x32/install.png";
  private static final String INSTALL_TARGET = "install";
  private static final Pattern STACK_ELEMENT =
    Pattern.compile("^\\s+at .*(.*)$");

  private JProgressBar guiOverallProgress;
  private JProgressBar guiTaskProgress;
  private JLabel guiOverallLabel;
  private JLabel guiTaskLabel;
  private JButton guiShowErrorButton;

  private charvax.swing.JProgressBar consoleOverallProgress;
  private charvax.swing.JProgressBar consoleTaskProgress;
  private charvax.swing.JLabel consoleOverallLabel;
  private charvax.swing.JLabel consoleTaskLabel;
  private charvax.swing.JButton consoleShowErrorButton;

  private List tasks = new ArrayList();
  private List targetStack = new ArrayList();

  private Throwable error;

  /**
   * Constructs this step.
   */
  public InstallStep (String name)
  {
    super(name);
    targetStack.add(INSTALL_TARGET);
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
    guiOverallLabel = new JLabel(Installer.getString("install.initialize"));
    guiOverallLabel.setAlignmentX(0.0f);
    guiOverallProgress = new JProgressBar();
    guiOverallProgress.setAlignmentX(0.0f);
    guiOverallProgress.setStringPainted(true);

    guiTaskLabel = new JLabel();
    guiTaskLabel.setAlignmentX(0.0f);
    guiTaskProgress = new JProgressBar();
    guiTaskProgress.setAlignmentX(0.0f);
    guiTaskProgress.setStringPainted(true);
    guiTaskProgress.setIndeterminate(true);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.setBorder(BorderFactory.createEmptyBorder(50, 125, 10, 125));

    panel.add(guiOverallProgress);
    panel.add(Box.createRigidArea(new Dimension(0, 5)));
    panel.add(guiOverallLabel);

    panel.add(Box.createRigidArea(new Dimension(0, 20)));

    panel.add(guiTaskProgress);
    panel.add(Box.createRigidArea(new Dimension(0, 5)));
    panel.add(guiTaskLabel);
    panel.add(Box.createRigidArea(new Dimension(0, 25)));

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    buttons.setAlignmentX(0.0f);
    guiShowErrorButton = new JButton(new ShowErrorAction());
    guiShowErrorButton.setVisible(false);
    buttons.add(guiShowErrorButton);
    panel.add(buttons);

    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initConsole()
   */
  public charva.awt.Component initConsole ()
  {
    consoleOverallLabel = new charvax.swing.JLabel(
        Installer.getString("install.initialize"));
    consoleOverallProgress = new charvax.swing.JProgressBar();
    consoleOverallProgress.setStringPainted(true);

    charvax.swing.JPanel overallProgressPanel =
      new charvax.swing.JPanel(new charva.awt.BorderLayout());
    overallProgressPanel.setBorder(
        new charvax.swing.border.TitledBorder("Overall Progress"));
    overallProgressPanel.add(consoleOverallProgress,
        charva.awt.BorderLayout.CENTER);

    consoleTaskLabel = new charvax.swing.JLabel();
    consoleTaskProgress = new charvax.swing.JProgressBar();
    consoleTaskProgress.setStringPainted(true);
    consoleTaskProgress.setIndeterminate(true);

    charvax.swing.JPanel taskProgressPanel =
      new charvax.swing.JPanel(new charva.awt.BorderLayout());
    taskProgressPanel.setBorder(
        new charvax.swing.border.TitledBorder("Task Progress"));
    taskProgressPanel.add(consoleTaskProgress,
        charva.awt.BorderLayout.CENTER);

    charvax.swing.JPanel panel = new charvax.swing.JPanel();
    panel.setLayout(
        new charvax.swing.BoxLayout(panel, charvax.swing.BoxLayout.Y_AXIS));

    //panel.setBorder(BorderFactory.createEmptyBorder(50, 125, 10, 125));

    panel.add(new charvax.swing.JLabel());
    panel.add(consoleOverallLabel);
    panel.add(new charvax.swing.JLabel());
    panel.add(overallProgressPanel);

    panel.add(new charvax.swing.JLabel());

    panel.add(consoleTaskLabel);
    panel.add(new charvax.swing.JLabel());
    panel.add(taskProgressPanel);
    panel.add(new charvax.swing.JLabel());

    charvax.swing.JPanel buttons = new charvax.swing.JPanel(
        new charva.awt.FlowLayout(FlowLayout.RIGHT, 0, 0));
    consoleShowErrorButton = new charvax.swing.JButton();
    //consoleShowErrorButton.addActionListener(...);
    consoleShowErrorButton.setVisible(false);
    buttons.add(consoleShowErrorButton);
    panel.add(buttons);

    return panel;
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
    // push context values into ant properties
    InstallContext context = Installer.getContext();
    for (Iterator ii = context.keys(); ii.hasNext();){
      Object key = ii.next();
      Object value = context.getValue(key);
      if(key != null && value != null){
        AntUtils.property(Installer.getProject(), key.toString(), value.toString());
      }
    }

    if(Installer.isConsoleMode()){
      displayedConsole();
    }else{
      displayedGui();
    }
  }

  /**
   * Invoked when this step is displayed in the gui.
   */
  protected void displayedGui ()
  {
    setBusy(true);
    try{
      Worker.post(new foxtrot.Task(){
        public Object run ()
          throws Exception
        {
          Target target = (Target)
            Installer.getProject().getTargets().get(INSTALL_TARGET);
          registerListener(target);
          execute(target);

          guiOverallProgress.setValue(guiOverallProgress.getMaximum());
          guiOverallLabel.setText(Installer.getString("install.done"));

          guiTaskProgress.setValue(guiTaskProgress.getMaximum());
          guiTaskLabel.setText(Installer.getString("install.done"));

          return null;
        }
      });
      setValid(true);
      setCancelEnabled(false);
    }catch(Exception e){
      error = e;
      error.printStackTrace();
      GuiDialogs.showError(error);
      guiOverallLabel.setText(
          INSTALL_TARGET + ": " + Installer.getString("error.dialog.text"));
      guiShowErrorButton.setVisible(true);
      setValid(false);
    }finally{
      setBusy(false);
      guiTaskProgress.setIndeterminate(false);
      Installer.getProject().removeBuildListener(this);
    }
  }

  /**
   * Invoked when this step is displayed in the console.
   */
  protected void displayedConsole ()
  {
    setBusy(true);

    new Thread(){
      public void run ()
      {
        try{
          Target target = (Target)
            Installer.getProject().getTargets().get(INSTALL_TARGET);
          registerListener(target);
          execute(target);

          consoleOverallProgress.setValue(consoleOverallProgress.getMaximum());
          consoleOverallLabel.setText(Installer.getString("install.done"));

          consoleTaskProgress.setValue(consoleTaskProgress.getMaximum());
          consoleTaskLabel.setText(Installer.getString("install.done"));

          setCancelEnabled(false);
        }catch(Exception e){
          error = e;
          error.printStackTrace();
          ConsoleDialogs.showError(error);
          consoleOverallLabel.setText(
              INSTALL_TARGET + ": " + Installer.getString("error.dialog.text"));
          consoleShowErrorButton.setVisible(true);
        }finally{
          consoleTaskProgress.setIndeterminate(false);
          setBusy(false);
          Installer.getProject().removeBuildListener(InstallStep.this);
        }
      }
    }.start();
  }

  /**
   * Registers the listener to monitor build progress.
   *
   * @param target The install target.
   */
  private void registerListener (Target target)
  {
    registerTasks(target.getTasks());

    if(Installer.isConsoleMode()){
      consoleOverallProgress.setMaximum(this.tasks.size());
      consoleOverallProgress.setValue(0);
    }else{
      guiOverallProgress.setMaximum(this.tasks.size());
      guiOverallProgress.setValue(0);
    }

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

/* Breaks ant property scope for some reason.
      if(task instanceof UnknownElement){
        UnknownElement ue = (UnknownElement)task;
        ue.maybeConfigure();
        task = ((UnknownElement)task).getTask();
      }
*/
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
    targetStack.add(e.getTarget().getName());
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#targetFinished(BuildEvent)
   */
  public void targetFinished (BuildEvent e)
  {
    targetStack.remove(targetStack.size() - 1);
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#taskStarted(BuildEvent)
   */
  public void taskStarted (BuildEvent e)
  {
    if(Installer.isConsoleMode()){
      consoleOverallLabel.setText(
          getTargetPath() + " - " + e.getTask().getTaskName());
    }else{
      guiOverallLabel.setText(getTargetPath() + " - " + e.getTask().getTaskName());
    }
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#taskFinished(BuildEvent)
   */
  public void taskFinished (BuildEvent e)
  {
    int index = tasks.indexOf(e.getTask());
    if(index > 0){
      if(Installer.isConsoleMode()){
        consoleOverallProgress.setValue(index + 1);
      }else{
        guiOverallProgress.setValue(index + 1);
      }
    }
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#messageLogged(BuildEvent)
   */
  public void messageLogged (BuildEvent e)
  {
    if(!STACK_ELEMENT.matcher(e.getMessage()).matches()){
      if(Installer.isConsoleMode()){
        consoleTaskLabel.setText(e.getMessage());
      }else{
        guiTaskLabel.setText(e.getMessage());
      }
    }
  }

  /**
   * Converts the current target stack to a canonical path.
   *
   * @return The path.
   */
  private String getTargetPath ()
  {
    StringBuffer buffer = new StringBuffer();
    for (Iterator ii = targetStack.iterator(); ii.hasNext();){
      if(buffer.length() != 0){
        buffer.append('/');
      }
      buffer.append(ii.next());
    }
    return buffer.toString();
  }

  private class ShowErrorAction
    extends AbstractAction
  {
    public ShowErrorAction ()
    {
      super(
          Installer.getString("install.error.view"),
          new ImageIcon(Installer.getImage("/images/16x16/error.png")));
    }

    public void actionPerformed (ActionEvent e){
      GuiDialogs.showError(error);
    }
  }
}
