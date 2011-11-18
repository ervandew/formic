/**
 * Wizard Framework
 * Copyright 2004 - 2011 Andrew Pietsch
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
 *
 * $Id$
 */

package org.pietschy.wizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

/**
 * The wizard class is the main entry point for creating wizards.  Typically you will create
 * the wizard with your own {@link WizardModel} implementation and then call {@link Wizard#showInFrame}
 * or one of the releated methods.
 * <p>
 * In the simplest case, you would subclass {@link org.pietschy.wizard.models.StaticModel} and add a number
 * of {@link WizardStep} instances.
 * <pre>
 *    StaticModel model = new StaticModel();
 *    model.add(new MyFirestStep());
 *    model.add(...);
 *    ...
 *
 *    Wizard wizard = new Wizard(model);
 *    wizard.showInFrame("My Wizard");
 * </pre>
 *
 * @see org.pietschy.wizard.models.StaticModel
 * @see org.pietschy.wizard.models.DynamicModel
 * @see org.pietschy.wizard.models.MultiPathModel
 */
public class
Wizard
extends JPanel
{
   private static final long serialVersionUID = 1L;

   protected static final int BORDER_WIDTH = 8;

   /**
    * When specified as the {@link #setDefaultExitMode(int) defaultExitMode}, this
    * causes the wizard to continue displaying the final step after finished has been pressed.  The
    * wizard is closed when the user presses the close button.  This allows the wizard to display a
    * final confirmation screen.
    *
    * @see #EXIT_ON_FINISH
    */
   public static final int EXIT_ON_CLOSE = 1;

   /**
    * When specified as the {@link #setDefaultExitMode(int) defaultExitMode}, this
    * causes the wizard to exit immediately once finish is pressed and
    * {@link WizardStep#applyState()} has been invoked.
    *
    * @see #EXIT_ON_CLOSE
    */
   public static final int EXIT_ON_FINISH = 2;

   private NextAction nextAction;
   private PreviousAction previousAction;
   private LastAction lastAction;
   private FinishAction finishAction;
   private CancelAction cancelAction;
   private CloseAction closeAction;
   private HelpAction helpAction;

   private HelpBroker helpBroker;

   private WizardStep activeStep;
   private WizardModel model;
   private int defaultExitMode = EXIT_ON_CLOSE;

   private JComponent titleComponent;
   private ButtonBar buttonBar;
   private JPanel viewPanel;
   private JPanel mainContainer;
   private JPanel overviewContainer;

   private boolean overviewVisible = true;

   private boolean canceled = false;

   private PropertyChangeListener viewListener = new PropertyChangeListener()
   {
      public void propertyChange(PropertyChangeEvent evt)
      {
         handleViewChange();
      }
   };


   /**
    * Creates a new Wizard that uses the specified {@link WizardModel}.
    *
    * @param model the model that the wizard is to use.
    */
   public Wizard(WizardModel model)
   {
      if (model == null)
         throw new NullPointerException("models is null");

      this.model = model;
      this.model.addPropertyChangeListener(new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            if (evt.getPropertyName().equals("activeStep"))
            {
               handleStepChange();
            }
         }
      });

      nextAction = new NextAction(this);
      previousAction = new PreviousAction(this);
      lastAction = new LastAction(this);
      finishAction = new FinishAction(this);
      cancelAction = new CancelAction(this);
      closeAction = new CloseAction(this);
      helpAction = new HelpAction(this);

      // initialize all the wizard steps.
      for (Iterator iter = model.stepIterator(); iter.hasNext();)
      {
         ((WizardStep) iter.next()).init(this.model);
      }

      setLayout(new BorderLayout());

      titleComponent = createTitleComponent();
      buttonBar = createButtonBar();

      mainContainer = new JPanel(new BorderLayout());
      overviewContainer = new JPanel(new BorderLayout());

      viewPanel = new JPanel(new BorderLayout());
      viewPanel.setPreferredSize(calculatePreferredStepSize());
      mainContainer.add(titleComponent, BorderLayout.NORTH);
      JPanel p = new JPanel(new BorderLayout());
      p.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
      p.add(viewPanel, BorderLayout.CENTER);
      mainContainer.add(p, BorderLayout.CENTER);
