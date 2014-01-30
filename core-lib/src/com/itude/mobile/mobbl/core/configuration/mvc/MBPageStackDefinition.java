/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl.core.configuration.mvc;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.configuration.MBConditionalDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.exceptions.MBInvalidPageStackDefinitionException;

public class MBPageStackDefinition extends MBConditionalDefinition
{

  private String _mode;
  private String _parent;

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtil.appendIndentString(appendToMe, level)//
        .append("<PageStack name='")//
        .append(getName())//
        .append('\'')//
        .append(getAttributeAsXml("mode", _mode))//
        .append("/>\n");
  }

  @Override
  public void validateDefinition()
  {
    if (getName() == null)
    {
      String message = "no name set for pagestack";
      throw new MBInvalidPageStackDefinitionException(message);
    }
  }

  public void setMode(String mode)
  {
    _mode = mode;
  }

  public String getMode()
  {
    return _mode;
  }

  public String getParent()
  {
    return _parent;
  }

  public void setParent(String parent)
  {
    _parent = parent;
  }

}
