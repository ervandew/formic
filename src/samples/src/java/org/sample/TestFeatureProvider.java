package org.sample;

import org.formic.wizard.step.shared.Feature;
import org.formic.wizard.step.shared.FeatureProvider;

public class TestFeatureProvider
  implements FeatureProvider
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

  public Feature[] getFeatures ()
  {
    final Feature[] features =
      new Feature[FEATURES.length];
    for (int ii = 0; ii < FEATURES.length; ii++){
      features[ii] = new Feature(
          FEATURES[ii], FEATURES_ENABLED[ii], FEATURES_DEPENDS[ii]);
    }
    return features;
  }
}
