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

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JCheckBox;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.jgoodies.forms.layout.FormLayout;

import foxtrot.Task;
import foxtrot.Worker;

import org.apache.commons.io.IOUtils;

import org.formic.Installer;

import org.formic.dialog.gui.GuiDialogs;

import org.formic.form.console.ConsoleForm;

import org.formic.form.gui.GuiComponentFactory;
import org.formic.form.gui.GuiForm;
import org.formic.form.gui.GuiFormBuilder;

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

    FormLayout layout = new FormLayout("pref, 4dlu, 150dlu");
    GuiFormBuilder builder = new GuiFormBuilder(getName(), layout);
    GuiComponentFactory factory = builder.getFactory();

    for (Iterator ii = features.iterator(); ii.hasNext();){
      Feature feature = (Feature)ii.next();
      JCheckBox box = factory.createCheckBox(feature.getProperty());
      box.setSelected(feature.isEnabled());
      builder.append(box);
      builder.nextRow();
    }

    return builder.getForm();
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
     * Determines if this instance is enabled.
     *
     * @return The enabled.
     */
    public boolean isEnabled ()
    {
      return this.enabled;
    }
  }
}
