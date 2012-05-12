/**
 * Formic installer framework.
 * Copyright (C) 2012  Eric Van Dewoestine
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
package org.formic.ant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.apache.tools.ant.taskdefs.Move;

import org.apache.velocity.VelocityContext;

import org.apache.velocity.app.Velocity;

import org.formic.wizard.step.shared.TemplateStepAction;

/**
 * Ant task to evaluate a velocity template.
 *
 * @author Eric Van Dewoestine
 */
public class TemplateTask
  extends Task
{
  private File template;
  private File out;
  private boolean move;

  public void execute()
    throws BuildException
  {
    if (template == null || out == null){
      throw new BuildException("Both template and out attributes must be supplied");
    }

    if (template.equals(out)){
      out = new File(out.getAbsolutePath() + ".out");
      move = true;
    }

    HashMap values = new HashMap();

    values.put(TemplateStepAction.INSTALLER_KEY, TemplateStepAction.INSTALLER);
    values.put(TemplateStepAction.OS_KEY, TemplateStepAction.OS);
    values.putAll(getProject().getProperties());

    FileReader reader = null;
    FileWriter writer = null;
    try{
      log("Evaluating template: " + template);
      reader = new FileReader(this.template);
      writer = new FileWriter(this.out);
      VelocityContext context = new VelocityContext(values);
      Velocity.evaluate(context, writer, TemplateTask.class.getName(), reader);
    }catch(Exception e){
      throw new RuntimeException(e);
    }finally{
      IOUtils.closeQuietly(reader);
      IOUtils.closeQuietly(writer);
    }

    if (move){
      Move move = new Move();
      move.setProject(getProject());
      move.setTaskName(getTaskName());
      move.setFile(out);
      move.setTofile(template);
      move.execute();
    }
  }

  public void setTemplate(File template)
  {
    this.template = template;
  }

  public void setOut(File out)
  {
    this.out = out;
  }
}
