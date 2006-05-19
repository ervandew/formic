package net.java.swingfx.waitwithstyle;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * A common interface that the CancelableProgressAdapter can use to hook into.
 * <p>
 * There are multiple panels that handle progress, the CancelableProgressAdapter
 * can hook into any panel that implements this interface.
 * @author Michael Bushe michael@bushe.com
 */
public interface CancelableAdaptee {
    /**
     * Starts the animation.
     */
    public void start();

    /**
     * Stops the animation.
     */
    public void stop();

    /**
     * Sets the text in the animation
     */
    public void setText(String text);

    /**
     * Gets the interface as a JComponent (usually returns "this")
     */
    public JComponent getComponent();

    /**
     * Adds a listener to the cancel button.  Usually delegated to
     * the CancelableProgressAdapter.
     */
    public void addCancelListener(ActionListener listener);

    /**
     * Removes a listener from the cancel button.  Usually delegated to
     * the CancelableProgressAdapter.
     */
    public void removeCancelListener(ActionListener listener);
}
