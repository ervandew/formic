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

import org.formic.wizard.impl.models.BranchingPath;
import org.formic.wizard.impl.models.MultiPathModel;
import org.formic.wizard.impl.models.SimplePath;

import org.pietschy.wizard.WizardModel;

import org.pietschy.wizard.models.Condition;

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

    org.pietschy.wizard.models.Path mainPath = buildPath(main, paths);

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
   * @return The wizard path.
   */
  private static org.pietschy.wizard.models.Path buildPath (
      Path _path, Map _paths)
  {
    // add steps and branches.
    org.pietschy.wizard.models.Path path = null;
    for (int ii = _path.getSteps().size() - 1; ii >= 0; ii--){
      Object next = _path.getSteps().get(ii);

      // steps
      if(next instanceof Step){
        Step step = (Step)next;
        Log.debug("Adding step '" + step.getName() +
            " to path '" + _path.getName() + "'");
        SimplePath simplePath = new SimplePath(step.getName(),
            (org.pietschy.wizard.WizardStep)
            getStep(step.getName(), step.getProperties()));
        if(path != null){
          Log.debug("Setting next path for '" + getPathName(simplePath) +
              "' to path '" + getPathName(path) + "'");
          simplePath.setNextPath(path);
        }
        path = simplePath;

      // branches
      }else{
        Branch branch = (Branch)next;
        if(!_paths.containsKey(branch.getPath())){
          throw new BuildException(
            "No path '" + branch.getPath() + "' found for branch in '" +
            _path.getName() + "'");
        }

        Log.debug("Adding branch '" + branch.getPath() +
            "' to path '" + _path.getName() + "'");
        org.pietschy.wizard.models.Path branchPath =
          buildPath((Path)_paths.get(branch.getPath()), _paths);

        if(branchPath instanceof SimplePath){
          Log.debug("Setting next path for '" + getPathName(branchPath) +
              "' to path '" + getPathName(path) + "'");
          ((SimplePath)branchPath).setNextPath(path);
        }

        BranchingPath branchingPath = new BranchingPath(branch.getPath());
        branchingPath.addBranch(0, branchPath, new WizardCondition(branch));
        if(path != null){
          Log.debug("Adding static path for '" + getPathName(branchingPath) +
              "' containing path '" + getPathName(path) + "'");
          branchingPath.addBranch(path, new StaticCondition(true));
        }
        path = branchingPath;
      }
    }

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
   * Gets the name of the supplied path.
   *
   * @param path The path.
   * @return The name.
   */
  private static String getPathName (org.pietschy.wizard.models.Path path)
  {
    if(path instanceof SimplePath){
      return ((SimplePath)path).getName();
    }
    return ((BranchingPath)path).getName();
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

  /**
   * Implementation of {@link Condition} that returns the value it was
   * construted with.
   */
  private static class StaticCondition
    implements Condition
  {
    private boolean value;

    /**
     * Constructs a new instance.
     *
     * @param value The value for this instance.
     */
    public StaticCondition (boolean value)
    {
      this.value = value;
    }

    /**
     * {@inheritDoc}
     * @see org.pietschy.wizard.models.Condition#evaluate(WizardModel)
     */
    public boolean evaluate (WizardModel _model)
    {
      return value;
    }
  }
}
