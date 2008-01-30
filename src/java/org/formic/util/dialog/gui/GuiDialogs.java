/**
 * Formic installer framework.
 * Copyright (C) 2005 - 2008  Eric Van Dewoestine
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
package org.formic.util.dialog.gui;

import java.awt.Image;
import java.awt.Toolkit;

import java.net.URL;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Provides several methods for displaying dialogs.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class GuiDialogs
{
  private static Icon WARNING;
  private static Icon INFO;
  private static Icon CONFIRM;

  private static ResourceBundle RESOURCES;

  /**
   * Shows a confirmation dialog.
   *
   * @param message The message.
   *
   * @return true if the user confirmed, false otherwise.
   */
  public static boolean showConfirm (String message)
  {
    return showConfirm(null, message);
  }

  /**
   * Shows a confirmation dialog.
   *
   * @param title The dialog title.
   * @param message The message.
   *
   * @return true if the user confirmed, false otherwise.
   */
  public static boolean showConfirm (String title, String message)
  {
    String t = getString(title, title);
    String m = getString(message, message);

    int result = JOptionPane.showConfirmDialog(null, m, t,
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, CONFIRM);
    return (result == JOptionPane.YES_OPTION);
  }

  /**
   * Shows an info dialog.
   *
   * @param message The message.
   */
  public static void showInfo (String message)
  {
    showInfo(null, message);
  }

  /**
   * Shows an info dialog.
   *
   * @param title The dialog title.
   * @param message The message.
   */
  public static void showInfo (String title, String message)
  {
    String t = getString(title, title);
    String m = getString(message, message);

    JOptionPane.showMessageDialog(
        null, m, t, JOptionPane.INFORMATION_MESSAGE, INFO);
  }

  /**
   * Shows a warning dialog.
   *
   * @param message The message.
   */
  public static void showWarning (String message)
  {
    showWarning(null, message);
  }

  /**
   * Shows a warning dialog.
   *
   * @param title The dialog title.
   * @param message The message.
   */
  public static void showWarning (String title, String message)
  {
    String t = getString(title, title);
    String m = getString(message, message);

    JOptionPane.showMessageDialog(
        null, m, t, JOptionPane.WARNING_MESSAGE, WARNING);
  }

  /**
   * Shows an error dialog.
   *
   * @param message The error message.
   */
  public static void showError (String message)
  {
    showError(null, message, null, null);
  }

  /**
   * Shows an error dialog.
   *
   * @param thrown The error detail.
   */
  public static void showError (Throwable thrown)
  {
    showError(null, null, thrown, null);
  }

  /**
   * Shows an error dialog.
   *
   * @param message The error message.
   * @param thrown The error detail.
   */
  public static void showError (String message, Throwable thrown)
  {
    showError(null, message, thrown, null);
  }

  /**
   * Shows an error dialog.
   *
   * @param message The error message.
   * @param detail The error detail.
   */
  public static void showError (String message, String detail)
  {
    showError(null, message, null, detail);
  }

  /**
   * Shows an error dialog.
   *
   * @param title The dialog title.
   * @param message The error message.
   * @param thrown The error detail.
   */
  public static void showError (String title, String message, Throwable thrown)
  {
    showError(title, message, thrown, null);
  }

  /**
   * Shows an error dialog.
   *
   * @param title The dialog title.
   * @param message The error message.
   * @param thrown The error detail.
   * @param detail If no exception, the detail to show.
   */
  private static void showError (
      String title, String message, Throwable thrown, String detail)
  {
    String t = getString(title, RESOURCES.getString("error.dialog.title"));

    String m = getString(message, message);
    if(m == null || m.length() == 0){
      m = thrown != null ? thrown.getLocalizedMessage() :
        RESOURCES.getString("error.dialog.text");

      if(m == null){
        m = RESOURCES.getString("error.dialog.text");
      }
    }

    new GuiErrorDialog(t, m, thrown, detail).setVisible(true);
  }

  /**
   * Sets the resources for the error dialogs.
   *
   * @param bundle The ResourceBundle.
   */
  public static void setBundle (ResourceBundle bundle)
  {
    if(bundle != null){
      RESOURCES = bundle;

      GuiErrorDialog.setBundle(bundle);

      WARNING = new ImageIcon(getImage(bundle, "warning.dialog.image"));
      INFO = new ImageIcon(getImage(bundle, "info.dialog.image"));
      CONFIRM = new ImageIcon(getImage(bundle, "confirm.dialog.image"));
    }
  }

  /**
   * Gets an image from the supplied ResourceBundle.
   *
   * @param bundle The ResourceBundle.
   * @param image The image.
   * @return The Image.
   */
  static Image getImage (ResourceBundle bundle, String image)
  {
    String path = bundle.getString(image);
    if(path == null){
      path = image;
    }

    URL url = GuiDialogs.class.getResource(path);
    return url != null ? Toolkit.getDefaultToolkit().createImage(url) : null;
  }

  /**
   * Gets a string from the resouce bundle, return the default if not found.
   *
   * @param key The resource key.
   * @param dflt The default if not found.
   * @return The value.
   */
  private static String getString (String key, String dflt)
  {
    try{
      return key != null ? RESOURCES.getString(key) : dflt;
    }catch(MissingResourceException mre){
      return dflt;
    }
  }
}
