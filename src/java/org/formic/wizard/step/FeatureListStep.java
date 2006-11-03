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

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import foxtrot.Task;
import foxtrot.Worker;

import org.apache.commons.io.IOUtils;

import org.apache.commons.lang.StringUtils;

import org.formic.Installer;

import org.formic.dialog.gui.GuiDialogs;

import org.formic.event.gui.HyperlinkListener;

import org.formic.form.console.ConsoleForm;

import org.formic.form.gui.GuiComponentFactory;
import org.formic.form.gui.GuiForm;

import org.formic.swing.ComponentTableCellRenderer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

  protected static final String FEATURES_XML = "features.xml";

  private String featuresXml;
  private List features;
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

    featuresXml = getProperty(FEATURES_XML);
    if(featuresXml == null){
      throw new IllegalArgumentException(
          Installer.getString(PROPERTY_REQUIRED, FEATURES_XML, getName()));
    }

    if (FeatureListStep.class.getResourceAsStream(featuresXml) == null){
      throw new IllegalArgumentException(
          Installer.getString(RESOURCE_NOT_FOUND, FEATURES_XML, getName()));
    }
  }

  /**
   * {@inheritDoc}
   * @see AbstractFormStep#initGuiForm()
   */
  protected GuiForm initGuiForm ()
  {
    initFeatures();

    GuiComponentFactory factory = new GuiComponentFactory(getName());
    GuiForm form = new GuiForm();
    form.setModel(factory.getFormModel());

    featureInfo = new JEditorPane("text/html", StringUtils.EMPTY);
    featureInfo.setEditable(false);
    featureInfo.addHyperlinkListener(new HyperlinkListener());

    JTable table = new JTable(features.size(), 2){
      public Class getColumnClass (int column){
        return getValueAt(0, column).getClass();
      }
      public boolean isCellEditable (int row, int column){
        return false;
      }
    };
    table.setBackground(new javax.swing.JList().getBackground());
    for (int ii = 0; ii < features.size(); ii++){
      Feature feature = (Feature)features.get(ii);
      JCheckBox box = factory.createCheckBox(feature.getProperty());
      box.setSelected(feature.isEnabled());

      feature.setTitle(box.getText());
      feature.setInfo(Installer.getString(
            getName() + "." + feature.getProperty() + ".html"));

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
   * Initializes the feature list.
   */
  public void initFeatures ()
  {
    setBusy(true);
    try{
      features = (List)Worker.post(new Task(){
        public Object run ()
          throws Exception
        {
          List list = new ArrayList();
          DocumentBuilder builder =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();

          InputStream xml = FeatureListStep.class.getResourceAsStream(featuresXml);
          try{
            Document document = builder.parse(xml);
            NodeList children = document.getElementsByTagName("feature");
            for (int ii = 0; ii < children.getLength(); ii++){
              Element element = (Element)children.item(ii);
              list.add(new Feature(
                  element.getAttribute("property"),
                  Boolean.valueOf(element.getAttribute("enabled")).booleanValue()
              ));
            }
          }finally{
            IOUtils.closeQuietly(xml);
          }
          return list;
        }
      });
    }catch(Exception e){
      GuiDialogs.showError(e);
    }finally{
      setBusy(false);
    }
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
  private class Feature
  {
    private String property;
    private String title;
    private String info;
    private boolean enabled;

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
     * Gets the title for this instance.
     *
     * @return The title.
     */
    public String getTitle ()
    {
      return this.title;
    }

    /**
     * Sets the title for this instance.
     *
     * @param title The title.
     */
    public void setTitle (String title)
    {
      this.title = title;
    }

    /**
     * Gets the info for this instance.
     *
     * @return The info.
     */
    public String getInfo ()
    {
      return this.info;
    }

    /**
     * Sets the info for this instance.
     *
     * @param info The info.
     */
    public void setInfo (String info)
    {
      this.info = info;
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
     * {@inheritDoc}
     * @see Object#toString()
     */
    public String toString ()
    {
      return title;
    }
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
          JCheckBox box = (JCheckBox)table.getModel().getValueAt(row, col);
          box.doClick();
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
          box.doClick();
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
