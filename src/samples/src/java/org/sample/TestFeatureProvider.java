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
