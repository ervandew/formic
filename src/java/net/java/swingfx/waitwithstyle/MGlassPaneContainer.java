package net.java.swingfx.waitwithstyle;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Dimension;
import java.awt.Container;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author Andy DePue
 * @since Apr 14, 2006 2:30:30 PM
 */
public class MGlassPaneContainer extends JPanel
{
  private JComponent glassPane;

  public MGlassPaneContainer()
  {
    setLayout(new Layout());
    setGlassPane(createGlassPane());
  }

  public MGlassPaneContainer(final Component singleComponent)
  {
    this();
    add(singleComponent);
  }



  //
  // PUBLIC API
  //

  public void setGlassPane(final JComponent glass)
  {
    if(glass == null) {
      setGlassPane(createGlassPane());
      return;
    }

    final JComponent old = this.glassPane;

    final boolean visible;
    if(this.glassPane != null && this.glassPane.getParent() == this) {
      this.remove(this.glassPane);
      visible = this.glassPane.isVisible();
    } else {
      visible = false;
    }

    glass.setVisible(visible);
    this.glassPane = glass;
    this.add(this.glassPane);

    firePropertyChange("glassPane", old, glass);

    if(visible) {
      repaint();
    }
  }

  public JComponent getGlassPane()
  {
    return glassPane;
  }


  public static MGlassPaneContainer findGlassPaneContainerFor(Component c)
  {
    while(c != null && !(c instanceof MGlassPaneContainer)) {
      c = c.getParent();
    }

    return (MGlassPaneContainer)c;
  }







  //
  // METHODS FROM JLayeredPane
  //

  protected void addImpl(Component comp, Object constraints, int index)
  {
    if(comp == getGlassPane()) {
      super.addImpl(comp, constraints, 0);
    } else {
      if(index == 0) {
        index = 1;
      }
      super.addImpl(comp, constraints, index);
    }
  }



  public boolean isOptimizedDrawingEnabled()
  {
    return !getGlassPane().isVisible() && super.isOptimizedDrawingEnabled();
  }





  //
  // HELPER/SUPPORT METHODS
  //

  protected JPanel createGlassPane()
  {
    final JPanel ret = new JPanel();
    ret.setName(getName() + ".glassPane");
    ret.setVisible(false);
    ret.setOpaque(false);
    return ret;
  }










  //
  // INNER CLASSES
  //

  protected class Layout implements LayoutManager2, Serializable
  {
    public Dimension maximumLayoutSize(final Container target)
    {
      assert target == MGlassPaneContainer.this;
      final int componentCount = getComponentCount();
      final JComponent glassPane = getGlassPane();
      final Dimension max = new Dimension();
      for(int i = 0;i < componentCount;i++) {
        final Component c = getComponent(i);
        if(c != glassPane) {
          final Dimension cMax = c.getMaximumSize();
          max.setSize(Math.max(max.width, cMax.width),
                      Math.max(max.height, cMax.height));
        }
      }
      return max;
    }

    public Dimension preferredLayoutSize(final Container target)
    {
      assert target == MGlassPaneContainer.this;
      final int componentCount = getComponentCount();
      final JComponent glassPane = getGlassPane();
      final Dimension pref = new Dimension();
      for(int i = 0;i < componentCount;i++) {
        final Component c = getComponent(i);
        if(c != glassPane) {
          final Dimension cPref = c.getPreferredSize();
          pref.setSize(Math.max(pref.width, cPref.width),
                       Math.max(pref.height, cPref.height));
        }
      }
      return pref;
    }

    public Dimension minimumLayoutSize(final Container target)
    {
      assert target == MGlassPaneContainer.this;
      final int componentCount = getComponentCount();
      final JComponent glassPane = getGlassPane();
      final Dimension min = new Dimension();
      for(int i = 0;i < componentCount;i++) {
        final Component c = getComponent(i);
        if(c != glassPane) {
          final Dimension cMin = c.getMinimumSize();
          min.setSize(Math.max(min.width, cMin.width),
                      Math.max(min.height, cMin.height));
        }
      }
      return min;
    }

    public void layoutContainer(final Container target)
    {
      assert target == MGlassPaneContainer.this;

      final Rectangle bounds = getBounds();
      final Insets insets = getInsets();
      final int width = bounds.width - insets.right - insets.left;
      final int height = bounds.height - insets.bottom - insets.top;

      final int componentCount = getComponentCount();

      for(int i = 0;i < componentCount;i++) {
        final Component c = getComponent(i);
        c.setBounds(0, 0, width, height);
      }
    }

    public float getLayoutAlignmentX(Container target) { return 0f; }
    public float getLayoutAlignmentY(Container target) { return 0f; }

    public void addLayoutComponent(Component comp, Object constraints) { }
    public void invalidateLayout(Container target) { }
    public void addLayoutComponent(String name, Component comp) { }
    public void removeLayoutComponent(Component comp) { }
  }
}
