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
 * Version of &lt;equals&gt; that only supports propery names which are not
 * looked up until evaluation time.<br/>
 * Ex.<br/>
 * &lt;formic:equals arg1="myproperty" arg2="otherproperty"/&gt;<br/>
 * vs<br/>
 * &lt;equals arg1="${myproperty}" arg2="${otherproperty}"/&gt;
 *
 * @author Eric Van Dewoestine
 */
public class Equals
  extends ProjectComponent
  implements Condition
{
  private String arg1, arg2;
  private boolean trim = false;
  private boolean caseSensitive = true;

  /**
   * Set the first string
   *
   * @param a1 the first string
   */
  public void setArg1(String a1) {
    arg1 = a1;
  }

  /**
   * Set the second string
   *
   * @param a2 the second string
   */
  public void setArg2(String a2) {
    arg2 = a2;
  }

  /**
   * Should we want to trim the arguments before comparing them?
   * @param b if true trim the arguments
   * @since Ant 1.5
   */
  public void setTrim(boolean b) {
    trim = b;
  }

  /**
   * Should the comparison be case sensitive?
   * @param b if true use a case sensitive comparison (this is the
   *          default)
   * @since Ant 1.5
   */
  public void setCasesensitive(boolean b) {
    caseSensitive = b;
  }

  /**
   * @return true if the two strings are equal
   * @exception BuildException if the attributes are not set correctly
   */
  public boolean eval() throws BuildException {
    if (arg1 == null || arg2 == null) {
      throw new BuildException("both arg1 and arg2 are required in "
          + "equals");
    }

    String _arg1 = getProject().getProperty(arg1);
    String _arg2 = getProject().getProperty(arg2);
    if (trim) {
      _arg1 = _arg1.trim();
      _arg2 = _arg2.trim();
    }

    return caseSensitive ? _arg1.equals(_arg2) : _arg1.equalsIgnoreCase(_arg2);
  }
}
