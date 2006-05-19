package net.java.swingfx.waitwithstyle;

/**
 * Created to bolt a cancel button onto to the infinite progress panel.  An
 * infiniteProgressAdapter pattern is used to hopefully adapt to the PerformanceInfiniteProgressPanel
 * as well (not yet done).
 * @author Michael Bushe michael@bushe.com
 */
public interface InfiniteProgressAdapter {
    /**
     * Called as the animation is starting.
     */
    void animationStarting();

    /**
     * Called as the animation is stopping.
     */
    void animationStopping();

    /**
     * Allows subcomponents to draw their own widgets
     * @param maxY the bottommost Y already drawn to
     */
    void paintSubComponents(double maxY);

    /**
     * Called when the ramp up ends (cancel button adds it component
     * at this point, otherwise it draws too early).
     */
    void rampUpEnded();
}
