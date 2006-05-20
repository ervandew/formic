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
package org.formic.wizard.step;

import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JPanel;

import foxtrot.Job;
import foxtrot.Worker;

import org.apache.tools.ant.Target;

import org.formic.Installer;

/**
 * Step that runs the background install process and displays the progress for
 * the user.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class InstallStep
  extends AbstractStep
{
  private static final String ICON = "/images/install.png";

  /**
   * Constructs this step.
   */
  public InstallStep (String _name, Properties _properties)
  {
    super(_name, _properties);
  }

  /**
   * {@inheritDoc}
   * @see AbstractStep#getIconPath()
   */
  protected String getIconPath ()
  {
    String path = super.getIconPath();
    return path != null ? path : ICON;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initGui()
   */
  public JComponent initGui ()
  {
    return new JPanel();
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initConsole()
   */
  public charvax.swing.JComponent initConsole ()
  {
    return null;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#isBusyAnimated()
   */
  public boolean isBusyAnimated ()
  {
    return false;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#displayed()
   */
  public void displayed ()
  {
    setBusy(true);
    Worker.post(new Job(){
      public Object run () {
        try{
          Target target = (Target)
            Installer.getProject().getTargets().get("install");
          if(target == null){
            throw new IllegalArgumentException(
              Installer.getString("install.target.not.found"));
          }
          target.execute();
        }catch(Exception e){
          e.printStackTrace();
        }
        return null;
      }
    });
    setBusy(false);
  }
}
