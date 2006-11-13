
import org.formic.Installer;

import org.formic.form.console.ConsoleForm;

import org.formic.form.gui.GuiForm;

import org.formic.wizard.step.RequirementsValidationStep.Requirement;

import org.formic.wizard.step.RequirementsValidationStep;

public class TestRequirementProvider
  implements RequirementsValidationStep.RequirementProvider
{
  private GuiForm guiForm;
  private ConsoleForm consoleForm;

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

  public int validate (Requirement requirement)
  {
    try{
      Thread.sleep(2000);
    }catch(Exception ignore){
    }
    if("requirement.fail".equals(requirement.getKey())){
      return FAIL;
    }
    return OK;
  }

  public void setGuiForm (GuiForm form)
  {
    this.guiForm = form;
  }

  public void setConsoleForm (ConsoleForm form)
  {
    this.consoleForm = form;
  }
}
