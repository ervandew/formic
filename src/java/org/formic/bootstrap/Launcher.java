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
package org.formic.bootstrap;

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

import org.formic.bootstrap.util.Extractor;

/**
 * Class responsible for extracting embedded archive and kicking off the ant
 * process.
 *
 * @author Eric Van Dewoestine (ervandew@yahoo.com)
 * @version $Revision$
 */
public class Launcher
  extends Thread
  implements Extractor.ArchiveExtractionListener
{
  private static final String TITLE = "Initializing Installer";

  private JFrame frame;
  private JProgressBar progressBar;
  private String tempDir;
  private String[] args;

  /**
   * Main method.
   *
   * @param args Command line arguments.
   */
  public static void main (String[] args)
  {
    Launcher launcher = new Launcher(args);
    launcher.start();
  }

  /**
   * Constructs a new instance.
   *
   * @param args The command line arguments.
   */
  public Launcher (String[] args)
  {
    this.args = args;

    frame = new JFrame(TITLE);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    // add Label
    JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    labelPanel.add(new JLabel(TITLE + "..."));
    panel.add(labelPanel);

    // add ProgressBar
    progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setPreferredSize(new Dimension(250, 15));
    panel.add(progressBar);

    // add Cancel Button
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setPreferredSize(new Dimension(75, 25));
    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed (ActionEvent event) {
        cancel();
        frame.setVisible(false);
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
  }

  /**
   * {@inheritDoc}
   * @see Runnable#run()
   */
  public void run ()
  {
    try{
      extractArchive();
      frame.setVisible(false);
      runInstaller(args);
    }catch(Exception e){
      // FIXME: show error dialog.
      e.printStackTrace();
    }finally{
      /*try{
        cleanup();
      }catch(Exception e){
      }*/
    }

    System.exit(0);
  }

  /**
   * Cancel execution of this thread.
   */
  private void cancel ()
  {
    interrupt();
    try{
      join(2000);
      cleanup();
    }catch(Exception e){
      // FIXME: show error dialog.
      e.printStackTrace();
    }
  }

  /**
   * Extract the embedded archive to a temp directory.
   */
  private void extractArchive ()
    throws Exception
  {
    tempDir = System.getProperty("java.io.tmpdir").replace('\\', '/') +
      "/formic_" + Math.abs(new Random().nextInt());

    File dir = new File(tempDir);
    if(!dir.exists()){
      dir.mkdirs();
    }

    String archive = tempDir + "/formic.zip";
    Extractor.readArchive("/formic.zip", archive);
    Extractor.extractArchive(archive, tempDir, this);

    // delete temp archive.
    new File(archive).delete();
  }

  /**
   * Run the installer.
   *
   * @param args Supplied arguments for installer.
   */
  private void runInstaller (String[] args)
    throws Exception
  {
    int offset = 3;
    String[] cmd = new String[args.length + 3];
    System.arraycopy(args, 0, cmd, 3, args.length);
    cmd[0] = tempDir.substring(0, 2);
    cmd[1] = "&&";
    cmd[2] = tempDir + "/formic.bat";

System.out.println("#### cmd = " + cmd[0]);
System.out.println("#### cmd = " + cmd[1]);
System.out.println("#### cmd = " + cmd[2]);

    Process process = Runtime.getRuntime().exec(cmd);
    process.waitFor();
  }

  /**
   * Performs any necessary cleanup.
   */
  private void cleanup ()
    throws Exception
  {
    File tempDir = new File(this.tempDir);
    deleteDir(tempDir);
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
        deleteDir(files[ii]);
      }else{
        files[ii].delete();
      }
    }
    dir.delete();
  }

  /**
   * {@inheritDoc}
   * @see Extractor.ArchiveExtractionListener#startExtraction(int)
   */
  public void startExtraction (int count)
  {
    progressBar.setIndeterminate(false);
    progressBar.setMaximum(count);
    progressBar.setStringPainted(true);
  }

  /**
   * {@inheritDoc}
   * @see Extractor.ArchiveExtractionListener#finishExtraction()
   */
  public void finishExtraction ()
  {
    progressBar.setValue(progressBar.getMaximum());
  }

  /**
   * {@inheritDoc}
   * @see Extractor.ArchiveExtractionListener#startExtractingFile(int,String)
   */
  public void startExtractingFile (int index, String file)
  {
    progressBar.setValue(index);
  }

  /**
   * {@inheritDoc}
   * @see Extractor.ArchiveExtractionListener#finishExtractingFile(int,String)
   */
  public void finishExtractingFile (int index, String file)
  {
  }
}
