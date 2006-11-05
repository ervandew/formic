/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2006  Eric Van Dewoestine
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

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;

import org.formic.Installer;

import org.formic.event.gui.HyperlinkListener;

import org.formic.form.console.ConsoleForm;

import org.formic.form.gui.GuiComponentFactory;
import org.formic.form.gui.GuiForm;

import org.formic.swing.ComponentTableCellRenderer;

/**
 * Step which allows the user to select features to be installed.
 * <p/>
 * <b>Properties</b>
 * <table class="properties">
 *   <tr>
 *     <th>Name</th><th>Description</th>
 *     <th>Required</th><th>Possible Values</th><th>Default</th>
 *   </tr>
 *   <tr>
 *     <td>features.xml</td>
 *     <td>Classpath relative location of the features.xml file.</td>
 *     <td>true</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 * </table>
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class FeatureListStep
  extends AbstractFormStep
{
  private static final String ICON = "/images/32x32/component_list.png";

  protected static final String PROVIDER = "provider";

  private FeatureProvider provider;
  private JEditorPane featureInfo;

  /**
   * Constructs the step.
   */
  public FeatureListStep (String name)
  {
    super(name);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#initProperties(Properties)
   */
  public void initProperties (Properties properties)
  {
    super.initProperties(properties);

    String provider = getProperty(PROVIDER);
    if(provider == null){
      throw new IllegalArgumentException(
          Installer.getString(PROPERTY_REQUIRED, PROVIDER, getName()));
    }

    try{
      this.provider = (FeatureProvider)Class.forName(provider).newInstance();
    }catch(ClassCastException cce){
      throw new IllegalArgumentException(Installer.getString(
            PROPERTY_TYPE_INVALID, PROVIDER, FeatureProvider.class.getName()));
    }catch(ClassNotFoundException cnfe){
      throw new IllegalArgumentException(Installer.getString(
            PROPERTY_CLASS_NOT_FOUND, PROVIDER, provider));
    }catch(Exception e){
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   * @see AbstractFormStep#initGuiForm()
   */
  protected GuiForm initGuiForm ()
  {
    GuiComponentFactory factory = new GuiComponentFactory(getName());
    GuiForm form = new GuiForm();
    form.setModel(factory.getFormModel());
    provider.setGuiForm(form);

    featureInfo = new JEditorPane("text/html", StringUtils.EMPTY);
    featureInfo.setEditable(false);
    featureInfo.addHyperlinkListener(new HyperlinkListener());

    Feature[] features = provider.getFeatures();
    JTable table = new JTable(features.length, 2){
      public Class getColumnClass (int column){
        return getValueAt(0, column).getClass();
      }
      public boolean isCellEditable (int row, int column){
        return false;
      }
    };
    table.setBackground(new javax.swing.JList().getBackground());
    for (int ii = 0; ii < features.length; ii++){
      final Feature feature = (Feature)features[ii];
      final JCheckBox box = factory.createCheckBox(feature.getProperty());
      box.setSelected(feature.isEnabled());

      feature.setTitle(Installer.getString(
            getName() + '.' + feature.getProperty()));
      feature.setInfo(Installer.getString(
            getName() + "." + feature.getProperty() + ".html"));
      feature.addPropertyChangeListener(new PropertyChangeListener(){
        public void propertyChange (PropertyChangeEvent event){
          if(Feature.ENABLED_PROPERTY.equals(event.getPropertyName())){
            if(box.isSelected() != feature.isEnabled()){
              box.setSelected(feature.isEnabled());
            }
          }
        }
      });

      box.setText(null);
      box.setBackground(table.getBackground());

      table.setValueAt(box, ii, 0);
      table.setValueAt(feature, ii, 1);
    }

    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    table.getColumnModel().getColumn(0).setMaxWidth(20);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setShowHorizontalLines(false);
    table.setShowVerticalLines(false);
    table.setDefaultRenderer(JCheckBox.class, new ComponentTableCellRenderer());

    table.addKeyListener(new FeatureListKeyListener());
    table.addMouseListener(new FeatureListMouseListener());
    table.getSelectionModel().addListSelectionListener(
        new FeatureListSelectionListener(table));

    table.setRowSelectionInterval(0, 0);

    JPanel panel = form.getContentPanel();
    panel.setLayout(new BorderLayout());
    JPanel container = new JPanel(new BorderLayout());
    container.add(table, BorderLayout.CENTER);
    panel.add(new JScrollPane(container), BorderLayout.CENTER);
    JScrollPane infoScroll = new JScrollPane(featureInfo);
    infoScroll.setMinimumSize(new Dimension(0, 50));
    infoScroll.setMaximumSize(new Dimension(0, 50));
    infoScroll.setPreferredSize(new Dimension(0, 50));
    panel.add(infoScroll, BorderLayout.SOUTH);

    return form;
  }

  /**
   * {@inheritDoc}
   * @see AbstractFormStep#initConsoleForm()
   */
  protected ConsoleForm initConsoleForm ()
  {
    throw new UnsupportedOperationException();
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
   * Represents an available feature.
   */
  public static class Feature
  {
    public static final String ENABLED_PROPERTY = "enabled";

    private String property;
    private String title;
    private String info;
    private boolean enabled;
    private PropertyChangeSupport propertyChangeSupport;

    /**
     * Constructs a new instance.
     *
     * @param property The property for this instance.
     * @param enabled True if the feature is enabled by default, false
     * otherwise.
     */
    public Feature (String property, boolean enabled)
    {
      this.property = property;
      this.enabled = enabled;
      this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Gets the property for this instance.
     *
     * @return The property.
     */
    public String getProperty ()
    {
      return this.property;
    }

    /**
     * Determines if this instance is enabled.
     *
     * @return The enabled.
     */
    public boolean isEnabled ()
    {
      return this.enabled;
    }

    /**
     * Sets whether or not this instance is enabled.
     *
     * @param enabled True if enabled, false otherwise.
     */
    public void setEnabled (boolean enabled)
    {
      propertyChangeSupport.firePropertyChange(
          ENABLED_PROPERTY, this.enabled, this.enabled = enabled);
    }

    /**
     * Gets the info for this instance.
     *
     * @return The info.
     */
    private String getInfo ()
    {
      return this.info;
    }

    /**
     * Sets the info for this instance.
     *
     * @param info The info.
     */
    private void setInfo (String info)
    {
      this.info = info;
    }

    /**
     * Sets the title for this feature.
     *
     * @param title The title.
     */
    private void setTitle (String title)
    {
      this.title = title;
    }

    /**
     * {@inheritDoc}
     * @see Object#toString()
     */
    public String toString ()
    {
      return title;
    }

    /**
     * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
     */
    public void addPropertyChangeListener (PropertyChangeListener listener)
    {
      propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
     */
    public void removePropertyChangeListener (PropertyChangeListener listener)
    {
      propertyChangeSupport.removePropertyChangeListener(listener);
    }
  }

  /**
   * Defines a feature provider for determining available features.
   */
  public static interface FeatureProvider
  {
    /**
     * Gets the available features.
     *
     * @return Array of Feature.
     */
    public Feature[] getFeatures ();

    /**
     * Sets the GuiForm where the feature list is to be displayed.
     *
     * @param form The GuiForm.
     */
    public void setGuiForm (GuiForm form);

    /**
     * Sets the ConsoleForm where the feature list is to be displayed.
     *
     * @param form The ConsoleForm.
     */
    public void setConsoleForm (ConsoleForm form);
  }

  /**
   * Mouse listener for the feature list.
   */
  private class FeatureListMouseListener
    extends MouseAdapter
  {
    /**
     * {@inheritDoc}
     * @see MouseListener#mouseClicked(MouseEvent)
     */
    public void mouseClicked (MouseEvent e)
    {
      if(e.getButton() == MouseEvent.BUTTON1){
        JTable table = (JTable)e.getSource();
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        if(col == 0){
          JCheckBox box = (JCheckBox)table.getModel().getValueAt(row, 0);
          Feature feature = (Feature)table.getModel().getValueAt(row, 1);
          box.doClick();
          feature.setEnabled(box.isSelected());
          table.revalidate();
          table.repaint();
        }
      }
    }
  }

  /**
   * Key listener for the feature list.
   */
  private class FeatureListKeyListener
    implements KeyListener
  {
    /**
     * {@inheritDoc}
     * @see KeyListener#keyTyped(KeyEvent)
     */
    public void keyTyped (KeyEvent e)
    {
      if(e.getKeyChar() == ' '){
        JTable table = (JTable)e.getSource();
        int row = table.getSelectedRow();
        if(row != -1){
          JCheckBox box = (JCheckBox)table.getModel().getValueAt(row, 0);
          Feature feature = (Feature)table.getModel().getValueAt(row, 1);
          box.doClick();
          feature.setEnabled(box.isSelected());
          table.revalidate();
          table.revalidate();
          table.repaint();
        }
      }
    }

    /**
     * {@inheritDoc}
     * @see KeyListener#keyPressed(KeyEvent)
     */
    public void keyPressed (KeyEvent e)
    {
    }

    /**
     * {@inheritDoc}
     * @see KeyListener#keyReleased(KeyEvent)
     */
    public void keyReleased (KeyEvent e)
    {
    }
  }

  /**
   * List selection listener responsible for updating feature info text area.
   */
  private class FeatureListSelectionListener
    implements ListSelectionListener
  {
    private JTable table;

    /**
     * Constructs a new instance.
     */
    public FeatureListSelectionListener (JTable table)
    {
      this.table = table;
    }

    /**
     * {@inheritDoc}
     * @see ListSelectionListener#valueChanged(ListSelectionEvent)
     */
    public void valueChanged (ListSelectionEvent e)
    {
      if(!e.getValueIsAdjusting()){
        Feature feature = (Feature)
          table.getModel().getValueAt(table.getSelectedRow(), 1);
        featureInfo.setText(feature.getInfo());
      }
    }
  }
}
