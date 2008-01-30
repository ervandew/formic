package org.sample;

import java.io.File;

import org.apache.tools.ant.taskdefs.condition.Os;

import org.formic.wizard.form.shared.Discoverer;

public class TestDiscoverer
  extends Discoverer
{
  public String discover ()
  {
    if(Os.isFamily("windows")){
      if(new File("C:/Program Files").exists()){
        return "C:\\Program Files\\FormicSamplApp";
      }
    }else{
      if(new File("/opt").exists()){
        return "/opt/formic_sample_app";
      }
    }

    return null;
  }
}
