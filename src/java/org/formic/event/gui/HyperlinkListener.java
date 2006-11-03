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
package org.formic.event.gui;

import java.io.IOException;

import javax.swing.JOptionPane;

import javax.swing.event.HyperlinkEvent;

/**
 * Listener to open the user's default browser (win, mac), or first browser
 * found on the system (linux, unix).
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class HyperlinkListener
  implements javax.swing.event.HyperlinkListener
{
  private String[] browser;

  /**
   * {@inheritDoc}
   * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(HyperlinkEvent)
   */
  public void hyperlinkUpdate (HyperlinkEvent _event)
  {
    if (_event.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
      launchBrowser(_event.getURL().toString());
    }
  }

  // initially based on "Bare Bones Browser Launch"
  // http://www.centerkey.com/java/browser/myapp/BareBonesBrowserLaunch.java
  public void launchBrowser (String url)
  {
    if(browser == null){
      String osName = System.getProperty("os.name");
      if (osName.startsWith("Mac OS")) {
        /*Class fileMgr = Class.forName("com.apple.eio.FileManager");
          Method openURL = fileMgr.getDeclaredMethod("openURL",
          new Class[] {String.class});
          openURL.invoke(null, new Object[] {url});*/
        browser = new String[]{"open"};
      } else if (osName.startsWith("Windows")){
        browser = new String[]{"rundll32", "url.dll,FileProtocolHandler"};
      } else if (osName.startsWith("SunOS")) {
        browser = new String[]{"/usr/dt/bin/sdtwebclient"};
      } else { //assume Unix or Linux
        String[] browsers = {
          "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
        try{
          for (int ii = 0; ii < browsers.length; ii++){
            int result = Runtime.getRuntime().exec(
                new String[] {"which", browsers[ii]}).waitFor();
            if (result == 0){
              browser = new String[]{browsers[ii]};
              break;
            }
          }
          if (browser == null){
            throw new RuntimeException("Could not find a web browser.");
          }
        }catch(IOException ioe){
          throw new RuntimeException(ioe);
        }catch(InterruptedException ie){
          throw new RuntimeException(ie);
        }
      }
    }

    try {
      String[] command = new String[browser.length + 1];
      System.arraycopy(browser, 0, command, 0, browser.length);
      command[browser.length] = url;
      Runtime.getRuntime().exec(command);
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
    }
  }
}
