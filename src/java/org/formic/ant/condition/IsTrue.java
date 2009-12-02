/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.formic.ant.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

import org.apache.tools.ant.taskdefs.condition.Condition;

/**
 * Version of &lt;istrue&gt; that only supports propery names which are not
 * looked up until evaluation time.<br/>
 * Ex.<br/>
 * &lt;formic:istrue value="myproperty"/&gt;<br/>
 * vs<br/>
 * &lt;istrue value="${myproperty}"/&gt;
 *
 * @author Eric Van Dewoestine
 */
public class IsTrue
  extends ProjectComponent
  implements Condition
{
  /**
   * what we eval
   */
  private String value = null;

  /**
   * set the value to be tested; let ant eval it to true/false
   * @param value the value to test
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * @return the inverted value;
   * @throws BuildException if someone forgot to spec a value
   */
  public boolean eval() throws BuildException {
    if (value == null) {
      throw new BuildException("Nothing to test for truth");
    }
    String val = getProject().getProperty(value);
    val = val != null ? val : value;
    return "true".equalsIgnoreCase(val);
  }
}
