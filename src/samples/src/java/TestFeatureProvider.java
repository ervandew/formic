import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.formic.form.console.ConsoleForm;

import org.formic.form.gui.GuiForm;

import org.formic.wizard.step.FeatureListStep;
import org.formic.wizard.step.FeatureListStep.Feature;

public class TestFeatureProvider
  implements FeatureListStep.FeatureProvider
{
  private static final String[] FEATURES = {
    "feature.one",
    "feature.two",
    "feature.three",
    "feature.four",
    "feature.five",
  };
  private static final boolean[] FEATURES_ENABLED = {
    true,
    false,
    false,
    false,
    false,
  };
  private static final String[][] FEATURES_DEPENDS = {
    null,
    null,
    {"feature.two"},
    null,
    {"feature.three", "feature.four"},
  };

  private GuiForm guiForm;
  private ConsoleForm consoleForm;

  public Feature[] getFeatures ()
  {
    final Feature[] features =
      new Feature[FEATURES.length];
    for (int ii = 0; ii < FEATURES.length; ii++){
      features[ii] = new Feature(
          FEATURES[ii], FEATURES_ENABLED[ii], FEATURES_DEPENDS[ii]);
    }

    // make feature 5 require feature 4
    /*features[3].addPropertyChangeListener(new PropertyChangeListener(){
      public void propertyChange (PropertyChangeEvent event){
        if(Feature.ENABLED_PROPERTY.equals(event.getPropertyName())){
          Feature feature = (Feature)event.getSource();
          if(feature.isEnabled() && !features[4].isEnabled()){
            features[4].setEnabled(true);
            if(guiForm != null){
              guiForm.showInfoMessage("Feature 4 requires feature 5.");
            }else if(consoleForm != null){
              // TODO
            }
          }
        }
      }
    });*/
    return features;
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
