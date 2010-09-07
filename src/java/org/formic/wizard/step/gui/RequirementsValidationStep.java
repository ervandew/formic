/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2010 Eric Van Dewoestine
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import foxtrot.Task;
import foxtrot.Worker;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import org.formic.Installer;

import org.formic.util.dialog.gui.GuiDialogs;

import org.formic.util.event.gui.HyperlinkListener;

import org.formic.util.swing.ComponentTableCellRenderer;

import org.formic.wizard.form.GuiForm;

import org.formic.wizard.step.AbstractGuiStep;

/**
 * Step which validates set of requirements.
 * <p/>
 * <b>Properties</b>
 * <table class="properties">
 *   <tr>
 *     <th>Name</th><th>Description</th>
 *     <th>Required</th><th>Possible Values</th><th>Default</th>
 *   </tr>
 *   <tr>
 *     <td>provider</td>
 *     <td>Implementation of {@link RequirementProvider}.</td>
 *     <td>true</td><td>&nbsp;</td><td>none</td>
 *   </tr>
 * </table>
 *
 * @author Eric Van Dewoestine
 */
public class RequirementsValidationStep
  extends AbstractGuiStep
{
  private static final Integer OK = new Integer(0);
  private static final Integer WARN = new Integer(1);
  private static final Integer FAIL = new Integer(2);

  protected static final String PROVIDER = "provider";

  private RequirementProvider provider;

  private GuiForm form;
  private JEditorPane requirementInfo;
  private JTable table;
  private JButton retryButton;

  private ImageIcon busyIcon;
  private ImageIcon okIcon;
  private ImageIcon warnIcon;
  private ImageIcon failedIcon;

  /**
   * Constructs the step.
   */
  public RequirementsValidationStep(String name, Properties properties)
  {
    super(name, properties);

    String provider = getProperty(PROVIDER);
    if(provider == null){
      throw new IllegalArgumentException(
          Installer.getString(PROPERTY_REQUIRED, PROVIDER, getName()));
    }

    try{
      this.provider = (RequirementProvider)Class.forName(provider).newInstance();
    }catch(ClassCastException cce){
      throw new IllegalArgumentException(Installer.getString(
            PROPERTY_TYPE_INVALID, PROVIDER, RequirementProvider.class.getName()));
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
    busyIcon = new ImageIcon(Installer.getImage(getName() + ".busy"));
    okIcon = new ImageIcon(Installer.getImage(getName() + ".ok"));
    warnIcon = new ImageIcon(Installer.getImage(getName() + ".warning"));
    failedIcon = new ImageIcon(Installer.getImage(getName() + ".failed"));

    requirementInfo = new JEditorPane("text/html", StringUtils.EMPTY);
    requirementInfo.setEditable(false);
    requirementInfo.addHyperlinkListener(new HyperlinkListener());

    table = new JTable(1, 2){
      public Class getColumnClass(int column){
        Object value = getValueAt(0, column);
        return value != null ? value.getClass() : Object.class;
      }
      public boolean isCellEditable(int row, int column){
        return false;
      }
    };

    table.setBackground(new javax.swing.JList().getBackground());
    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setShowHorizontalLines(false);
    table.setShowVerticalLines(false);
    table.setDefaultRenderer(JLabel.class, new ComponentTableCellRenderer());

    table.getSelectionModel().addListSelectionListener(
        new RequirementsSelectionListener(table));

    table.setRowSelectionInterval(0, 0);

    retryButton = new JButton(Installer.getString("requirements.retry"));
    retryButton.setVisible(false);
    retryButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        displayed();
      }
    });

    form = createForm();

    JPanel container = new JPanel(new BorderLayout());
    container.add(table, BorderLayout.CENTER);
    JScrollPane infoScroll = new JScrollPane(requirementInfo);

    JPanel panel = new JPanel(new MigLayout(
          "wrap 1", "[fill]", "[] [fill, grow] [fill] []"));
    panel.add(form.createMessagePanel());
    panel.add(new JScrollPane(container), "grow");
    panel.add(infoScroll, "height 50!");
    panel.add(retryButton, "right, width 50!");

    return panel;
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#prepare()
   */
  public void prepare()
  {
    Requirement[] requirements = provider.getRequirements();
    DefaultTableModel model = new DefaultTableModel(requirements.length, 2);
    for (int ii = 0; ii < requirements.length; ii++){
      Requirement requirement = (Requirement)requirements[ii];
      requirement.setTitle(Installer.getString(
            getName() + '.' + requirement.getKey()));
      requirement.setInfo(Installer.getString(
            getName() + '.' + requirement.getKey() + ".html"));

      model.setValueAt(requirement, ii, 0);
      model.setValueAt(new JLabel(), ii, 1);
    }
    table.setModel(model);
    table.getColumnModel().getColumn(1).setMaxWidth(20);
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#displayed()
   */
  public void displayed()
  {
    form.showInfoMessage(null);
    requirementInfo.setText(null);
    retryButton.setEnabled(false);

    setBusy(true);
    try{
      Integer result = (Integer)Worker.post(new Task(){
        public Object run()
          throws Exception
        {
          Integer result = OK;
          TableModel model = table.getModel();
          for (int ii = 0; ii < model.getRowCount(); ii++){
            final JLabel label = (JLabel)model.getValueAt(ii, 1);
            SwingUtilities.invokeLater(new Runnable(){
              public void run(){
                label.setIcon(busyIcon);
                busyIcon.setImageObserver(table);
                table.revalidate();
                table.repaint();
              }
            });
            Requirement requirement = (Requirement)model.getValueAt(ii, 0);
            final RequirementProvider.Status status =
              provider.validate(requirement);
            requirement.setStatus(status);
            if(status.getCode() == RequirementProvider.FAIL){
              result = FAIL;
            }else if(OK.equals(result) &&
                status.getCode() == RequirementProvider.WARN)
            {
              result = WARN;
            }

            SwingUtilities.invokeLater(new Runnable(){
              public void run(){
                switch(status.getCode()){
                  case RequirementProvider.OK:
                    label.setIcon(okIcon);
                    break;
                  case RequirementProvider.WARN:
                    label.setIcon(warnIcon);
                    break;
                  default:
                    label.setIcon(failedIcon);
                }
                table.revalidate();
                table.repaint();
              }
            });
          }
          return result;
        }
      });
      if(FAIL.equals(result)){
        form.showErrorMessage(
            Installer.getString("requirements.message.failed"));
      }else if(WARN.equals(result)){
        form.showWarningMessage(
            Installer.getString("requirements.message.warning"));
      }else{
        form.showInfoMessage(Installer.getString("requirements.message.ok"));
      }
      boolean valid = !result.equals(FAIL);
      setValid(valid);
      retryButton.setVisible(retryButton.isVisible() || !valid);
      retryButton.setEnabled(!valid);
    }catch(Exception e){
      GuiDialogs.showError(e);
      setValid(false);
    }finally{
      setBusy(false);
    }
  }

  /**
   * {@inheritDoc}
   * @see org.formic.wizard.WizardStep#isBusyAnimated()
   */
  public boolean isBusyAnimated()
  {
    return false;
  }

  /**
   * Represents a requirement to be validated.
   */
  public static class Requirement
  {
    private String key;
    private String title;
    private String info;
    private RequirementProvider.Status status;

    /**
     * Constructs a new instance.
     *
     * @param key The key for this instance.
     */
    public Requirement(String key)
    {
      this.key = key;
    }

    /**
     * Gets the key for this instance.
     *
     * @return The key.
     */
    public String getKey()
    {
      return this.key;
    }

    /**
     * Gets the title for this instance.
     *
     * @return The title.
     */
    private String getTitle()
    {
      return this.title;
    }

    /**
     * Sets the title for this instance.
     *
     * @param title The title.
     */
    private void setTitle(String title)
    {
      this.title = title;
    }

    /**
     * Gets the info for this instance.
     *
     * @return The info.
     */
    private String getInfo()
    {
      return this.info;
    }

    /**
     * Sets the info for this instance.
     *
     * @param info The info.
     */
    private void setInfo(String info)
    {
      this.info = info;
    }

    /**
     * Gets the status for this instance.
     *
     * @return The status.
     */
    public RequirementProvider.Status getStatus()
    {
      return this.status;
    }

    /**
     * Sets the status for this instance.
     *
     * @param status The status.
     */
    public void setStatus(RequirementProvider.Status status)
    {
      this.status = status;
    }

    /**
     * {@inheritDoc}
     * @see Object#toString()
     */
    public String toString()
    {
      return title;
    }
  }

  /**
   * Represents a requirement to be validated.
   */
  public static abstract class ValidatingRequirement
    extends Requirement
  {
    public ValidatingRequirement(String key)
    {
      super(key);
    }

    public abstract RequirementProvider.Status validate();
  }

  /**
   * Provides and validates requirements.
   */
  public static interface RequirementProvider
  {
    /**
     * Requirement was met.
     */
    public static final int OK = 1;

    /**
     * Requirement was not met, but installation can proceed anyways..
     */
    public static final int WARN = 2;

    /**
     * Requirement was not met and installation cannot proceed.
     */
    public static final int FAIL = 3;

    /**
     * Status with a code of OK and no message.
     */
    public static final Status OK_STATUS = new Status(OK, StringUtils.EMPTY);

    /**
     * Gets the requirements to be validated.
     *
     * @return Array of Requirement.
     */
    public Requirement[] getRequirements();

    /**
     * Validates the supplied Requirement returning a status with a code of
     * either {@link #OK}, {@link #WARN}, or {@link #FAIL} depending on whether
     * the requirement was satisfied, not satisfied but can be ignores, or not
     * satisified and installer must not proceed.
     *
     * @param requirement The requirement to validate.
     * @return The status of the validation.
     */
    public Status validate(Requirement requirement);

    /**
     * Represents a status returned when validating a requirement.
     */
    public static class Status
    {
      private int code;
      private String message;

      /**
       * Constructs a new instance.
       *
       * @param code The code for this instance.
       * @param message The message for this instance.
       */
      public Status(int code, String message)
      {
        this.code = code;
        this.message = message;
      }

      /**
       * Gets the code for this instance.
       *
       * @return The code.
       */
      public int getCode()
      {
        return this.code;
      }

      /**
       * Gets the message for this instance.
       *
       * @return The message.
       */
      public String getMessage()
      {
        return this.message;
      }
    }
  }

  /**
   * List selection listener responsible for updating requirement info text
   * area.
   */
  private class RequirementsSelectionListener
    implements ListSelectionListener
  {
    private JTable table;

    /**
     * Constructs a new instance.
     */
    public RequirementsSelectionListener(JTable table)
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
        int row = table.getSelectedRow();
        if(row >= 0){
          Requirement requirement = (Requirement)
            table.getModel().getValueAt(row, 0);
          if(requirement != null){
            requirementInfo.setText(requirement.getInfo());
            if(requirement.getStatus() != null){
              switch(requirement.getStatus().getCode()){
                case RequirementProvider.OK:
                  form.showInfoMessage(requirement.getStatus().getMessage());
                  break;
                case RequirementProvider.WARN:
                  form.showWarningMessage(
                      requirement.getStatus().getMessage());
                  break;
                default:
                  form.showErrorMessage(requirement.getStatus().getMessage());
              }
            }
          }
        }
      }
    }
  }
}
