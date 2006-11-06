
import org.formic.form.console.ConsoleForm;

import org.formic.form.gui.GuiForm;

import org.formic.wizard.step.RequirementsValidationStep;
import org.formic.wizard.step.RequirementsValidationStep.Requirement;

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
    requirements[2] = new Requirement("requirement.three");
    return requirements;
  }

  public int validate (Requirement requirement)
  {
    try{
      Thread.sleep(2000);
    }catch(Exception ignore){
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
