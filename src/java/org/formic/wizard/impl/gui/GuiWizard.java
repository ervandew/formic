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
package org.formic.wizard.impl.gui;

import java.awt.Component;
import java.awt.Image;
import java.awt.KeyboardFocusManager;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
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

  private org.pietschy.wizard.WizardStep activeStep;

  private ButtonBar buttonBar;

  private PropertyChangeEvent[] events;

  /**
   * Constructs a new instance.
   */
  public GuiWizard (WizardModel _model)
  {
    super(_model);
    setDefaultExitMode(org.pietschy.wizard.Wizard.EXIT_ON_FINISH);
    getModel().addPropertyChangeListener(this);
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addPropertyChangeListener(new FocusChangeHandler());

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
   * @see org.pietschy.wizard.Wizard#createButtonBar()
   */
  protected org.pietschy.wizard.ButtonBar createButtonBar ()
  {
    buttonBar = new ButtonBar(this);
    return buttonBar;
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
    super.cancel();
    try{
      semaphore.release();
    }catch(Exception e){
      e.printStackTrace();
    }
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
   * @see org.formic.wizard.Wizard#showWizard(String)
   */
  public void showWizard (String action)
  {
    showInFrame(Installer.getString(action + ".title"), Installer.getImage());

    fireQueuedEvents();
  }

  /**
   * {@inheritDoc}
   * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange (final PropertyChangeEvent evt)
  {
    SwingUtilities.invokeLater(new Runnable(){
      public void run (){
        MultiPathModel model = (MultiPathModel)getModel();
        org.pietschy.wizard.WizardStep step = model.getActiveStep();

        String property = evt.getPropertyName();

        if (property.equals(Wizard.ACTIVE_STEP)){
          // update step listening.
          if (activeStep != null){
             activeStep.removePropertyChangeListener(GuiWizard.this);
          }
          activeStep = step;
          activeStep.addPropertyChangeListener(GuiWizard.this);

          if(step != null){
            WizardStep ws = ((GuiWizardStep)step).getStep();

            updateButtonStatus(model, ws, step);

            if(buttonBar.getNextButton().isEnabled()){
              buttonBar.getNextButton().grabFocus();
            }

            // notify step that it is displayed.
            ws.displayed();

            updateDefaultButton();
          }
        }else{
          if (property.equals(WizardStep.CANCEL)){
            boolean cancelEnabled = ((Boolean)evt.getNewValue()).booleanValue();
            getCancelAction().setEnabled(cancelEnabled);
          }else if (property.equals(WizardStep.PREVIOUS)){
            boolean previousEnabled = ((Boolean)evt.getNewValue()).booleanValue();
            getPreviousAction().setEnabled(previousEnabled);
          }else if (property.equals(WizardStep.VALID) ||
              property.equals(WizardStep.BUSY))
          {
            if(step != null){
              WizardStep ws = ((GuiWizardStep)step).getStep();
              updateButtonStatus(model, ws, step);
            }
            updateDefaultButton();
          }
        }
      }
    });
  }

  /**
   * Update the state (enabled / disabled) of the buttons in the button bar.
   */
  private void updateButtonStatus (
      MultiPathModel model, WizardStep ws, org.pietschy.wizard.WizardStep step)
  {
    // set whether next step is enabled or not.
    model.setNextAvailable(step.isComplete() && !model.isLastStep(step));

    // set whether previous step is enabled or not.
    boolean previousAvailable =
      !model.isFirstStep(step) &&
      !model.isLastStep(step) &&
      ws.isPreviousEnabled();
    getPreviousAction().setEnabled(previousAvailable);
    //model.setPreviousAvailable(previousAvailable);

    // set whether cancel step is enabled or not.
    boolean cancelAvailable =
      !model.isLastStep(step) && ws.isCancelEnabled();
    getCancelAction().setEnabled(cancelAvailable);
    //model.setCancelAvailable(cancelAvailable);
  }

  /**
   * Sets the default button according to the current state of the step.
   */
  private void updateDefaultButton ()
  {
    if(buttonBar.getNextButton().isEnabled()){
      getRootPane().setDefaultButton(buttonBar.getNextButton());
    }else if(buttonBar.getFinishButton().isEnabled()){
      getRootPane().setDefaultButton(buttonBar.getFinishButton());
    }else{
      getRootPane().setDefaultButton(null);
    }
  }

  /**
   * Set array of events that where queued up prior to full wizard
   * initialization.
   *
   * @param events The events.
   */
  public void setEventQueue (PropertyChangeEvent[] events)
  {
    this.events = events;
  }

  /**
   * Fire the queued up events.
   */
  private void fireQueuedEvents ()
  {
    if(events != null){
      for (int ii = 0; ii < events.length; ii++){
        propertyChange(events[ii]);
      }
    }
  }

  /**
   * FocusChangeHandler to ensure proper button is activated via <enter>.
   */
  private final class FocusChangeHandler
    implements PropertyChangeListener
  {
    public void propertyChange (PropertyChangeEvent event)
    {
      String propertyName = event.getPropertyName();
      if (!"permanentFocusOwner".equals(propertyName)){
        return;
      }

      Component focusOwner = KeyboardFocusManager
        .getCurrentKeyboardFocusManager().getFocusOwner();

      if (focusOwner instanceof JButton){
        getRootPane().setDefaultButton((JButton)focusOwner);
      }else{
        updateDefaultButton();
      }
    }
  }
}
