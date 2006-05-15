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
package org.formic.wizard;

import java.io.IOException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.taskdefs.condition.ConditionBase;

import org.formic.Installer;
import org.formic.Log;

import org.formic.ant.type.Branch;
import org.formic.ant.type.Path;
import org.formic.ant.type.Step;

import org.formic.wizard.impl.ConsoleWizard;
import org.formic.wizard.impl.ConsoleWizardStep;
import org.formic.wizard.impl.GuiWizard;
import org.formic.wizard.impl.GuiWizardStep;

import org.pietschy.wizard.WizardModel;

import org.pietschy.wizard.models.BranchingPath;
import org.pietschy.wizard.models.Condition;
import org.pietschy.wizard.models.MultiPathModel;
import org.pietschy.wizard.models.SimplePath;

/**
 * Class for building wizards.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class WizardBuilder
{
  private static Properties steps = new Properties();
  private static boolean consoleMode;

  /**
   * Loads step name to step class mappings from the supplied resource.
   *
   * @param _resource The resource.
   */
  public static void loadSteps (String _resource)
  {
    try{
      steps.load(Installer.class.getResourceAsStream(_resource));
    }catch(NullPointerException npe){
      throw new RuntimeException(
          Installer.getString("resource.not.found", _resource));
    }catch(IOException ioe){
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Builds a wizard from the supplied list of paths.
   * <p/>
   * The first path in the list is expected to be the main path.
   *
   * @param _paths The list of paths.
   * @param _consoleMode true if running in console mode, false otherwise.
   * @return The Wizard.
   */
  public static Wizard build (List _paths, boolean _consoleMode)
  {
    consoleMode = _consoleMode;

    if(_paths.size() == 0){
      throw new BuildException("No paths defined.");
    }

    Path main = (Path)_paths.get(0);
    Map paths = new HashMap();
    for (int ii = 1; ii < _paths.size(); ii++){
      Path path = (Path)_paths.get(ii);
      paths.put(path.getName(), path);
    }

    org.pietschy.wizard.models.Path mainPath =
      buildPath(main, paths, new HashMap());

    if(_consoleMode){
      return new ConsoleWizard(new MultiPathModel(mainPath));
    }
    return new GuiWizard(new MultiPathModel(mainPath));
  }

  /**
   * Builds a wizard path from the supplied ant path.
   *
   * @param _path The ant path.
   * @param _paths A map of all other paths (except the main one).
   * @param _built A map of all paths that have been built.
   * @return The wizard path.
   */
  private static org.pietschy.wizard.models.Path buildPath (
      Path _path, Map _paths, Map _built)
  {
    org.pietschy.wizard.models.Path path = null;
    if(_path.getBranches().size() > 0){
      path = new BranchingPath();

      // add branches
      for (Iterator ii = _path.getBranches().iterator(); ii.hasNext();){
        Branch branch = (Branch)ii.next();
        if(!_paths.containsKey(branch.getPath())){
          throw new BuildException(
            "No path '" + branch.getPath() + "' found for branch in '" +
            _path.getName() + "'");
        }

        Log.debug("Adding branch '" + branch.getPath() +
            "' to path '" + _path.getName() + "'");
        org.pietschy.wizard.models.Path branchPath =
          (org.pietschy.wizard.models.Path)_built.get(branch.getPath());
        if(branchPath == null){
          branchPath = buildPath(
              (Path)_paths.get(branch.getPath()), _paths, _built);
        }

        ((BranchingPath)path).addBranch(branchPath, new WizardCondition(branch));
      }
    }else{
      path = new SimplePath();

      // set next path
      if(_path.getNextpath() != null){
        if(!_paths.containsKey(_path.getNextpath())){
          throw new BuildException(
            "No path '" + _path.getNextpath() +
            "' found for nextpath attribute on path '" + _path.getName() + "'");
        }

        Log.debug("Setting next path for '" + _path.getName() +
            "' to '" + _path.getNextpath() + "'");

        org.pietschy.wizard.models.Path nextPath =
          (org.pietschy.wizard.models.Path)_built.get(_path.getNextpath());
        if(nextPath == null){
          nextPath = buildPath(
              (Path)_paths.get(_path.getNextpath()), _paths, _built);
        }

        ((SimplePath)path).setNextPath(nextPath);
      }
    }

    // add steps
    for (Iterator ii = _path.getSteps().iterator(); ii.hasNext();){
      Step step = (Step)ii.next();
      Log.debug("Adding step '" + step.getName() +
          " to path '" + _path.getName() + "'");
      path.addStep((org.pietschy.wizard.WizardStep)
          getStep(step.getName(), step.getProperties()));
    }

    _built.put(_path.getName(), path);
    return path;
  }

  /**
   * Gets an instance of the step with the supplied name.
   *
   * @param _name The step name.
   * @param _properties The step properties.
   *
   * @return The step.
   */
  private static Object getStep (String _name, Properties _properties)
  {
    try{
      String classname = steps.getProperty(_name);
      if(classname == null){
        throw new RuntimeException(
            Installer.getString("step.not.found", _name));
      }
      Constructor constructor =
        Class.forName(classname).getConstructor(
            new Class[]{String.class, Properties.class});
      WizardStep step = (WizardStep)
        constructor.newInstance(new Object[]{_name, _properties});

      if(consoleMode){
        return new ConsoleWizardStep(step);
      }
      return new GuiWizardStep(step);
    }catch(InvocationTargetException ite){
      Throwable target = ite.getTargetException();
      if(target instanceof IllegalArgumentException){
        throw (IllegalArgumentException)target;
      }
      throw new RuntimeException(target);
    }catch(RuntimeException re){
      throw re;
    }catch(Exception e){
      throw new RuntimeException(
          Installer.getString("step.error.loading", _name), e);
    }
  }

  /**
   * Implementation of {@link Condition} that delegates to an ant condition.
   */
  private static class WizardCondition
    implements Condition
  {
    private ConditionBase antCondition;

    /**
     * Constructs a new instance.
     *
     * @param antCondition The antCondition for this instance.
     */
    public WizardCondition (ConditionBase antCondition)
    {
      this.antCondition = antCondition;
    }

    /**
     * {@inheritDoc}
     * @see org.pietschy.wizard.models.Condition#evaluate(WizardModel)
     */
    public boolean evaluate (WizardModel _model)
    {
      return ((org.apache.tools.ant.taskdefs.condition.Condition)
          antCondition).eval();
    }
  }
}
