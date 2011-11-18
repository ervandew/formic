package net.java.swingfx.waitwithstyle;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Window;

import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
//import javax.swing.Timer;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * A InfiniteProgressPanel-like component, but more efficient. This is the preferred class to use unless you need the
 * total control over the appearance that InfiniteProgressPanel gives you.<br /><br />
 * An infinite progress panel displays a rotating figure and a message to notice the user of a long, duration unknown
 * task. The shape and the text are drawn upon a white veil which alpha level (or shield value) lets the underlying
 * component shine through. This panel is meant to be used as a <i>glass pane</i> in the window performing the long
 * operation. <br /><br /> Calling setVisible(true) makes the component visible and starts the animation.
 * Calling setVisible(false) halts the animation and makes the component invisible.
 * Once you've started the animation all the mouse events are intercepted by this panel, preventing them from being
 * forwared to the underlying components. <br /><br /> The panel can be controlled by the <code>setVisible()</code>,
 * method. <br /><br />
 * This version of the infinite progress panel does not display any fade in/out when the animation is
 * started/stopped.<br /><br />
 * Example: <br /><br />
 * <pre>PerformanceInfiniteProgressPanel pane = new PerformanceInfiniteProgressPanel();
 * frame.setGlassPane(pane);
 * pane.setVisible(true);
 * // Do something here, presumably launch a new thread
 * // ...
 * // When the thread terminates:
 * pane.setVisible(false);
 * </pre>
 * @see InfiniteProgressPanel
 * <br /><br />
 * TODO: update this documentation
 *
 * @author Romain Guy
 * @author Henry Story
 * @author Andy DePue
 *
 * @version 1.1
 */

