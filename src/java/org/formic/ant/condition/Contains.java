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
 * Version of &lt;contains&gt; that only supports propery names which are not
 * looked up until evaluation time.<br/>
 * Ex.<br/>
 * &lt;formic:contains value="myproperty"/&gt;<br/>
 * vs<br/>
 * &lt;contains value="${myproperty}"/&gt;
 *
 * @author Eric Van Dewoestine
 * @version $Revision$
 */
public class Contains
  extends ProjectComponent
  implements Condition
{
  private String string = null;
  private String subString = null;
  private boolean caseSensitive = true;

  /**
   * The string to search in.
   * @param string the string to search in
   */
  public void setString(String string) {
    this.string = string;
  }

  /**
   * The string to search for.
   * @param subString the string to search for
   */
  public void setSubstring(String subString) {
    this.subString = subString;
  }

  /**
   * Whether to search ignoring case or not.
   * @param b if false, ignore case
   */
  public void setCasesensitive(boolean b) {
    caseSensitive = b;
  }

  /**
   * @return true if the substring is within the string
   * @exception BuildException if the attributes are not set correctly
   */
  public boolean eval() throws BuildException {
    if (string == null || subString == null) {
      throw new BuildException("both string and substring are required "
          + "in contains");
    }

    String _string = getProject().getProperty(string);
    String _subString = getProject().getProperty(subString);
    return caseSensitive
      ? _string.indexOf(_subString) > -1
      : _string.toLowerCase().indexOf(_subString.toLowerCase()) > -1;
  }
}
