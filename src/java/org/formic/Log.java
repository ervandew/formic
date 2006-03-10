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
package org.formic;

import org.apache.tools.ant.Project;

/**
 * Class used to log messages.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class Log
{
  private static Project project;

  /**
   * Sets the ant project this installer is running under.
   *
   * @param _project The ant project.
   */
  static void setProject (Project _project)
  {
    project = _project;
  }

  /**
   * Logs a debug message.
   *
   * @param _message The message.
   */
  public static void debug (String _message)
  {
    project.log(_message, Project.MSG_DEBUG);
  }

  /**
   * Logs an info message.
   *
   * @param _message The message.
   */
  public static void info (String _message)
  {
    project.log(_message, Project.MSG_INFO);
  }

  /**
   * Logs a warn message.
   *
   * @param _message The message.
   */
  public static void warn (String _message)
  {
    project.log(_message, Project.MSG_WARN);
  }

  /**
   * Logs an error message.
   *
   * @param _message The message.
   */
  public static void error (String _message)
  {
    project.log(_message, Project.MSG_ERR);
  }
}
