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
package org.formic.wizard.impl.console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import charva.awt.BorderLayout;
import charva.awt.Component;
import charva.awt.Dimension;
import charva.awt.FlowLayout;
import charva.awt.Toolkit;

import charva.awt.event.WindowAdapter;
import charva.awt.event.WindowEvent;

import charvax.swing.BorderFactory;
import charvax.swing.JButton;
import charvax.swing.JFrame;
import charvax.swing.JLabel;
import charvax.swing.JPanel;
import charvax.swing.JProgressBar;
import charvax.swing.JScrollPane;
import charvax.swing.JSeparator;
import charvax.swing.JTextArea;
import charvax.swing.SwingUtilities;

import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

import org.apache.commons.lang.WordUtils;

import org.formic.Installer;

import org.formic.wizard.Wizard;
import org.formic.wizard.WizardStep;

import org.formic.wizard.console.dialog.Dialogs;

import org.formic.wizard.impl.models.MultiPathModel;

import org.pietschy.wizard.WizardModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wizard for console installers.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class ConsoleWizard
  implements Wizard, PropertyChangeListener
{
  private static final Logger logger =
    LoggerFactory.getLogger(ConsoleWizard.class);

  private static final String BUSY_TEXT = Installer.getString("busy.text");

  private static JFrame frame;

  private Semaphore semaphore = new Semaphore(1);

  private WizardModel model;
  private org.pietschy.wizard.WizardStep activeStep;

  private boolean canceled;

  private JPanel viewPanel;
  private JPanel hiddenPanel;

  private JButton previousButton;
  private JButton nextButton;
  //private JButton lastButton;
  private JButton finishButton;
  private JButton cancelButton;
  //private JButton closeButton;

  /**
   * Constructs a new instance.
   */
  public ConsoleWizard (WizardModel _model)
  {
    model = _model;
    model.addPropertyChangeListener(this);
    try{
      semaphore.acquire();
    }catch(Exception e){
      logger.error("Error acquiring console wizard lock.", e);
    }
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.Wizard#showWizard()
   */
  public void showWizard ()
  {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    int width = Installer.getDimension().width / 7;
    int height = Installer.getDimension().height / 12;

    String error = null;
    if(screen.width < width){
      error = Installer.getString("console.width.min",
          new Integer(screen.width), new Integer(width));
    }

    if(screen.height < height){
      error = Installer.getString("console.height.min",
          new Integer(screen.height), new Integer(height));
    }

    Dimension dimension = new Dimension(width, height);
    // Below will screw up error dialog in some dimensions.
        /*Math.max(width, screen.width),
        Math.max(height, screen.height));*/

    frame = new JFrame(Installer.getString("title"));
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing (WindowEvent _event){
        canceled = true;
      }
    });

    viewPanel = new JPanel(new BorderLayout());

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(createInfoPanel(), BorderLayout.NORTH);
    mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
    if(error == null){
      mainPanel.add(viewPanel, BorderLayout.CENTER);
      frame.setSize(dimension);
    }else{
      frame.setSize(screen);
    }

    frame.add(mainPanel);
    frame.setVisible(true);

    if(error != null){
      logger.error(error);
      Dialogs.showError(error);
      close(true);
    }else{
      model.reset();
    }
  }

  /**
   * Creates the info panel on the installation wizard.
   *
   * @return The info panel.
   */
  private JPanel createInfoPanel()
  {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(
        BorderFactory.createLineBorder(Toolkit.getDefaultForeground()));

    JLabel title = new JLabel("Title");
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    titlePanel.add(title);

    JLabel summary = new JLabel("Summary");
    JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    summaryPanel.add(summary);

    panel.add(titlePanel, BorderLayout.NORTH);
    panel.add(summaryPanel, BorderLayout.CENTER);

    return panel;
  }

  /**
   * Creates the button panel on the installation wizard.
   *
   * @return The button panel.
   */
  private JPanel createButtonPanel()
  {
    JPanel panel = new JPanel(new BorderLayout());

    previousButton = new JButton(Installer.getString("previous.text"));
    previousButton.addActionListener(new PreviousAction(this));

    nextButton = new JButton(Installer.getString("next.text"));
    nextButton.addActionListener(new NextAction(this));

    finishButton = new JButton(Installer.getString("finish.text"));
    finishButton.addActionListener(new FinishAction(this));

    cancelButton = new JButton(Installer.getString("cancel.text"));
    cancelButton.addActionListener(new CancelAction(this));

    JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 3));
    buttonBar.add(previousButton);
    buttonBar.add(nextButton);
    buttonBar.add(finishButton);
    buttonBar.add(cancelButton);

    panel.add(new JSeparator(), BorderLayout.NORTH);
    panel.add(buttonBar, BorderLayout.CENTER);

    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.Wizard#waitFor()
   */
  public void waitFor ()
  {
    try{
      semaphore.acquire();
    }catch(Exception e){
      logger.error("Error acquiring console wizard lock in waitFor.", e);
    }
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.Wizard#wasCanceled()
   */
  public boolean wasCanceled ()
  {
    return canceled;
  }

  /**
   * Close the wizard.
   *
   * @param _canceled true if the wizard was canceled.
   */
  public void close (boolean _canceled)
  {
    canceled = _canceled;
    //frame.setVisible(false);
    Toolkit.getDefaultToolkit().close();
    try{
      semaphore.release();
    }catch(Exception e){
      logger.error("Error releasing console wizard lock", e);
    }
  }

  /**
   * Gets the model for this instance.
   *
   * @return The model.
   */
  public WizardModel getModel ()
  {
    return this.model;
  }

  /**
   * Gets the frame for this instance.
   *
   * @return The frame.
   */
  public static JFrame getFrame ()
  {
    return frame;
  }

  /**
   * {@inheritDoc}
   * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange (PropertyChangeEvent evt)
  {
    if (evt.getPropertyName().equals(Wizard.ACTIVE_STEP)){
      MultiPathModel model = (MultiPathModel)getModel();
      org.pietschy.wizard.WizardStep step = model.getActiveStep();

      // update step listening.
      if (activeStep != null){
         activeStep.removePropertyChangeListener(this);
      }
      activeStep = step;
      activeStep.addPropertyChangeListener(this);

      if(step != null){
        updateView(((ConsoleWizardStep)activeStep).getConsoleView());

        WizardStep ws = ((ConsoleWizardStep)step).getStep();

        updateButtonStatus(model, ws, step);

        // notify step that it is displayed.
        ws.displayed();
      }
    }else if (evt.getPropertyName().equals(WizardStep.CANCEL)){
      boolean cancelEnabled = ((Boolean)evt.getNewValue()).booleanValue();
      cancelButton.setEnabled(cancelEnabled);
    }else if (evt.getPropertyName().equals(WizardStep.VALID) ||
        evt.getPropertyName().equals(WizardStep.BUSY))
    {
      MultiPathModel model = (MultiPathModel)getModel();
      org.pietschy.wizard.WizardStep step = model.getActiveStep();
      final WizardStep ws = ((ConsoleWizardStep)step).getStep();

      boolean nextEnabled =
        ws.isValid() && !ws.isBusy() && !model.isLastStep(step);
      nextButton.setEnabled(nextEnabled);

      // show inifinite wait for busy state.
      if(evt.getPropertyName().equals(WizardStep.BUSY) && ws.isBusyAnimated()){
        final boolean busy = ((Boolean)evt.getNewValue()).booleanValue();

        SwingUtilities.invokeLater(new Runnable(){
          public void run () {
            if(busy){
              hiddenPanel = (JPanel)viewPanel.getComponents()[0];
              updateView(new BusyPanel(ws));
            }else{
              updateView(hiddenPanel);
              hiddenPanel = null;
            }
          }
        });
      }
    }
  }

  /**
   * Update the view for the current step.
   */
  private void updateView (Component view)
  {
    viewPanel.add(view);
    Component[] components = viewPanel.getComponents();
    for (int ii = 0; ii < components.length; ii++){
      if(components[ii] != view){
        viewPanel.remove(components[ii]);
      }
    }
    viewPanel.validate();
    viewPanel.repaint();
  }

  /**
   * Update the state (enabled / disabled) of the buttons in the button bar.
   */
  private void updateButtonStatus (
      MultiPathModel model, WizardStep ws, org.pietschy.wizard.WizardStep step)
  {
    // set whether previous step is enabled or not.
    boolean previousAvailable =
      !model.isFirstStep(step) &&
      !model.isLastStep(step) &&
      ws.isPreviousEnabled();
    previousButton.setEnabled(previousAvailable);

    // set whether next step is enabled or not.
    boolean nextEnabled =
      ws.isValid() && !ws.isBusy() && !model.isLastStep(step);
    nextButton.setEnabled(nextEnabled);

    // set whether cancel step is enabled or not.
    boolean cancelAvailable =
      !model.isLastStep(step) && ws.isCancelEnabled();
    cancelButton.setEnabled(cancelAvailable);

    // set whether finish step is enabled or not.
    finishButton.setEnabled(model.isLastStep(step));
  }

  /**
   * Panel to be displayed when a busy step supports animation.
   */
  private class BusyPanel
    extends JPanel
  {
    private JButton cancelButton;

    /**
     * Constructs a new BusyPanel.
     *
     * @param ws The WizardStep.
     */
    public BusyPanel (WizardStep ws)
    {
      setLayout(new BorderLayout());

      String message = Installer.getString(ws.getName() + ".busy", BUSY_TEXT);
      JTextArea messageArea = new JTextArea(WordUtils.wrap(message, 50), 3, 50);
      messageArea.setEditable(false);

      JScrollPane messagePane = new JScrollPane(messageArea);
      JPanel centerMessage = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
      centerMessage.add(messagePane);
      add(centerMessage, BorderLayout.NORTH);

      JProgressBar progress = new JProgressBar();
      progress.setStringPainted(true);
      progress.setIndeterminate(true);

      JPanel progressPanel = new JPanel(new BorderLayout());
      progressPanel.setBorder(BorderFactory.createLineBorder(null, 1));
      progressPanel.add(progress, BorderLayout.CENTER);
      progressPanel.setSize(25, 2);

      JPanel centerProgress = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
      centerProgress.add(progressPanel);

      add(centerProgress, BorderLayout.CENTER);
    }
  }
}
