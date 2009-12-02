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
package org.formic.ant.logger;

import java.io.PrintStream;

import org.apache.commons.lang.StringUtils;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log via log4j.
 *
 * @author Eric Van Dewoestine
 */
public class Log4jLogger
  extends DefaultLogger
{
  private static final Logger logger = LoggerFactory.getLogger("ANT");

  /**
   * {@inheritDoc}
   * @see DefaultLogger#printMessage(String,PrintStream,int)
   */
  protected void printMessage(String message, PrintStream out, int priority)
  {
    switch (priority){
      case Project.MSG_ERR:
        logger.error(message);
        break;
      case Project.MSG_WARN:
        logger.warn(message);
        break;
      case Project.MSG_INFO:
        logger.info(message);
        break;
      case Project.MSG_DEBUG:
      case Project.MSG_VERBOSE:
        logger.debug(message);
        break;
    }
  }

  /**
   * {@inheritDoc}
   * @see org.apache.tools.ant.BuildListener#targetStarted(BuildEvent)
   */
  public void targetStarted(BuildEvent event)
  {
    logEvent(event);
  }

  /**
   * {@inheritDoc}
   * @see org.apache.tools.ant.BuildListener#targetFinished(BuildEvent)
   */
  public void targetFinished(BuildEvent event)
  {
    logEvent(event);
  }

  /**
   * Logs the supplied event.
   *
   * @param event The event to log.
   */
  private void logEvent(BuildEvent event)
  {
    if(event.getMessage() != null){
      logger.info("[{}] {}", event.getTask().getTaskName(), event.getMessage());
    }

    if(event.getException() != null){
      logger.error(StringUtils.EMPTY, event.getException());
    }
  }
}
