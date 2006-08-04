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
package org.formic.bootstrap;

import ca.beq.util.win32.registry.RegistryKey;
import ca.beq.util.win32.registry.RegistryValue;
import ca.beq.util.win32.registry.RootKey;

/**
 * Initial cut at methods for registering an uninstaller under Windows.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class Uninstaller
{
  private static final String UNINSTALL =
    "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\";
  private static final String DISPLAY_NAME = "DisplayName";
  private static final String UNINSTALL_STRING = "UninstallString";

  /**
   * java -cp lib/jRegistryKey.jar:.
   *   -Djava.library.path=lib/native/windows Uninstaller
   */
  public static void main (String[] args)
  {
    //register("A Small Test App",
    //  "A Small Test App (remove only)", "c:\\test.bat");
    unregister("A Small Test App");
  }

  /**
   * Registers an uninstaller in the Windows registry.
   *
   * @param productName Product name to use.
   * @param displayName Name to display in the Windows Add/Remove Programs list.
   * @param uninstaller Path to the uninstaller to execute.
   */
  public static void register (
      String productName, String displayName, String uninstaller)
  {
    RegistryKey key = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,
        UNINSTALL + normalizeProductName(productName));
    if(!key.exists()){
      key.create();
    }
    RegistryValue value = new RegistryValue(DISPLAY_NAME, displayName);
    key.setValue(value);
    value = new RegistryValue(UNINSTALL_STRING, uninstaller);
    key.setValue(value);
  }

  /**
   * Removes the registered uninstaller for the supplied product name.
   *
   * @param productName Product name to use.
   */
  public static void unregister (String productName)
  {
    RegistryKey key = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,
        UNINSTALL + normalizeProductName(productName));
    if(key.exists()){
      key.delete();
    }
  }

  /**
   * Normalize the supplied name to ensure it is suitable to be used a registry
   * key.
   *
   * @param productName The product name.
   */
  private static String normalizeProductName (String productName)
  {
    StringBuffer name = new StringBuffer();
    char[] chars = productName.toCharArray();
    for(int ii = 0; ii < chars.length; ii++){
      if(Character.isLetterOrDigit(chars[ii])){
        name.append(chars[ii]);
      }
    }
    return name.toString();
  }
}
