package net.java.swingfx.waitwithstyle;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * An InfiniteProgressAdapter that adds a cancel button to a CancelableAdaptee (the InfiniteProgressPanel or the
 * PerformanceProgressPanel).
 * @author Michael Bushe michael@bushe.com
 */
public class CancelableProgessAdapter implements InfiniteProgressAdapter {
    protected CancelableAdaptee progressPanel;
    protected JLabel textLabel;
    protected JButton cancelButton;
    protected JButton applicationDefaultButton;
    private JRootPane rootPane;

    /**
     * Construct with an adaptee
     * @param progressPanel the adaptee, if null, setAdaptee() can be called later (before animation starting) with
     * a non-null value.
     */
    public CancelableProgessAdapter(CancelableAdaptee progressPanel) {
        setAdaptee(progressPanel);
    }

    /**
     * Must be called with a non-null before any of the adaptee's calls to animationStarting, etc. are called.
     * @param progressPanel
     */
    public void setAdaptee(CancelableAdaptee progressPanel) {
        this.progressPanel = progressPanel;
    }


    /**
     * Adds a cancel listener that will be called back when the the cancel button is clicked.
     * @param listener a cancel callback
     */
    public void addCancelListener(ActionListener listener) {
        if (cancelButton == null) {
            cancelButton = createCancelButton();
        }
        if (cancelButton != null) {
            cancelButton.addActionListener(listener);
        }
    }

    /**
     * Eemoves a cancel listener that would have been called back when the the cancel button was clicked.
     * @param listener a cancel callback
     */
    public void removeCancelListener(ActionListener listener) {
        if (cancelButton != null) {
            cancelButton.removeActionListener(listener);
        }
    }

    /**
     * Overridable to supply your own button.  Overriders should:
     * <ul>
     * <li>Call progresspanel.stop() on action performed.
     * <li>Set the cursor on their button, else the busy cursor will not indicate to the user that the button is clickable.
     * </ul>
     */
    protected JButton createCancelButton() {
        if (progressPanel instanceof JComponent) {
            rootPane = ((JComponent) progressPanel).getRootPane();
            if (rootPane != null) {
                rootPane.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("defaultButton".equals(evt.getPropertyName())) {
                            /*
                             * Track the default button every time it changes so that when the cancel
                             * button becomes the default button, as it must when the user clicks it,
                             * its possible to reset the default button back to being the button that
                             * the application expects it to be...
                             *
                             * Ideally, the cancel button should never even get focus as a focus change
                             * is not transparent to application code. A better scheme might be to set
                             * cancel button to be not focusable and then click the button
                             * programmatically as appropriate.
                             */
                            JButton oldDefaultButton = (JButton) evt.getOldValue();
                            if (oldDefaultButton != cancelButton) {
                                applicationDefaultButton = oldDefaultButton;
                            }
                        }
                    }
                });
            }
        }

        JButton button = new JButton("Cancel");
        button.setCursor(Cursor.getDefaultCursor());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                progressPanel.stop();
                if (rootPane != null) {
                    if (applicationDefaultButton != null) {
                        rootPane.setDefaultButton(applicationDefaultButton);
                    }
                    applicationDefaultButton = null;
                }
            }
        });
        return button;
    }

    /**
     * Called by the CancelableAdaptee (progress panel) when the animation is starting.  Does nothing by default.
     */
    public void animationStarting() {
    }

    /**
     * Called by the CancelableAdaptee (progress panel) when the animation is stopping.  Removes the
     * button from the progressPanel by default.
     */
    public void animationStopping() {
        if (cancelButton != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressPanel.getComponent().remove(cancelButton);
                }
            });
        }
    }

    /**
     * Called by the CancelableAdaptee (progress panel) when it is finished painting itself.
     * <p>
     * By default, paints the cancelButton if it is not null.
     * @param maxY the lowest (on the screen) Y that the adapteee used - you should paint your components before this.
     */
    public void paintSubComponents(double maxY) {
        if (cancelButton != null) {
            int buttonWidth = 80;
            Rectangle cancelButtonBoundedRectangle = new Rectangle((progressPanel.getComponent().getWidth() / 2 - buttonWidth / 2), (int)maxY + 10, 80, 21);
            cancelButton.setBounds(cancelButtonBoundedRectangle);
        }
    }

    /**
     * Called by the CancelableAdaptee (progress panel) when the animation's ramp up (fade in) is over.  Adds the
     * cancel button to the progressPanel by default (if not null), via an invokeLater().
     */
    public void rampUpEnded() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (cancelButton != null) {
                    progressPanel.getComponent().add(cancelButton);
                }
            }
        });
    }


    /**
     * Called to programmatically click the cancel button.  Can be called from any thread.
     */
    public void doCancel() {
        Runnable runner = new Runnable() {
            public void run() {
                if (cancelButton == null) {
                    //could be called programmatically, easy way to simulate, don't care about expense
                    cancelButton = createCancelButton();
                }
                if (cancelButton != null) {
                    cancelButton.doClick();
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runner.run();
        } else {
            SwingUtilities.invokeLater(runner);
        }
    }
}
