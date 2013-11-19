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
package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidDialogDefinitionException;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBDialogGroupDefinition extends MBDialogDefinition
{
  private final List<MBDialogDefinition>        _children;
  private final Map<MBDialogDefinition, String> _childrenPreCondition;

  public MBDialogGroupDefinition()
  {
    _children = new ArrayList<MBDialogDefinition>();
    _childrenPreCondition = new HashMap<MBDialogDefinition, String>();
  }

  public void addDialog(MBDialogDefinition dialogDef)
  {
    if (_children.contains(dialogDef))
    {
      Log.w(Constants.APPLICATION_NAME, "Group contains duplicate definitions for dialog " + dialogDef.getName() + " in group " + getName());
    }

    _children.add(dialogDef);
    _childrenPreCondition.put(dialogDef, dialogDef.getPreCondition());
  }

  @Override
  public void addChildElement(MBDialogDefinition child)
  {
    addDialog(child);
  }

  public MBDialogDefinition getDialogDefinition(String name)
  {
    for (MBDialogDefinition dialogDef : _children)
    {
      if (dialogDef.getName().equals(name)) return dialogDef;
    }

    return null;
  }

  @Override
  public void validateDefinition()
  {
    if (getName() == null)
    {
      throw new MBInvalidDialogDefinitionException("no name set for dialogGroup");
    }
  }

  @Override
  public boolean isPreConditionValid()
  {
    boolean valid = super.isPreConditionValid();

    if (valid)
    {
      // then reset pre conditions
      for (MBDialogDefinition child : getChildren())
      {
        child.setPreCondition(_childrenPreCondition.get(child));
      }
    }
    else
    {
      // then override children to also be invalid
      for (MBDialogDefinition child : getChildren())
      {
        child.setPreCondition(Constants.C_FALSE);
      }
    }

    return valid;
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level)//
        .append("<DialogGroup name='")//
        .append(getName())//
        .append('\'')//
        .append(getAttributeAsXml("mode", getMode()))//
        .append(getAttributeAsXml("title", getTitle()))//
        .append(getAttributeAsXml("titlePortrait", getTitlePortrait()))//
        .append(getAttributeAsXml("icon", getIcon()))//
        .append(">\n");

    for (MBDialogDefinition dialog : _children)
    {
      dialog.asXmlWithLevel(appendToMe, level + 2);
    }

    StringUtil.appendIndentString(appendToMe, level).append("<DialogGroup/>");

    return appendToMe;
  }

  public List<MBDialogDefinition> getChildren()
  {
    return _children;
  }

  @Override
  public boolean isGroup()
  {
    return true;
  }

}