//      mainContainer.add(buttonBar, BorderLayout.SOUTH);
      JPanel commandPanel = new JPanel(new BorderLayout());
      commandPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.NORTH);
      commandPanel.add(buttonBar, BorderLayout.CENTER);
      mainContainer.add(commandPanel, BorderLayout.SOUTH);


      if (model instanceof OverviewProvider)
      {
         p = new JPanel(new BorderLayout());
         p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2),
                                                        BorderFactory.createEtchedBorder()));
//         p.setBorder(new OverviewBorder());
         p.add(((OverviewProvider) model).getOverviewComponent(), BorderLayout.CENTER);
         overviewContainer.add(p, BorderLayout.WEST);
         //overviewContainer.add(Box.createVerticalStrut(buttonBar.getPreferredSize().height + BORDER_WIDTH), BorderLayout.SOUTH);
      }

      add(mainContainer, BorderLayout.CENTER);
      add(overviewContainer, BorderLayout.WEST);

      configureOverviewContainer();

      if (model instanceof HelpBroker)
         setHelpBroker((HelpBroker) model);

      this.model.reset();
   }

   /**
    * Set the time when the wizard exits.  The two allowable values are
    * {@link #EXIT_ON_FINISH} and {@link #EXIT_ON_CLOSE}.
    * <p>
    * The default value is {@link #EXIT_ON_CLOSE}
    *
    * @param defaultExitMode the default exit mode.
    */
   public void
   setDefaultExitMode(int defaultExitMode)
   {
      if (defaultExitMode != EXIT_ON_FINISH && defaultExitMode != EXIT_ON_CLOSE)
         throw new IllegalArgumentException();

      this.defaultExitMode = defaultExitMode;
   }

   /**
    * Gets the exit mode of the wizard, being either {@link #EXIT_ON_FINISH} or
    * {@link #EXIT_ON_CLOSE}
    *
    * @return the default close opertation.
    */
   public int
   getDefaultExitMode()
   {
      return defaultExitMode;
   }

   /**
    * Called by the constructor to create the wizards title component.  The default component will be an instance of
    * {@link DefaultTitleComponent} but subclasses may override to provide a custom implementation.  Typically the
    * title component listens to the wizard model and updates its appearance accordingly.
    *
    * @return the wizards title component.
    * @see #getTitleComponent()
    */
   protected JComponent
   createTitleComponent()
   {
      return new DefaultTitleComponent(this);
   }

   /**
    * Gets the component being used to render the wizards title.  By default this will be an instance of
    * {@link DefaultTitleComponent}.  Subclasses can change the default by overriding {@link #createTitleComponent()}.
    * <p>
    * For example, to activate the gradient background on the default title component you can call.
    * <pre>
    * ((DefaultTitleComponent) wizard.getTitleComponent()).setGradientBackground(true);
    * </pre>
    *
    * @return the wizards title component.
    */
   public JComponent
   getTitleComponent()
   {
      return titleComponent;
   }

   /**
    * Called by the constructor to create the button bar.  Subclasses may override to provide a custom {@link ButtonBar}
    * implementation.
    *
    * @return the wizards {@link ButtonBar}.
    */
   protected ButtonBar
   createButtonBar()
   {
      return new ButtonBar(this);
   }

   /**
    * Resets the wizard.  This method delegates directly to {@link WizardModel#reset}.
    */
   public void
   reset()
   {
      canceled = false;
      getModel().reset();
   };

   /**
    * Gets the models this wizard is using.
    *
    * @return the wizard models.
    */
   public WizardModel
   getModel()
   {
      return model;
   }

   /**
    * Checks the visibily of the overview panel that is displayed on the wizards left
    * panel.  The overview panel will only be displayed if this property is <tt>true</tt> and
    * the current {@link WizardModel} implements {@link OverviewProvider}.
    */
   public boolean
   isOverviewVisible()
   {
      return overviewVisible;
   }

   /**
    * Configures the visibily of the overview panel that is displayed on the wizards left
    * panel.  This method will only have an effect if the current {@link WizardModel} implements
    * {@link OverviewProvider}.
    *
    * @param overviewVisible <tt>true</tt> to display the overview, <tt>false</tt> otherwise.
    */
   public void
   setOverviewVisible(boolean overviewVisible)
   {
      if (this.overviewVisible != overviewVisible)
      {
         boolean old = this.overviewVisible;
         this.overviewVisible = overviewVisible;
         firePropertyChange("overviewVisible", old, overviewVisible);
         configureOverviewContainer();
      }
   }

   private void
   configureOverviewContainer()
   {
      overviewContainer.setVisible(overviewVisible && model instanceof OverviewProvider);
   }

   public void
   setHelpBroker(HelpBroker broker)
   {
      HelpBroker old = this.helpBroker;
      this.helpBroker = broker;
      firePropertyChange("helpBroker", old, broker);
   }

   public HelpBroker
   getHelpBroker()
   {
      return helpBroker;
   }

   /**
    * Returns the action that is bound to the next button.
    */
   public Action
   getNextAction()
   {
      return nextAction;
   }

   /**
    * Returns the action that is bound to the previous button.
    */
   public Action
   getPreviousAction()
   {
      return previousAction;
   }

   /**
    * Returns the action that is bound to the last button.
    */
   public Action
   getLastAction()
   {
      return lastAction;
   }

   /**
    * Returns the action that is bound to the showCloseButton button.
    */
   public Action
   getFinishAction()
   {
      return finishAction;
   }

   /**
    * Returns the action that is bound to the cancel button.
    */
   public Action
   getCancelAction()
   {
      return cancelAction;
   }

   /**
    * Returns the action that is bound to the close button.
    */
   public Action
   getHelpAction()
   {
      return helpAction;
   }

   /**
    * Returns the action that is bound to the close button.
    */
   public Action
   getCloseAction()
   {
      return closeAction;
   }

   /**
    * Marks this wizard as finished.  This will cause the button bar to only display
    * the close button.
    */
   protected void
   showCloseButton()
   {
      buttonBar.showCloseButton(true);
   }

   /**
    * Cancels this wizard.  This method simply fires the {@link WizardListener#wizardCancelled}
    * event.
    */
   public void
   cancel()
   {
      /*WizardStep activeStep = getModel().getActiveStep();
      if (activeStep != null && activeStep.isBusy())
      {
         if (!confirmAbort())
            return;

         activeStep.abortBusy();
      }*/

      canceled = true;
      fireWizardCancelled();
   }

   /**
    * Checks if the wizard was canceled.   This method will return <tt>false</tt> unless the user
    * canceled the wizard.
    *
    * @return <tt>true</tt> if the user canceled the wizard, <tt>false</tt> otherwise.
    */
   public boolean
   wasCanceled()
   {
      return canceled;
   }


   /**
    * This method is called when the user cancels the wizard while the {@link #activeStep} is
    * {@link WizardStep#isBusy busy}.  This method displays a {@link JOptionPane} asking if the
    * user wants to abort the wizard.
    *
    * @return <tt>true</tt> if the user confirms the abort, <tt>false</tt> otherwise.
    */
   protected boolean
   confirmAbort()
   {
      int response = JOptionPane.showConfirmDialog(this, "Cancel the currently active task?", "Abort", JOptionPane.YES_NO_CANCEL_OPTION);
      return response == JOptionPane.YES_OPTION;
   }

   /**
    * Closes this wizard.  This method simply fires the {@link WizardListener#wizardClosed}
    * event.
    */
   public void
   close()
   {
      fireWizardClosed();
   }

   /**
    * Adds a {@link WizardListener} to this wizard.
    *
    * @param l the listener to add.
    */
   public void
   addWizardListener(WizardListener l)
   {
      listenerList.add(WizardListener.class, l);
   }

   /**
    * Removes a {@link WizardListener} from this wizard.
    *
    * @param l the listener to remove.
    */
   public void
   removeWizardListener(WizardListener l)
   {
      listenerList.remove(WizardListener.class, l);
   }


   /**
    * Handles a change in the {@link WizardModel} active step.
    */
   private void
   handleStepChange()
   {

      if (activeStep != null)
      {
         activeStep.removePropertyChangeListener("view", viewListener);
      }

      activeStep = model.getActiveStep();

      activeStep.addPropertyChangeListener("view", viewListener);

// CHANGE: EV
/* OLD CODE
      activeStep.prepare();
      handleViewChange();
// NEW CODE */
      handleViewChange();
      activeStep.prepare();
// END CHANGE
   }

   /**
    * Handles changes in the current {@link WizardStep}s view.
    */
   private void
   handleViewChange()
   {
      viewPanel.removeAll();
      viewPanel.add(activeStep.getView(), BorderLayout.CENTER);
      viewPanel.revalidate();
      viewPanel.repaint();
   }


   /**
    * Caculates the preferred size of the main wizard area based on the {@link WizardStep#getPreferredSize}
    * values of all the wizard steps.
    */
   private Dimension
   calculatePreferredStepSize()
   {
      int w = 0;
      int h = 0;

      for (Iterator iter = getModel().stepIterator(); iter.hasNext();)
      {
         WizardStep step = (WizardStep) iter.next();
         Dimension d = step.getPreferredSize();

         w = Math.max(d.width, w);
         h = Math.max(d.height, h);
      }

      return new Dimension(w, h);
   }


   private void
   fireWizardClosed()
   {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();

      WizardEvent event = null;

      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == WizardListener.class)
         {
            // Lazily create the event:
            if (event == null)
               event = new WizardEvent(this);
            ((WizardListener) listeners[i + 1]).wizardClosed(event);
         }
      }

   }

   private void
   fireWizardCancelled()
   {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();

      WizardEvent event = null;

      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == WizardListener.class)
         {
            // Lazily create the event:
            if (event == null)
               event = new WizardEvent(this);
            ((WizardListener) listeners[i + 1]).wizardCancelled(event);
         }
      }

   }

   /**
    * Displays the wizard in a new {@link JFrame} with the specified title.  The frame will
    * be automatically closed when the wizard is completed or canceled.  This method will
    * not block.
    *
    * @param title the title of the frame.
    * @see #addWizardListener
    * @see #removeWizardListener
    */
   public void
   showInFrame(String title)
   {
      showInFrame(title, null, null);
   }

   /**
    * Displays the wizard in a new {@link JFrame} with the specified title.  The frame will
    * be automatically closed when the wizard is completed or canceled.  This method will
    * not block.
    *
    * @param title      the title of the frame.
    * @param windowIcon the icon to use for the frame.  This is used to configure {@link javax.swing.JFrame#setIconImage(java.awt.Image)}.
    * @see #addWizardListener
    * @see #removeWizardListener
    */
   public void
   showInFrame(String title, Image windowIcon)
   {
      showInFrame(title, windowIcon, null);
   }

   /**
    * Displays the wizard in a new {@link JFrame} with the specified title.  The frame will
    * be automatically closed when the wizard is completed or canceled.  This method will
    * not block.
    *
    * @param title      the title of the frame.
    * @param relativeTo the new {@link JFrame} will be displayed relative to this component. If
    *                   the component is <tt>null</tt>, the window will be centered on the
    *                   desktop as per {@link JWindow#setLocationRelativeTo(java.awt.Component)}.
    * @see #addWizardListener
    * @see #removeWizardListener
    */
   public void
   showInFrame(String title, Component relativeTo)
   {
      showInFrame(title, null, relativeTo);
   }

   /**
    * Displays the wizard in a new {@link JFrame} with the specified title.  The frame will
    * be automatically closed when the wizard is completed or canceled.  This method will
    * not block.
    *
    * @param title      the title of the frame.
    * @param windowIcon the icon to use for the frame.  This is used to configure {@link javax.swing.JFrame#setIconImage(java.awt.Image)}.
    * @param relativeTo the new {@link JFrame} will be displayed relative to this component. If
    *                   the component is <tt>null</tt>, the window will be centered on the
    *                   desktop as per {@link JWindow#setLocationRelativeTo(java.awt.Component)}.
    * @see #addWizardListener
    * @see #removeWizardListener
    */
   public void
   showInFrame(String title, Image windowIcon, Component relativeTo)
   {
      JFrame window = new JFrame(title);
      window.setIconImage(windowIcon);
      showInWindow(window, relativeTo);
   }

   /**
    * Displays the wizard in a new {@link JDialog} with the specified title.  The dialog will
    * be automatically closed when the wizard is completed or canceled.  This method will
    * block if the dialog is modal.
    *
    * @param title  the dialog title.
    * @param parent the component that will own the dialog.
    * @param modal  <tt>true</tt> to make the dialog modal, <tt>false otherwise</tt>.
    * @see #addWizardListener
    * @see #removeWizardListener
    */
   public void
   showInDialog(String title, Component parent, boolean modal)
   {

      JDialog dialog = null;
      if (parent != null)
      {
         Window w = (parent instanceof Window) ? (Window) parent : SwingUtilities.getWindowAncestor(parent);

         if (w instanceof Frame)
         {
            dialog = new JDialog((Frame) w, title, modal);
         }
         else if (w instanceof Dialog)
         {
            dialog = new JDialog((Dialog) w, title, modal);
         }
         else
         {
            throw new IllegalArgumentException("Parent component must be within a Frame or Dialog");
         }

         if (parent instanceof Window)
         {
            dialog = createDialogFor((Window) parent, title, modal);
         }
         else
         {
            dialog = createDialogFor(SwingUtilities.getWindowAncestor(parent), title, modal);
         }
      }
      else
      {
         dialog = new JDialog();
         dialog.setModal(modal);
         dialog.setTitle(title);
      }

      dialog.setLocationRelativeTo(parent);
      showInWindow(dialog, parent);
   }

   private JDialog
   createDialogFor(Window window, String title, boolean modal)
   {
      if (window == null)
         throw new NullPointerException("window is null");

      JDialog dialog = null;
      if (window instanceof Frame)
      {
         dialog = new JDialog((Frame) window, title, modal);
      }
      else if (window instanceof Dialog)
      {
         dialog = new JDialog((Dialog) window, title, modal);
      }

      return dialog;
   }

   /**
    * Displays this wizard in the specified {@link Window} that is positioned relative to the
    * specified component.
    *
    * @param window     the window that will contain the wizard.
    * @param relativeTo the component used to position the window.  If the component is <tt>null</tt>,
    *                   the window will be centered on the desktop as per
    *                   {@link JWindow#setLocationRelativeTo(java.awt.Component)}.
    */
   private void
   showInWindow(Window window, Component relativeTo)
   {
      ((RootPaneContainer) window).getContentPane().add(this);
      window.addWindowListener(new WindowAdapter()
      {
         public void windowClosing(WindowEvent e)
         {
            cancel();
         }
      });

      WizardFrameCloser.bind(this, window);
      window.pack();
      window.setLocationRelativeTo(relativeTo);
      window.setVisible(true);
      window.toFront();
   }

   /*private class
   OverviewBorder
   extends AbstractBorder
   {
      private static final long serialVersionUID = 1L;

      private int width = 5;
      private Insets insets = new Insets(0, 0, 0, 2);

      public boolean isBorderOpaque()
      {
         return true;
      }

      public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
      {
         Color old = g.getColor();

         g.setColor(Color.BLACK);

         g.drawLine(x + width - 1, y, x + width - 1, y + height);

         g.setColor(old);
      }

      public Insets
      getBorderInsets(Component c)
      {
         return insets;
      }
   }*/
}
