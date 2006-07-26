/**
 * Formic installer framework.
 * Copyright (C) 2004 - 2006  Eric Van Dewoestine
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
package org.formic.bootstrap.windows;

import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

/**
 * Class responsible for extracting embedded archive and kicking off the ant
 * process.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class Launcher
  extends Thread
{
  private static final String TITLE = "Initializing Installer";
  private JFrame frame;
  private String tempDir;

  /**
   * Main method.
   *
   * @param args Command line arguments.
   */
  public static void main (String[] args)
  {
    JFrame frame = new JFrame(TITLE);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    final Launcher launcher = new Launcher(frame);

    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    // add Label
    JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    labelPanel.add(new JLabel(TITLE + "..."));
    panel.add(labelPanel);

    // add ProgressBar
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setPreferredSize(new Dimension(250, 15));
    panel.add(progressBar);

    // add Cancel Button
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setPreferredSize(new Dimension(75, 25));
    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed (ActionEvent event) {
        launcher.interrupt();
        try{
          launcher.join(2000);
        }catch(Exception e){
          e.printStackTrace();
        }
        launcher.cleanup();
        System.exit(0);
      }
    });
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(cancelButton);
    panel.add(buttonPanel);

    frame.getContentPane().add(panel);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    launcher.start();
  }

  /**
   * Constructs a new instance.
   * @param frame The JFrame showing the progress of the bootstrap process.
   */
  public Launcher (JFrame frame)
  {
    this.frame = frame;
  }

  /**
   * {@inheritDoc}
   * @see Runnable#run()
   */
  public void run ()
  {
    extractArchive();
    try{
      while(true){
        Thread.sleep(3000);
      }
    }catch(InterruptedException ie){
    }
    frame.setVisible(false);
    //runInstaller();

    System.exit(0);
  }

  /**
   * Extract the embedded archive to a temp directory.
   */
  private void extractArchive ()
  {
    tempDir = System.getProperty("java.io.tmpdir") +
      "/formic_" + Math.abs(new Random().nextInt());
  }

  /**
   * Run the installer.
   */
  private void runInstaller ()
  {
    String[] cmd = {
      tempDir + "/ant/bin/ant",
      "-logger", "org.formic.ant.Log4jLogger",
      "-lib", tempDir,
      "-f", tempDir + "/install.xml"
    };

    try{
      Process process = Runtime.getRuntime().exec(cmd);
      process.waitFor();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Performs any necessary cleanup.
   */
  private void cleanup ()
  {
    try{
      File tempDir = new File(this.tempDir);
      deleteDir(tempDir);
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Deletes the specified directory.
   *
   * @param dir The directory to delete.
   */
  private void deleteDir (File dir)
    throws Exception
  {
    File[] files = dir.listFiles();
    for (int ii = 0; ii < files.length; ii++){
      if(files[ii].isDirectory()){
        deleteDir(dir);
      }else{
        files[ii].delete();
      }
    }
    dir.delete();
  }
}
