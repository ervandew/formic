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
package org.formic.wizard.impl.gui;

import java.awt.Image;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

import org.formic.Installer;

import org.formic.wizard.Wizard;
import org.formic.wizard.WizardStep;

import org.formic.wizard.impl.models.MultiPathModel;

import org.pietschy.wizard.WizardFrameCloser;
import org.pietschy.wizard.WizardModel;

/**
 * Extension to default Wizard that provides a {@link #waitFor()} method.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class GuiWizard
  extends org.pietschy.wizard.Wizard
  implements Wizard, PropertyChangeListener
{
  private Semaphore semaphore = new Semaphore(1);

  private Action cancelAction;
  private Action previousAction;

  /**
   * Constructs a new instance.
   */
  public GuiWizard (WizardModel _model)
  {
    super(_model);
    setDefaultExitMode(org.pietschy.wizard.Wizard.EXIT_ON_FINISH);
    getModel().addPropertyChangeListener(this);

    try{
      semaphore.acquire();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Forces current thread to wait for the wizard thread to complete.
   */
  public void waitFor ()
  {
    try{
      semaphore.acquire();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.Wizard#getPreviousAction()
   */
  public Action getPreviousAction ()
  {
    if(previousAction == null){
      previousAction = super.getPreviousAction();
    }
    return previousAction;
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.Wizard#getCancelAction()
   */
  public Action getCancelAction ()
  {
    if(cancelAction == null){
      cancelAction = new CancelAction(this);
    }
    return cancelAction;
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.Wizard#cancel()
   */
  public void cancel ()
  {
    try{
      semaphore.release();
    }catch(Exception e){
      e.printStackTrace();
    }

    super.cancel();
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.Wizard#confirmAbort()
   */
  protected boolean confirmAbort ()
  {
    /*int response = JOptionPane.showConfirmDialog(this,
        Installer.getString("abort.confirm.text"),
        Installer.getString("abort.confirm.title"),
        JOptionPane.YES_NO_CANCEL_OPTION);
    return response == JOptionPane.YES_OPTION;*/
    return true;
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.Wizard#close()
   */
  public void close ()
  {
    try{
      semaphore.release();
    }catch(Exception e){
      e.printStackTrace();
    }

    super.close();
  }

  /**
   * {@inheritDoc}
   * @see org.pietschy.wizard.Wizard#showInFrame(String,Image)
   */
  public void showInFrame (String title, Image image)
  {
    JFrame window = new JFrame(title);
    window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    window.setIconImage(image);

    ((RootPaneContainer)window).getContentPane().add(this);
    window.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e) {
        getCancelAction().actionPerformed(null);
      }
    });

    WizardFrameCloser.bind(this, window);
    window.pack();
    window.setLocationRelativeTo(null);
    window.setVisible(true);
    window.toFront();
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.Wizard#showWizard()
   */
  public void showWizard ()
  {
    showInFrame(Installer.getString("title"), Installer.getImage());
  }

  /**
   * {@inheritDoc}
   * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange (PropertyChangeEvent evt)
  {
    if (evt.getPropertyName().equals("activeStep")){
      final MultiPathModel model = (MultiPathModel)getModel();
      final org.pietschy.wizard.WizardStep step = model.getActiveStep();
      if(step != null){
        SwingUtilities.invokeLater(new Runnable(){
          public void run (){
            // set whether previous step is enabled or not.
            boolean previousAvailable =
              !model.isFirstStep(step) && !model.isLastStep(step);
            getPreviousAction().setEnabled(previousAvailable);
            model.setPreviousAvailable(previousAvailable);

            // notify step that it is displayed.
            WizardStep ws = ((GuiWizardStep)step).getStep();
            ws.displayed();
          }
        });
      }
    }
  }
}
