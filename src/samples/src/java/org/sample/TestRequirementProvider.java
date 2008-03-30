/**
 * Copyright (c) 2005 - 2008
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
