package org.sample;

import org.formic.Installer;

import org.formic.wizard.step.gui.RequirementsValidationStep;
import org.formic.wizard.step.gui.RequirementsValidationStep.Requirement;

public class TestRequirementProvider
  implements RequirementsValidationStep.RequirementProvider
{
  private int tries = 0;

  public Requirement[] getRequirements ()
  {
    Requirement[] requirements = new Requirement[3];
    requirements[0] = new Requirement("requirement.one");
    requirements[1] = new Requirement("requirement.two");
    boolean three = ((Boolean)
        Installer.getContext().getValue("featureList.feature.three"))
      .booleanValue();
    if(three){
      requirements[2] = new Requirement("requirement.fail");
    }else{
      requirements[2] = new Requirement("requirement.three");
    }
    return requirements;
  }

  public Status validate (Requirement requirement)
  {
    try{
      Thread.sleep(2000);
    }catch(Exception ignore){
    }
    if(tries == 0 && "requirement.fail".equals(requirement.getKey())){
      tries++;
      return new Status(FAIL, "A test failure message.");
    }
    return OK_STATUS;
  }
}