public class SingleComponentInfiniteProgress extends JComponent
    implements /*ActionListener,*/ CancelableAdaptee
{
  private static final long serialVersionUID = 1L;

  private static final double UNSCALED_BAR_SIZE       = 45d;

  public static final int     DEFAULT_NUMBER_OF_BARS  = 12;
  public static final int     DEFAULT_FPS             = 10;
  public static final double  NO_AUTOMATIC_RESIZING   = -1d;
  public static final double  NO_MAX_BAR_SIZE         = -1d;

  private int numBars;
  private double dScale = 1.2d;
  private double resizeRatio = NO_AUTOMATIC_RESIZING;
  private double maxBarSize = 64d;
  private double minBarSize = 4;
  private boolean useBackBuffer;
  private String text;
  protected InfiniteProgressAdapter infiniteProgressAdapter;


  private MouseAdapter mouseAdapter = new MouseAdapter() { };
  private MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter() { };
  private KeyAdapter keyAdapter = new KeyAdapter() { };
  private ComponentAdapter componentAdapter = new ComponentAdapter() {
    public void componentResized(final ComponentEvent e) {
      if(useBackBuffer == true) {
        setOpaque(false);
        imageBuf = null;
        timerTask.iterate = 3;
      }
    }
  };

  private BufferedImage imageBuf = null;
  private Area[] bars;
  private Rectangle barsBounds = null;
  private Rectangle barsScreenBounds = null;
  private AffineTransform centerAndScaleTransform = null;
  private Timer timer;
  private RedrawTask timerTask;
  private int timerInterval;
  private Color[] colors = null;
  private int colorOffset = 0;
  private boolean tempHide = false;


  //
  // CONSTRUCTORS
  //


  /**
   * TODO: finish documentation (see InfiniteProgressPanel)
   */
  public SingleComponentInfiniteProgress()
  {
    this(true);
  }

  public SingleComponentInfiniteProgress(boolean i_bUseBackBuffer)
  {
    this(i_bUseBackBuffer, DEFAULT_NUMBER_OF_BARS);
  }

  public SingleComponentInfiniteProgress(int numBars)
  {
    this(true, numBars, DEFAULT_FPS, NO_AUTOMATIC_RESIZING, NO_MAX_BAR_SIZE, null);
  }

  public SingleComponentInfiniteProgress(
      InfiniteProgressAdapter infiniteProgressAdapter)
  {
    this(true, DEFAULT_NUMBER_OF_BARS,
        DEFAULT_FPS, NO_AUTOMATIC_RESIZING, NO_MAX_BAR_SIZE,
        infiniteProgressAdapter);
  }

  public SingleComponentInfiniteProgress(boolean i_bUseBackBuffer, int numBars)
  {
    this(i_bUseBackBuffer, numBars,
        DEFAULT_FPS, NO_AUTOMATIC_RESIZING, NO_MAX_BAR_SIZE, null);
  }

  public SingleComponentInfiniteProgress(
      boolean i_bUseBackBuffer, InfiniteProgressAdapter infiniteProgressAdapter)
  {
    this(i_bUseBackBuffer, DEFAULT_NUMBER_OF_BARS,
        DEFAULT_FPS, NO_AUTOMATIC_RESIZING, NO_MAX_BAR_SIZE,
        infiniteProgressAdapter);
  }

  public SingleComponentInfiniteProgress(
      int numBars, InfiniteProgressAdapter infiniteProgressAdapter)
  {
    this(true, numBars,
        DEFAULT_FPS, NO_AUTOMATIC_RESIZING, NO_MAX_BAR_SIZE,
        infiniteProgressAdapter);
  }

  public SingleComponentInfiniteProgress(boolean i_bUseBackBuffer,
                                         int numBars,
                                         int fps,
                                         double resizeRatio,
                                         double maxBarSize,
                                         InfiniteProgressAdapter infiniteProgressAdapter)
  {
    this.useBackBuffer = i_bUseBackBuffer;
    this.numBars = numBars;
    this.resizeRatio = resizeRatio;
    this.maxBarSize = maxBarSize;

    //this.timer = new Timer(1000 / fps, this);
    this.timerInterval = 1000 / fps;

    setInfiniteProgressAdapter(infiniteProgressAdapter);

    colors = new Color[numBars * 2];
    // build bars
    bars = buildTicker(numBars);
    // calculate bars bounding rectangle
    barsBounds = new Rectangle();
    for(int i = 0; i < bars.length; i++) {
      barsBounds = barsBounds.union(bars[i].getBounds());
    }
// CHANGE
// NEW
   // adapt to L&F color scheme.
   JLabel template = new JLabel();
   setBackground(template.getBackground());
   setForeground(template.getForeground());
// OLD
    // create colors
    /*for(int i = 0; i < bars.length; i++) {
      int channel = 224 - 128 / (i + 1);
      colors[i] = new Color(channel, channel, channel);
      colors[numBars + i] = colors[i];
    }*/
// END CHANGE */
    // set cursor
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    // set opaque
    setOpaque(useBackBuffer);
  }

  //
  // METHODS FROM JComponent
  //

  /**
   * Show/Hide the pane, starting and stopping the animation as you go
   */
  public void setVisible(boolean i_bIsVisible)
  {
    final boolean old = isVisible();
    setOpaque(false);
    // capture
    if(i_bIsVisible) {
      timerTask = new RedrawTask();
      if(useBackBuffer) {
        // add window resize listener
        Window w = SwingUtilities.getWindowAncestor(this);
        if(w != null) {
          w.addComponentListener(componentAdapter);
        } else {
          addAncestorListener(new AncestorListener()
          {
            public void ancestorAdded(AncestorEvent event)
            {
              Window w = SwingUtilities.getWindowAncestor(SingleComponentInfiniteProgress.this);
              if(w != null) {
                w.addComponentListener(componentAdapter);
              }
            }

            public void ancestorRemoved(AncestorEvent event)
            {
            }

            public void ancestorMoved(AncestorEvent event)
            {
            }
          });
        }
        timerTask.iterate = 3;
      }
      // capture events
      addMouseListener(mouseAdapter);
      addMouseMotionListener(mouseMotionAdapter);
      addKeyListener(keyAdapter);
      // start anim
      if(infiniteProgressAdapter != null) {
        infiniteProgressAdapter.animationStarting();
        infiniteProgressAdapter.rampUpEnded();
      }
      //timer.start();
      timer = new Timer();
      timer.schedule(timerTask, 0, timerInterval);
    } else {
      // stop anim
      //timer.stop();
      if (timer != null){
        timer.cancel();
        timer = null;
        timerTask = null;
      }
      if(infiniteProgressAdapter != null) {
        infiniteProgressAdapter.animationStopping();
      }
      /// free back buffer
      imageBuf = null;
      // stop capturing events
      removeMouseListener(mouseAdapter);
      removeMouseMotionListener(mouseMotionAdapter);
      removeKeyListener(keyAdapter);
      // remove window resize listener
      Window oWindow = SwingUtilities.getWindowAncestor(this);
      if(oWindow != null) {
        oWindow.removeComponentListener(componentAdapter);
      }
    }
    super.setVisible(i_bIsVisible);
    firePropertyChange("running", old, i_bIsVisible);
  }

  /**
   * Recalc bars based on changes in size
   */
  public void setBounds(int x, int y, int width, int height)
  {
    super.setBounds(x, y, width, height);
    calcBarsForBounds(getWidth(), getHeight(), true);

    // Now, see if the text fits...

    final double maxY = getTextMaxY(getText(), getFont(), (Graphics2D)getGraphics(), barsScreenBounds.getMaxY());
    final int bottom = getY() + getHeight();
    if(maxY >= bottom) {
      final int neededSpace = (int)Math.ceil(maxY) - bottom;
      calcBarsForBounds(getWidth(), Math.max(1, getHeight() - neededSpace), false);
    }
  }


  /**
   * paint background dimed and bars over top
   */
  protected void paintComponent(Graphics g)
  {
    if(!tempHide) {
      paintBars(g, true);
    }
  }













  //
  // METHODS FROM INTERFACE CancelableAdaptee
  //

  public void start()
  {
    setVisible(true);
  }

  public void stop()
  {
    setVisible(false);
  }

  public void setText(String text)
  {
    final String old = this.text;
    this.text = text;
    repaint();
    firePropertyChange("text", old, text);
  }

  public JComponent getComponent()
  {
    return this;
  }

  /**
   * Adds a listener to the cancel button in this progress panel.
   *
   * @param listener
   *
   * @throws RuntimeException if the infiniteProgressAdapter is null or is
   *                          not a CancelableProgessAdapter
   */
  public void addCancelListener(ActionListener listener)
  {
    if(infiniteProgressAdapter instanceof CancelableProgessAdapter) {
      ((CancelableProgessAdapter) infiniteProgressAdapter).addCancelListener(listener);
    } else {
      throw new RuntimeException(
          "Expected CancelableProgessAdapter for cancel listener.  Adapter is " +
          infiniteProgressAdapter);
    }
  }

  /**
   * Removes a listener to the cancel button in this progress panel.
   *
   * @param listener
   *
   * @throws RuntimeException if the infiniteProgressAdapter is null or is
   *                          not a CancelableProgessAdapter
   */
  public void removeCancelListener(ActionListener listener)
  {
    if(infiniteProgressAdapter instanceof CancelableProgessAdapter) {
      ((CancelableProgessAdapter) infiniteProgressAdapter)
          .removeCancelListener(listener);
    } else {
      throw new RuntimeException(
          "Expected CancelableProgessAdapter for cancel listener.  Adapter is " +
          infiniteProgressAdapter);
    }
  }

  //
  // METHODS FROM INTERFACE ActionListener
  //

  /*int iterate;  //we use transparency to draw a number of iterations before making a snapshot

  /**
   * Called to animate the rotation of the bar's colors
   */
  /*public void actionPerformed(ActionEvent e)
  {
    // rotate colors
    if(colorOffset == (numBars - 1)) {
      colorOffset = 0;
    } else {
      colorOffset++;
    }
    // repaint
    if(barsScreenBounds != null) {
      repaint(barsScreenBounds);
    } else {
      repaint();
    }
    if(useBackBuffer && imageBuf == null) {
      if(iterate < 0) {
        try {
          makeSnapshot();
          setOpaque(true);
        } catch(AWTException e1) {
          throw new RuntimeException(e1);
        }
      } else {
        iterate--;
      }
    }
  }*/

  private class RedrawTask
    extends TimerTask
  {
    int iterate;
    public void run ()
    {
      SwingUtilities.invokeLater(new Runnable(){
        public void run (){
          // rotate colors
          if(colorOffset == (numBars - 1)) {
            colorOffset = 0;
          } else {
            colorOffset++;
          }
          // repaint
          if(barsScreenBounds != null) {
            repaint(barsScreenBounds);
          } else {
            repaint();
          }
          if(useBackBuffer && imageBuf == null) {
            if(iterate < 0) {
              try {
                makeSnapshot();
                setOpaque(true);
              } catch(AWTException e1) {
                throw new RuntimeException(e1);
              }
            } else {
              iterate--;
            }
          }
        }
      });
    }
  }

  //
  // PROPERTY ACCESSORS
  //

  public String getText()
  {
    return text;
  }

  public double getResizeRatio()
  {
    return resizeRatio;
  }

  public void setResizeRatio(final double resizeRatio)
  {
    final double old = this.resizeRatio;
    this.resizeRatio = resizeRatio;
    setBounds(getBounds());
    repaint();
    firePropertyChange("resizeRatio", old, resizeRatio);
  }

  public double getMaxBarSize()
  {
    return maxBarSize;
  }

  public void setMaxBarSize(final double maxBarSize)
  {
    final double old = this.maxBarSize;
    this.maxBarSize = maxBarSize;
    setBounds(getBounds());
    repaint();
    firePropertyChange("maxBarSize", old, maxBarSize);
  }

  public int getNumBars()
  {
    return numBars;
  }

  public boolean getUseBackBuffer()
  {
    return useBackBuffer;
  }

  public boolean isRunning()
  {
    return isVisible();
  }






  //
  // HELPER/SUPPORT METHODS
  //

  private void makeSnapshot() throws AWTException
  {
    final Rectangle bounds = getBounds();
    final Point upperLeft = new Point(bounds.x, bounds.y);
    SwingUtilities.convertPointToScreen(upperLeft, this);
    final Rectangle screenRect = new Rectangle(upperLeft.x, upperLeft.y, bounds.width, bounds.height);
    Insets insets = getInsets();
    screenRect.x += insets.left;
    screenRect.y += insets.top;
    screenRect.width -= insets.left + insets.right;
    screenRect.height -= insets.top + insets.bottom;
    // capture window contents
    imageBuf = new Robot().createScreenCapture(screenRect);
    //no need to fade because we are allready using an image that is showing through
  }

  protected void calcBarsForBounds(final int width, final int height,
                                   final boolean honorMinBarSize)
  {
    // update centering transform
    centerAndScaleTransform = new AffineTransform();
    centerAndScaleTransform.translate((double)width / 2d,
                                      (double)height / 2d);

    double scale = dScale;

    if(resizeRatio != NO_AUTOMATIC_RESIZING) {
      final int minSpace = Math.min(width, height);
      scale = (minSpace * resizeRatio) / UNSCALED_BAR_SIZE;
      if(maxBarSize != NO_MAX_BAR_SIZE && (UNSCALED_BAR_SIZE * scale) >= maxBarSize) {
        scale = maxBarSize / UNSCALED_BAR_SIZE;
      }
      if(honorMinBarSize && (UNSCALED_BAR_SIZE * scale < minBarSize)) {
        scale = minBarSize / UNSCALED_BAR_SIZE;
      }
    }

    centerAndScaleTransform.scale(scale, scale);

    calcNewBarsBounds();
  }

  private void calcNewBarsBounds()
  {
    if(barsBounds != null) {
      Area oBounds = new Area(barsBounds);
      oBounds.transform(centerAndScaleTransform);
      barsScreenBounds = oBounds.getBounds();
    }
  }

  protected void setInfiniteProgressAdapter(InfiniteProgressAdapter infiniteProgressAdapter)
  {
    this.infiniteProgressAdapter = infiniteProgressAdapter;
  }



  protected double paintBars(final Graphics g, final boolean paintBackground)
  {
    Rectangle oClip = g.getClipBounds();
    if(paintBackground) {
      if(imageBuf != null) {
        // draw background image
        g.drawImage(imageBuf, oClip.x, oClip.y, oClip.x + oClip.width, oClip.y + oClip.height,
                    oClip.x, oClip.y, oClip.x + oClip.width, oClip.y + oClip.height,
                    null);
        g.drawImage(imageBuf, 0, 0, null);
      } else {
// CHANGE
// OLD
        //g.setColor(new Color(255, 255, 255, 180));
// NEW
        Color color = getBackground();
        g.setColor(new Color(
              color.getRed(), color.getGreen(), color.getBlue(), 200));
// END CHANGE
        g.fillRect(oClip.x, oClip.y, oClip.width, oClip.height);
      }
    }
    // move to center
    Graphics2D g2 = (Graphics2D)g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.transform(centerAndScaleTransform);
// CHANGE
    // create colors
    if(colors[0] == null){
      for(int i = 0; i < bars.length; i++) {
        //int channel = 224 - 128 / (i + 1);
        int re = Math.abs(getBackground().getRed() - 128 / (i + 1));
        int gr = Math.abs(getBackground().getGreen() - 128 / (i + 1));
        int bl = Math.abs(getBackground().getBlue() - 128 / (i + 1));
        colors[i] = new Color(re, gr, bl);
        colors[numBars + i] = colors[i];
      }
    }
// END CHANGE
    // draw ticker
    for(int i = 0; i < bars.length; i++) {
      g2.setColor(colors[i + colorOffset]);
      g2.fill(bars[i]);
    }

    // NOTE: we pass in g and not g2 so that the transformation is not
    // applied.
    double maxY = drawTextAt(text, getFont(), (Graphics2D)g,
                             getWidth(), barsScreenBounds.getMaxY(),
                             getForeground());

    if(infiniteProgressAdapter != null) {
      infiniteProgressAdapter.paintSubComponents(maxY);
    }

    // NOTE: this will not contain the size of the sub components, since the
    // paintSubComponents(...) method does not provide this information, and
    // I don't feel like patching the adapter since I don't use it right
    // now. :)

    return maxY;
  }

  /**
   * Draw text in a Graphics2D.
   *
   * @param text       the text to draw
   * @param font       the font to use
   * @param g2         the graphics context to draw in
   * @param width      the width of the parent, so it can be centered
   * @param y          the height at which to draw
   * @param foreGround the foreground color to draw in
   *
   * @return the y value that is the y param + the text height.
   */
  public static double drawTextAt(String text, Font font, Graphics2D g2,
                                  int width, double y, Color foreGround)
  {
    final TextLayout layout = getTextLayout(text, font, g2);
    if(layout != null) {
      Rectangle2D bounds = layout.getBounds();
      g2.setColor(foreGround);
      float textX = (float) (width - bounds.getWidth()) / 2;
      y = y + layout.getLeading() + 2 * layout.getAscent();
      layout.draw(g2, textX, (float) y);
      return y + bounds.getHeight();
    } else {
      return 0d;
    }
  }

  public static double getTextMaxY(final String text, final Font font,
                                   final Graphics2D g2, final double y)
  {
    final TextLayout layout = getTextLayout(text, font, g2);
    if(layout != null) {
      return y + layout.getLeading() + (2 * layout.getAscent()) + layout.getBounds().getHeight();
    } else {
      return 0d;
    }
  }

  private static TextLayout getTextLayout(final String text, final Font font,
                                          final Graphics2D g2)
  {
    if(text != null && text.length() > 0) {
      FontRenderContext context = g2.getFontRenderContext();
      return new TextLayout(text, font, context);
    } else {
      return null;
    }
  }




  /**
   * Builds the circular shape and returns the result as an array of
   * <code>Area</code>. Each <code>Area</code> is one of the bars composing
   * the shape.
   */
  private static Area[] buildTicker(int i_iBarCount)
  {
    Area[] ticker = new Area[i_iBarCount];
    Point2D.Double center = new Point2D.Double(0, 0);
    double fixedAngle = 2.0 * Math.PI / ((double) i_iBarCount);

    for(double i = 0.0; i < (double) i_iBarCount; i++) {
      Area primitive = buildPrimitive();

      AffineTransform toCenter = AffineTransform.getTranslateInstance(
          center.getX(), center.getY());
      AffineTransform toBorder = AffineTransform.getTranslateInstance(UNSCALED_BAR_SIZE, -6.0);
      AffineTransform toCircle = AffineTransform.getRotateInstance(
          -i * fixedAngle, center.getX(), center.getY());

      AffineTransform toWheel = new AffineTransform();
      toWheel.concatenate(toCenter);
      toWheel.concatenate(toBorder);

      primitive.transform(toWheel);
      primitive.transform(toCircle);

      ticker[(int) i] = primitive;
    }

    return ticker;
  }

  /**
   * Builds a bar.
   */
  private static Area buildPrimitive()
  {
    Rectangle2D.Double body = new Rectangle2D.Double(6, 0, 30, 12);
    Ellipse2D.Double head = new Ellipse2D.Double(0, 0, 12, 12);
    Ellipse2D.Double tail = new Ellipse2D.Double(30, 0, 12, 12);

    Area tick = new Area(body);
    tick.add(new Area(head));
    tick.add(new Area(tail));

    return tick;
  }
}

