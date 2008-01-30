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
package org.formic.util.dialog.console;

import charvax.swing.JOptionPane;

import org.formic.Installer;

import org.formic.wizard.impl.console.ConsoleWizard;

/**
 * Provides several messages for displaying console dialogs.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class ConsoleDialogs
{
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
    String t = Installer.getStringOrDefault(title, title);
    String m = Installer.getStringOrDefault(message, message);

    int result = JOptionPane.showConfirmDialog(
        ConsoleWizard.getFrame(), m, t, JOptionPane.YES_NO_OPTION);
    return (result == JOptionPane.YES_OPTION);
  }

  /**
   * Shows an info dialog.
   *
   * @param message The message.
   */
  public static void showInfo (String message)
  {
    showInfo(Installer.getString("info.dialog.title"), message);
  }

  /**
   * Shows an info dialog.
   *
   * @param title The dialog title.
   * @param message The message.
   */
  public static void showInfo (String title, String message)
  {
    showMessage(title, message, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Shows a warning dialog.
   *
   * @param message The message.
   */
  public static void showWarning (String message)
  {
    showWarning(Installer.getString("warning.dialog.title"), message);
  }

  /**
   * Shows a warning dialog.
   *
   * @param title The dialog title.
   * @param message The message.
   */
  public static void showWarning (String title, String message)
  {
    showMessage(title, message, JOptionPane.WARNING_MESSAGE);
  }

  /**
   * Show a message dialog.
   *
   * @param title The dialog title.
   * @param message The message.
   * @param type The type of message.
   */
  private static void showMessage (String title, String message, int type)
  {
    String t = Installer.getStringOrDefault(title, title);
    String m = Installer.getStringOrDefault(message, message);

    JOptionPane.showMessageDialog(ConsoleWizard.getFrame(), m, t, type);
  }

  /**
   * Shows an error dialog.
   *
   * @param message The error message.
   */
  public static void showError (String message)
  {
    showError(null, message, null);
  }

  /**
   * Shows an error dialog.
   *
   * @param title The dialog title.
   * @param message The error message.
   */
  public static void showError (String title, String message)
  {
    showError(title, message, null);
  }

  /**
   * Shows an error dialog.
   *
   * @param thrown The error detail.
   */
  public static void showError (Throwable thrown)
  {
    showError(null, null, thrown);
  }

  /**
   * Shows an error dialog.
   *
   * @param message The error message.
   * @param thrown The error detail.
   */
  public static void showError (String message, Throwable thrown)
  {
    showError(null, message, thrown);
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
    String t = Installer.getStringOrDefault(
        title, Installer.getString("error.dialog.title"));
    String m = Installer.getStringOrDefault(message,
        thrown != null ? thrown.getLocalizedMessage() : message);
    if(m == null){
      m = Installer.getString("error.dialog.text");
    }

    new ConsoleErrorDialog(t, m, thrown).setVisible(true);
  }
}
