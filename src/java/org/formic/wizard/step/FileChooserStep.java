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

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JFileChooser;

import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.FormLayout;

import org.apache.commons.io.FilenameUtils;

import org.apache.commons.lang.StringUtils;

import org.formic.Installer;

import org.formic.form.Validator;

import org.formic.form.console.ConsoleForm;

import org.formic.form.gui.GuiComponentFactory;
import org.formic.form.gui.GuiFileChooser;
import org.formic.form.gui.GuiForm;
import org.formic.form.gui.GuiFormBuilder;

import org.formic.form.validator.ValidatorBuilder;

/**
 * Wizard step that allows the user to choose a file or folder.
 * <p/>
 * <b>Properties</b>
 * <table class="properties">
 *   <tr>
 *     <th>Name</th><th>Description</th>
 *     <th>Required</th><th>Possible Values</th><th>Default</th>
 *   </tr>
 *   <tr>
 *     <td>property</td>
 *     <td>Defines the property which will hold the file or folder chosen.</td>
 *     <td>true</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 *   <tr>
 *     <td>choosable</td>
 *     <td>
 *       Optional comma separated list of choosable file extensions. Supports
 *       values consisting of ext:Description.  Ex. java:Java File,xml:Xml File
 *     </td>
 *     <td>false</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 *   <tr>
 *     <td>selectionMode</td>
 *     <td>
 *       Selection mode to use.  By default allows selection of file or
 *       directory.  Setting to "files" allows only file selection,
 *       "directories" allows only directory selection.
 *     </td>
 *     <td>false</td><td>&nbsp;</td><td>files or directories</td>
 *   </tr>
 *   <tr>
 *     <td>discoverer</td>
 *     <td>
 *       Optional fully qualified classname of a FileChooserStep.Discoverer
 *       implementation used to discover a default value for the user's
 *       environment.
 *     </td>
 *     <td>false</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 * </table>
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class FileChooserStep
  extends AbstractFormStep
{
  private static final String ICON = "/images/32x32/folder.png";
  private static final String SELECTION_MODE = "selectionMode";
  private static final String CHOOSABLE = "choosable";
  private static final String DISCOVERER = "discoverer";

  protected static final String PROPERTY = "property";

  private String property;
  private String discoverer;
  private FileFilter[] choosable;
  private int selectionMode = JFileChooser.FILES_AND_DIRECTORIES;

  private GuiFileChooser guiFileChooser;
  //private ConsoleFileChooser consoleFileChooser;

  /**
   * Constructs the welcome step.
   */
  public FileChooserStep (String name)
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

    property = getProperty(PROPERTY);
    if(property == null){
      throw new IllegalArgumentException(
          Installer.getString(PROPERTY_REQUIRED, PROPERTY, getName()));
    }

    String mode = getProperty(SELECTION_MODE);
    if("files".equals(mode)){
      selectionMode = JFileChooser.FILES_ONLY;
    }else if("directories".equals(mode)){
      selectionMode = JFileChooser.DIRECTORIES_ONLY;
    }

    String choose = getProperty(CHOOSABLE);
    choosable = parseChoosable(choose);

    discoverer = getProperty(DISCOVERER);
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
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#displayed()
   */
  public void displayed ()
  {
    if(Installer.isConsoleMode()){
      //consoleFileChooser.requestFocus();
    }else{
      guiFileChooser.grabFocus();
    }
  }

  /**
   * {@inheritDoc}
   * @see AbstractFormStep#initGuiForm()
   */
  public GuiForm initGuiForm ()
  {
    FormLayout layout = new FormLayout("pref, 4dlu, 150dlu");
    GuiFormBuilder builder = new GuiFormBuilder(getName(), layout);
    GuiComponentFactory factory = builder.getFactory();

    Validator required = new ValidatorBuilder().required().validator();
    guiFileChooser = factory.createFileChooser(
        property, getDefaultValue(), required);

    guiFileChooser.getFileChooser().setFileSelectionMode(selectionMode);
    for (int ii = 0; ii < choosable.length; ii++){
      guiFileChooser.getFileChooser().addChoosableFileFilter(choosable[ii]);
    }
    builder.append(guiFileChooser);

    return builder.getForm();
  }

  /**
   * {@inheritDoc}
   * @see AbstractFormStep#initConsoleForm()
   */
  public ConsoleForm initConsoleForm ()
  {
    throw new UnsupportedOperationException("initConsoleForm()");
  }

  /**
   * Gets the default value to use for the file chooser text field.
   *
   * @return The default value.
   */
  private String getDefaultValue ()
  {
    if(discoverer != null){
      try{
        Discoverer instance = (Discoverer)
          Class.forName(discoverer).newInstance();
        return instance.discover();
      }catch(Exception e){
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  /**
   * Parses the supplied choosable string.
   *
   * @param choosable The choosable string.
   * @return The resulting file filters.
   */
  private FileFilter[] parseChoosable (String choosable)
  {
    List results = new ArrayList();

    if(choosable != null){
      String[] values = StringUtils.split(choosable, ',');
      for (int ii = 0; ii < values.length; ii++){
        String[] value = StringUtils.split(values[ii].trim(), ':');
        if(value.length > 1){
          results.add(new ExtensionFileFilter(value[0], value[1]));
        }else{
          results.add(new ExtensionFileFilter(value[0]));
        }
      }
    }

    return (FileFilter[])results.toArray(new FileFilter[results.size()]);
  }

  /**
   * Defines classes that can be used to discover a possible default in the
   * user's environment.
   */
  public interface Discoverer
  {
    /**
     * Invoked to discover the default value.
     *
     * @return The default value or null if none.
     */
    public String discover ();
  }

  /**
   * Implemention of {@link FileFilter} that filters by extension.
   */
  private static class ExtensionFileFilter
    extends FileFilter
  {
    private String ext;
    private String desc;

    /**
     * Constructs a new instance.
     *
     * @param ext The ext for this instance.
     */
    public ExtensionFileFilter (String ext)
    {
      this(ext, null);
    }

    /**
     * Constructs a new instance.
     *
     * @param ext The ext for this instance.
     * @param desc The desc for this instance.
     */
    public ExtensionFileFilter (String ext, String desc)
    {
      this.ext = ext;
      this.desc = desc;
    }

    /**
     * {@inheritDoc}
     * @see FileFilter#accept(File)
     */
    public boolean accept (File f)
    {
      return f.isDirectory() || ext.equalsIgnoreCase(
          FilenameUtils.getExtension(f.getAbsolutePath()));
    }

    /**
     * {@inheritDoc}
     * @see FileFilter#getDescription()
     */
    public String getDescription ()
    {
      return desc;
    }
  }
}
