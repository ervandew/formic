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
package org.formic.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.apache.velocity.VelocityContext;

import org.apache.velocity.app.Velocity;

/**
 * Utility class for evaluating templates.
 *
 * @author Eric Van Dewoestine
 */
public class TemplateUtils
{
  private static final Logger logger =
    Logger.getLogger(TemplateUtils.class);

  static{
    try{
      // tell velocity to use log4j for all logging
      Properties properties = new Properties();
      properties.setProperty("runtime.log.logsystem.class",
          org.apache.velocity.runtime.log.SimpleLog4JLogSystem.class.getName());
      properties.setProperty("runtime.log.logsystem.log4j.category",
          "org.apache.velocity");
      properties.setProperty("directive.foreach.counter.initial.value", "0");
      properties.setProperty("resource.loader", "file");

      // stop annoying error regarding VM_global_library.vm not found.
      properties.setProperty("velocimacro.library", "");
      Velocity.init(properties);
    }catch(Exception e){
      logger.error("", e);
    }
  }

  /**
   * Evaluates the template supplied w/ the specified values.
   *
   * @param template The template.
   * @param values The template values.
   * @return The evaluation result.
   */
  public static String evaluate(String template, Map values)
    throws Exception
  {
    return evaluate(new ByteArrayInputStream(template.getBytes()), values);
  }

  /**
   * Evaluates the template supplied via the specfied reader w/ the specified
   * values.
   *
   * @param template The template.
   * @param values The template values.
   * @return The evaluation result.
   */
  public static String evaluate(InputStream template, Map values)
    throws Exception
  {
    BufferedReader reader = null;
    StringWriter writer = new StringWriter();
    VelocityContext context = new VelocityContext(values);
    try{
      reader = new BufferedReader(new InputStreamReader(template));
      Velocity.evaluate(context, writer, TemplateUtils.class.getName(), reader);
    }catch(Exception e){
      throw new RuntimeException(e);
    }

    return writer.toString();
  }
}
