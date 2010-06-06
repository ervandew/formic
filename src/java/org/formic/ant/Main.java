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
package org.formic.ant;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;

import org.apache.tools.ant.launch.AntMain;

import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.ProxySetup;

import org.formic.ant.util.AntUtils;

public class Main
  implements AntMain
{
  private Vector targets = new Vector();
  private int msgOutputLevel = Project.MSG_INFO;
  private boolean proxy = false;
  private Properties definedProps = new Properties();
  private String loggerClassname = null;
  private String buildFile = null;

  public static void main(String[] args){
    String[] defaultArgs = new String[]{
      "-logger", "org.formic.ant.logger.Log4jLogger",
      "-buildfile", "installer.xml",
    };

    String[] antArgs = null;
    if (args.length == 0){
      antArgs = new String[defaultArgs.length + 1];
      antArgs[antArgs.length - 1] = "installer";
    }else{
      antArgs = new String[defaultArgs.length + args.length];
      System.arraycopy(args, 0, antArgs, defaultArgs.length, args.length);
    }
    System.arraycopy(defaultArgs, 0, antArgs, 0, defaultArgs.length);

    new Main().startAnt(antArgs, null, null);
  }

  /**
   * {@inheritDoc}
   * @see AntMain#startAnt(String[],Properties,ClassLoader)
   */
  public void startAnt(String[] args, Properties userProperties, ClassLoader coreLoader)
  {
    try {
      processArgs(args);
    } catch(Throwable exc) {
      System.err.println(exc.getMessage());
      System.exit(1);
    }

    int exitCode = 1;
    try {
      runBuild(coreLoader);
      exitCode = 0;
    } catch(BuildException be) {
      System.err.println(be.getMessage());
    } catch(Throwable exc) {
      exc.printStackTrace();
      System.err.println(exc.getMessage());
    }
    System.exit(exitCode);
  }

  private void processArgs(String[] args) {
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];

      if (arg.equals("-quiet") || arg.equals("-q")) {
        msgOutputLevel = Project.MSG_WARN;
      } else if (arg.equals("-verbose") || arg.equals("-v")) {
        msgOutputLevel = Project.MSG_VERBOSE;
      } else if (arg.equals("-debug") || arg.equals("-d")) {
        msgOutputLevel = Project.MSG_DEBUG;
      } else if (arg.equals("-buildfile") || arg.equals("-file") || arg.equals("-f")) {
        i = handleArgBuildFile(args, i);
      } else if (arg.startsWith("-D")) {
        i = handleArgDefine(args, i);
      } else if (arg.equals("-logger")) {
        i = handleArgLogger(args, i);
      } else if (arg.equals("-autoproxy")) {
        proxy = true;
      } else {
        targets.addElement(arg);
      }
    }

    if (msgOutputLevel >= Project.MSG_INFO) {
      System.out.println("Buildfile: " + buildFile);
    }
  }

  private int handleArgBuildFile(String[] args, int pos) {
    try {
      buildFile = args[++pos];
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      throw new BuildException(
          "You must specify a buildfile when using the -buildfile argument");
    }
    return pos;
  }

  private int handleArgDefine(String[] args, int argPos) {
    String arg = args[argPos];
    String name = arg.substring(2, arg.length());
    String value = null;
    int posEq = name.indexOf("=");
    if (posEq > 0) {
      value = name.substring(posEq + 1);
      name = name.substring(0, posEq);
    } else if (argPos < args.length - 1) {
      value = args[++argPos];
    } else {
      throw new BuildException("Missing value for property "
          + name);
    }
    definedProps.put(name, value);
    return argPos;
  }

  private int handleArgLogger(String[] args, int pos) {
    if (loggerClassname != null) {
      throw new BuildException(
          "Only one logger class may be specified.");
    }
    try {
      loggerClassname = args[++pos];
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      throw new BuildException(
          "You must specify a classname when using the -logger argument");
    }
    return pos;
  }

  private BuildLogger createLogger() {
    BuildLogger logger = null;
    if (loggerClassname != null) {
      try {
        logger = (BuildLogger) ClasspathUtils.newInstance(
            loggerClassname, Main.class.getClassLoader(),
            BuildLogger.class);
      } catch (BuildException e) {
        System.err.println("The specified logger class "
            + loggerClassname
            + " could not be used because " + e.getMessage());
        throw new RuntimeException();
      }
    } else {
      logger = new DefaultLogger();
    }

    logger.setMessageOutputLevel(msgOutputLevel);
    logger.setOutputPrintStream(System.out);
    logger.setErrorPrintStream(System.err);
    logger.setEmacsMode(false);

    return logger;
  }

  private void runBuild(ClassLoader coreLoader) throws BuildException {
    final Project project = new Project();
    project.setCoreLoader(coreLoader);

    Throwable error = null;

    try {
      project.addBuildListener(createLogger());

      // use a system manager that prevents from System.exit()
      SecurityManager oldsm = null;
      oldsm = System.getSecurityManager();

      //SecurityManager can not be installed here for backwards
      //compatibility reasons (PD). Needs to be loaded prior to
      //ant class if we are going to implement it.
      //System.setSecurityManager(new NoExitSecurityManager());
      try {
        project.fireBuildStarted();
        project.init();

        // set user-define properties
        Enumeration e = definedProps.keys();
        while (e.hasMoreElements()) {
          String arg = (String) e.nextElement();
          String value = (String) definedProps.get(arg);
          project.setUserProperty(arg, value);
        }

        project.setKeepGoingMode(false);
        if (proxy) {
          //proxy setup if enabled
          ProxySetup proxySetup = new ProxySetup(project);
          proxySetup.enableProxies();
        }

        AntUtils.configureProjectFromResource(buildFile, project);

        // make sure that we have a target to execute
        if (targets.size() == 0) {
          if (project.getDefaultTarget() != null) {
            targets.addElement(project.getDefaultTarget());
          }
        }

        project.executeTargets(targets);
      } finally {
        // put back the original security manager
        //The following will never eval to true. (PD)
        if (oldsm != null) {
          System.setSecurityManager(oldsm);
        }
      }
    } catch(BuildException be) {
      throw be;
    } catch(Exception exc) {
      throw new BuildException(exc);
    } finally {
      try {
        project.fireBuildFinished(error);
      } catch (Throwable t) {
        // yes, I know it is bad style to catch Throwable,
        // but if we don't, we lose valuable information
        System.err.println("Caught an exception while logging the"
            + " end of the build.  Exception was:");
        t.printStackTrace();
        if (error != null) {
          System.err.println("There has been an error prior to"
              + " that:");
          error.printStackTrace();
        }
        throw new BuildException(t);
      }
    }
  }
}
