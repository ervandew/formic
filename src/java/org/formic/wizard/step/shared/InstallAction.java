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
package org.formic.wizard.step.shared;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import org.apache.tools.ant.BuildEvent;
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

/**
 * Action for executing the install task and monitoring its progress.
 *
 * @author Eric Van Dewoestine
 */
public class InstallAction
  implements BuildListener
{
  private static final String INSTALL_TARGET = "install";
  private static final Pattern STACK_ELEMENT =
    Pattern.compile("^\\s+at .*(.*)$");
  private static ArrayList CALL_TASKS = new ArrayList();
  static{
    CALL_TASKS.add("ant");
    CALL_TASKS.add("antcall");
  }

  private Target target;
  private ArrayList tasks = new ArrayList();
  private ArrayList targetStack = new ArrayList();
  private final InstallListener listener;

  /**
   * Constructs a new InstallAction with the supplied InstallListener.
   *
   * @param listener The listener listening to install progress events.
   */
  public InstallAction(InstallListener listener)
  {
    this.listener = listener;
    target = (Target)
      Installer.getProject().getTargets().get(INSTALL_TARGET);
    targetStack.add(INSTALL_TARGET);
    Installer.getProject().addBuildListener(this);
    registerTasks(target.getTasks());
  }

  /**
   * Executes the installation.
   */
  public void execute()
    throws Exception
  {
    if(listener != null){
      SwingUtilities.invokeLater(new Runnable(){
        public void run(){
          listener.installStarted(tasks.size());
        }
      });
    }

    // push context values into ant properties
    InstallContext context = Installer.getContext();
    for (Iterator ii = context.keys(); ii.hasNext();){
      Object key = ii.next();
      Object value = context.getValue(key);
      if(key != null && value != null){
        AntUtils.property(Installer.getProject(), key.toString(), value.toString());
      }
    }

    if(target == null){
      throw new IllegalArgumentException(
        Installer.getString("install.target.not.found"));
    }

    try{
      target.execute();
      tasks.clear();
    }finally{
      Installer.getProject().removeBuildListener(this);
    }
  }

  /**
   * Register the supplied array of tasks.
   *
   * @param tasks The tasks to register.
   */
  private void registerTasks(Task[] tasks)
  {
    for (int ii = 0; ii < tasks.length; ii++){
      Task task = tasks[ii];

      this.tasks.add(task);

      if(task instanceof UnknownElement){
        UnknownElement ue = (UnknownElement)task;
        if(CALL_TASKS.indexOf(ue.getTag()) != -1){
          ue.maybeConfigure();
          task = ue.getTask();
          // set back to unconfigured state to ensure that ant will configure it
          // again when it will pick up proper environment.
          ue.setRealThing(null);
        }
      }

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
   * {@inheritDoc}
   * @see BuildListener#buildStarted(BuildEvent)
   */
  public void buildStarted(BuildEvent e)
  {
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#buildFinished(BuildEvent)
   */
  public void buildFinished(BuildEvent e)
  {
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#targetStarted(BuildEvent)
   */
  public void targetStarted(BuildEvent e)
  {
    targetStack.add(e.getTarget().getName());
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#targetFinished(BuildEvent)
   */
  public void targetFinished(BuildEvent e)
  {
    targetStack.remove(targetStack.size() - 1);
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#taskStarted(BuildEvent)
   */
  public void taskStarted(final BuildEvent e)
  {
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        listener.taskStarted(getTargetPath() + " - " + e.getTask().getTaskName());
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#taskFinished(BuildEvent)
   */
  public void taskFinished(BuildEvent e)
  {
    final int index = tasks.indexOf(e.getTask());
    if(index > 0){
      SwingUtilities.invokeLater(new Runnable(){
        public void run(){
          listener.taskFinished(index + 1);
        }
      });
    }
  }

  /**
   * {@inheritDoc}
   * @see BuildListener#messageLogged(BuildEvent)
   */
  public void messageLogged(final BuildEvent e)
  {
    if(!STACK_ELEMENT.matcher(e.getMessage()).matches()){
      SwingUtilities.invokeLater(new Runnable(){
        public void run(){
          listener.messageLogged(e.getMessage());
        }
      });
    }
  }

  /**
   * Converts the current target stack to a canonical path.
   *
   * @return The path.
   */
  private String getTargetPath()
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

  /**
   * Listener that is notified as the installation progresses.
   */
  public interface InstallListener
  {
    public void installStarted(int tasks);

    public void taskStarted(String taskInfo);

    public void taskFinished(int index);

    public void messageLogged(String message);
  }
}
