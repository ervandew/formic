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

import java.io.InputStream;

import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import org.apache.tools.ant.taskdefs.condition.Os;

import org.formic.Installer;

import org.formic.util.TemplateUtils;

/**
 * Shared logic for gui and console template steps.
 *
 * @author Eric Van Dewoestine
 */
public class TemplateStepAction
{
  private static final String INSTALLER_KEY = "installer";
  private static final Installer INSTALLER = new Installer();
  private static final String OS_KEY = "os";
  private static final Os OS = new Os();

  /**
   * Processes the supplied template file and returns the result.
   *
   * @param template The template file.
   * @return The template evaluation result.
   */
  public String processTemplate(String template)
  {
    HashMap values = new HashMap();

    values.put(INSTALLER_KEY, INSTALLER);
    values.put(OS_KEY, OS);

    InputStream in = null;
    try{
      in = TemplateStepAction.class.getResourceAsStream(template);
      return in != null ?
        TemplateUtils.evaluate(in, values) :
        TemplateUtils.evaluate(template, values);
    }catch(Exception e){
      throw new RuntimeException(e);
    }finally{
      IOUtils.closeQuietly(in);
    }
  }
}
