/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2010  Eric Van Dewoestine
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

/**
 * Thread to run the external command.
 */
public class CommandExecutor
  extends Thread
{
  private static final Logger logger = Logger.getLogger(CommandExecutor.class);

  protected int returnCode = -1;
  protected String result;
  protected String error;
  protected String[] cmd;
  protected Process process;

  private boolean shutdown;
  private ShutdownHook shutdownHook;

  /**
   * Construct a new instance.
   */
  protected CommandExecutor()
  {
  }

  /**
   * Construct a new instance.
   */
  protected CommandExecutor(String[] cmd)
  {
    this.cmd = cmd;
  }

  /**
   * Execute the supplied command.
   *
   * @param cmd The command to execute.
   */
  public static CommandExecutor execute(String[] cmd)
    throws Exception
  {
    return execute(cmd, -1);
  }

  /**
   * Execute the supplied command.
   *
   * @param cmd The command to execute.
   * @param timeout Timeout in milliseconds.
   * @return The CommandExecutor instance containing the ending state of the
   * process.
   */
  public static CommandExecutor execute(String[] cmd, long timeout)
    throws Exception
  {
    CommandExecutor executor = new CommandExecutor(cmd);
    executor.start();

    if(timeout > 0){
      executor.join(timeout);
    }else{
      executor.join();
    }

    return executor;
  }

  /**
   * Run the thread.
   */
  public void run()
  {
    logger.info(this.toString());
    Runtime runtime = Runtime.getRuntime();
    try{
      process = runtime.exec(cmd);

      shutdownHook = new ShutdownHook();
      try{
        runtime.addShutdownHook(shutdownHook);
      }catch(IllegalStateException ignore){
        // happens if this is called during shutdown
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Thread outThread = createOutThread(out);
      outThread.start();

      ByteArrayOutputStream err = new ByteArrayOutputStream();
      Thread errThread = createErrThread(err);
      errThread.start();

      returnCode = process.waitFor();
      outThread.join(1000);
      errThread.join(1000);

      if (result == null){
        result = out.toString();
      }
      if (error == null){
        error = err.toString();
      }
    }catch(Exception e){
      returnCode = 12;
      error = e.getMessage();
      e.printStackTrace();
    }finally{
      if (shutdownHook != null){
        try{
          runtime.removeShutdownHook(shutdownHook);
        }catch(IllegalStateException ignore){
          // happens if this is called during shutdown
        }
      }
    }
  }

  /**
   * Create the thread that will handle the process's output stream.
   *
   * @param out An OutputStream which can be used to buffer up the entire
   * process output.
   * @return The Thread instance.
   */
  protected Thread createOutThread(final OutputStream out)
  {
    return new Thread(){
      public void run (){
        try{
          IOUtils.copy(process.getInputStream(), out);
        }catch(IOException ioe){
          ioe.printStackTrace();
        }
      }
    };
  }

  /**
   * Create the thread that will handle the process's error stream.
   *
   * @param err An OutputStream which can be used to buffer up the entire
   * process error output.
   * @return The Thread instance.
   */
  protected Thread createErrThread(final OutputStream err)
  {
    return new Thread(){
      public void run (){
        try{
          IOUtils.copy(process.getErrorStream(), err);
        }catch(IOException ioe){
          ioe.printStackTrace();
        }
      }
    };
  }

  /**
   * Destroy this process.
   */
  public void destroy()
  {
    if(process != null){
      process.destroy();
    }
  }

  /**
   * Gets the output of the command.
   *
   * @return The command result.
   */
  public String getResult()
  {
    return result;
  }

  /**
   * Get the return code from the process.
   *
   * @return The return code.
   */
  public int getReturnCode()
  {
    return returnCode;
  }

  /**
   * Gets the error message from the command if there was one.
   *
   * @return The possibly empty error message.
   */
  public String getErrorMessage()
  {
    return error;
  }

  /**
   * Determines if the process was terminated during a shutdown.
   *
   * @return The true if shutdown, false otherwise.
   */
  public boolean isShutdown()
  {
    return this.shutdown;
  }

  public String toString()
  {
    return StringUtils.join(cmd, ' ');
  }

  private class ShutdownHook
    extends Thread
  {
    public void run(){
      logger.info(
          "Terminating process for command: " + Arrays.toString(cmd));
      shutdown = true;
      process.destroy();
    }
  }
}
