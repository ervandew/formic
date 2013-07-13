/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2013 Eric Van Dewoestine
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
package org.formic.wizard.step.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.HashMap;
import java.util.Map;
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

import org.formic.util.event.gui.HyperlinkListener;

import org.formic.util.swing.ComponentTableCellRenderer;

import org.formic.wizard.form.GuiForm;

import org.formic.wizard.step.AbstractGuiStep;

import org.formic.wizard.step.shared.Feature;
import org.formic.wizard.step.shared.FeatureProvider;

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
 *     <td>provider</td>
 *     <td>Implementation of {@link FeatureProvider}.</td>
 *     <td>true</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 * </table>
 *
 * @author Eric Van Dewoestine
 */
public class FeatureListStep
  extends AbstractGuiStep
{
  private static final String PROVIDER = "provider";

  private FeatureProvider provider;
  private JEditorPane featureInfo;
  private Map featureMap = new HashMap();

  /**
   * Constructs the step.
   */
  public FeatureListStep(String name, Properties properties)
  {
    super(name, properties);

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
   * @see org.formic.wizard.step.GuiStep#init()
   */
  public Component init()
  {
    featureInfo = new JEditorPane("text/html", StringUtils.EMPTY);
    featureInfo.setEditable(false);
    featureInfo.addHyperlinkListener(new HyperlinkListener());

    Feature[] features = provider.getFeatures();
    JTable table = new JTable(features.length, 1){
      private static final long serialVersionUID = 1L;
      public Class getColumnClass(int column){
        return getValueAt(0, column).getClass();
      }
      public boolean isCellEditable(int row, int column){
        return false;
      }
    };
    table.setBackground(new javax.swing.JList().getBackground());

    GuiForm form = createForm();

    for (int ii = 0; ii < features.length; ii++){
      final Feature feature = (Feature)features[ii];
      final JCheckBox box = new JCheckBox();

      String name = getName() + '.' + feature.getKey();
      form.bind(name, box);

      box.putClientProperty("feature", feature);
      featureMap.put(feature.getKey(), box);

      feature.setInfo(Installer.getString(
            getName() + "." + feature.getKey() + ".html"));
      feature.addPropertyChangeListener(new PropertyChangeListener(){
        public void propertyChange(PropertyChangeEvent event){
          if(Feature.ENABLED_PROPERTY.equals(event.getPropertyName())){
            if(box.isSelected() != feature.isEnabled()){
              box.setSelected(feature.isEnabled());
            }
          }
        }
      });

      box.setText(Installer.getString(name));
      box.setBackground(table.getBackground());
      table.setValueAt(box, ii, 0);
    }

    FeatureListMouseListener mouseListener = new FeatureListMouseListener();
    for (int ii = 0; ii < features.length; ii++){
      Feature feature = (Feature)features[ii];
      if(feature.isEnabled()){
        JCheckBox box = (JCheckBox)featureMap.get(feature.getKey());
        box.setSelected(true);
        mouseListener.processDependencies(feature);
        mouseListener.processExclusives(feature);
      }
    }

    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setShowHorizontalLines(false);
    table.setShowVerticalLines(false);
    table.setDefaultRenderer(JCheckBox.class, new ComponentTableCellRenderer());

    table.addKeyListener(new FeatureListKeyListener());
    table.addMouseListener(mouseListener);
    table.getSelectionModel().addListSelectionListener(
        new FeatureListSelectionListener(table));

    table.setRowSelectionInterval(0, 0);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    JPanel container = new JPanel(new BorderLayout());
    container.add(table, BorderLayout.CENTER);
    panel.add(new JScrollPane(container), BorderLayout.CENTER);
    JScrollPane infoScroll = new JScrollPane(featureInfo);
    infoScroll.setMinimumSize(new Dimension(0, 50));
    infoScroll.setMaximumSize(new Dimension(0, 50));
    infoScroll.setPreferredSize(new Dimension(0, 50));
    panel.add(infoScroll, BorderLayout.SOUTH);

    return panel;
  }

  private class FeatureListListener
  {
    protected void toggleSelection(JTable table, int row)
    {
      JCheckBox box = (JCheckBox)table.getModel().getValueAt(row, 0);
      Feature feature = (Feature)box.getClientProperty("feature");
      box.doClick();
      feature.setEnabled(box.isSelected());

      processDependencies(feature);
      processExclusives(feature);

      table.revalidate();
      table.repaint();
    }

    protected void processDependencies(Feature feature)
    {
      String[] dependencies = feature.getDependencies();
      if (dependencies == null){
        return;
      }

      for(int ii = 0; ii < dependencies.length; ii++){
        String key = dependencies[ii];
        JCheckBox box = (JCheckBox)featureMap.get(key);
        // feature may have been excluded from the list by the provider
        if (box == null){
          continue;
        }
        if(feature.isEnabled()){
          box.setEnabled(false);
          box.setSelected(true);
          Feature dfeature = (Feature)box.getClientProperty("feature");
          if(!dfeature.isEnabled()){
            dfeature.setEnabled(true);
            processDependencies(dfeature);
            processExclusives(dfeature);
          }
        }else{
          // check if any other enabled feature has this as a dependency
          boolean required = false;
          Feature[] features = provider.getFeatures();
          for(int jj = 0; jj < features.length; jj++){
            JCheckBox fbox = (JCheckBox)featureMap.get(features[jj].getKey());
            Feature f = (Feature)fbox.getClientProperty("feature");
            if (f.isEnabled() && f.hasDependency(key)){
              required = true;
              break;
            }
          }
          box.setEnabled(!required);
        }
      }
    }

    protected void processExclusives(Feature feature)
    {
      String[] exclusives = feature.getExclusives();
      if (exclusives == null){
        return;
      }

      for(int ii = 0; ii < exclusives.length; ii++){
        String key = exclusives[ii];
        JCheckBox box = (JCheckBox)featureMap.get(key);
        // feature may have been excluded from the list by the provider
        if (box == null){
          continue;
        }
        if(feature.isEnabled()){
          box.setEnabled(false);
          box.setSelected(false);
          Feature efeature = (Feature)box.getClientProperty("feature");
          if(efeature.isEnabled()){
            efeature.setEnabled(false);
            processDependencies(efeature);
            processExclusives(efeature);
          }
        }else{
          // check if any other enabled feature has this in its exclusive list
          boolean exclusive = false;
          Feature[] features = provider.getFeatures();
          for(int jj = 0; jj < features.length; jj++){
            JCheckBox fbox = (JCheckBox)featureMap.get(features[jj].getKey());
            Feature f = (Feature)fbox.getClientProperty("feature");
            if (f.isEnabled() && f.hasExclusive(key)){
              exclusive = true;
              break;
            }
          }
          box.setEnabled(!exclusive);
        }
      }
    }
  }

  /**
   * Mouse listener for the feature list.
   */
  private class FeatureListMouseListener
    extends FeatureListListener
    implements MouseListener
  {
    /**
     * {@inheritDoc}
     * @see MouseListener#mouseClicked(MouseEvent)
     */
    public void mouseClicked(MouseEvent e)
    {
      if(e.getButton() == MouseEvent.BUTTON1){
        JTable table = (JTable)e.getSource();
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        JCheckBox box = (JCheckBox)table.getModel().getValueAt(row, col);
        int height = table.getRowHeight(row);
        // in our case we want the clicking of the checkbox label to render
        // the feature info, but not to change the status of the checkbox.
        // Before we had a separate JLabel, but then that requires extra work
        // to change the text color when the checkbox is disabled/enabled.
        if(col == 0 && row > -1 && e.getX() < height){
          toggleSelection(table, row);
        }
      }
    }

    /**
     * {@inheritDoc}
     * @see MouseListener#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
    }

    /**
     * {@inheritDoc}
     * @see MouseListener#mouseReleased(MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
    }

    /**
     * {@inheritDoc}
     * @see MouseListener#mouseEntered(MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
    }

    /**
     * {@inheritDoc}
     * @see MouseListener#mouseExited(MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
    }
  }

  /**
   * Key listener for the feature list.
   */
  private class FeatureListKeyListener
    extends FeatureListListener
    implements KeyListener
  {
    /**
     * {@inheritDoc}
     * @see KeyListener#keyTyped(KeyEvent)
     */
    public void keyTyped(KeyEvent e)
    {
      if(e.getKeyChar() == ' '){
        JTable table = (JTable)e.getSource();
        int row = table.getSelectedRow();
        if(row != -1){
          toggleSelection(table, row);
        }
      }
    }

    /**
     * {@inheritDoc}
     * @see KeyListener#keyPressed(KeyEvent)
     */
    public void keyPressed(KeyEvent e)
    {
    }

    /**
     * {@inheritDoc}
     * @see KeyListener#keyReleased(KeyEvent)
     */
    public void keyReleased(KeyEvent e)
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
    public FeatureListSelectionListener(JTable table)
    {
      this.table = table;
    }

    /**
     * {@inheritDoc}
     * @see ListSelectionListener#valueChanged(ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
      if(!e.getValueIsAdjusting()){
        JCheckBox box = (JCheckBox)
          table.getModel().getValueAt(table.getSelectedRow(), 0);
        Feature feature = (Feature)box.getClientProperty("feature");
        featureInfo.setText(feature.getInfo());
      }
    }
  }
}
