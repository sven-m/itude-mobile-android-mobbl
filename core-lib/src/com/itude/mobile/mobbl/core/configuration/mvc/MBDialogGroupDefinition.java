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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.configuration.MBConditionalDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.exceptions.MBInvalidPageStackDefinitionException;
import com.itude.mobile.mobbl.core.util.Constants;

public class MBDialogGroupDefinition extends MBConditionalDefinition
{
  private final List<MBPageStackDefinition>        _children;
  private final Map<MBPageStackDefinition, String> _childrenPreCondition;

  private String                                   _title;
  private String                                   _titlePortrait;
  private String                                   _mode;
  private String                                   _icon;
  private String                                   _showAs;
  private String                                   _domain;

  public MBDialogGroupDefinition()
  {
    _children = new ArrayList<MBPageStackDefinition>();
    _childrenPreCondition = new HashMap<MBPageStackDefinition, String>();
  }

  public void addPageStack(MBPageStackDefinition pageStackDef)
  {
    if (_children.contains(pageStackDef))
    {
      Log.w(Constants.APPLICATION_NAME, "Group contains duplicate definitions for pagestack " + pageStackDef.getName() + " in dialog "
                                        + getName());
    }

    _children.add(pageStackDef);
    _childrenPreCondition.put(pageStackDef, pageStackDef.getPreCondition());
    pageStackDef.setParent(getName());
  }

  @Override
  public void addChildElement(MBPageStackDefinition child)
  {
    addPageStack(child);
  }

  public MBPageStackDefinition getPageStackDefinition(String name)
  {
    for (MBPageStackDefinition pageStackDef : _children)
    {
      if (pageStackDef.getName().equals(name)) return pageStackDef;
    }

    return null;
  }

  @Override
  public void validateDefinition()
  {
    if (getName() == null)
    {
      throw new MBInvalidPageStackDefinitionException("no name set for dialogGroup");
    }
  }

  @Override
  public boolean isPreConditionValid()
  {
    boolean valid = super.isPreConditionValid();

    if (valid)
    {
      // then reset pre conditions
      for (MBPageStackDefinition child : getChildren())
      {
        child.setPreCondition(_childrenPreCondition.get(child));
      }
    }
    else
    {
      // then override children to also be invalid
      for (MBPageStackDefinition child : getChildren())
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
        .append(getAttributeAsXml("showAs", _showAs))//
        .append(getAttributeAsXml("domain", _domain))//
        .append(">\n");

    for (MBPageStackDefinition dialog : _children)
    {
      dialog.asXmlWithLevel(appendToMe, level + 2);
    }

    StringUtil.appendIndentString(appendToMe, level).append("<DialogGroup/>");

    return appendToMe;
  }

  public List<MBPageStackDefinition> getChildren()
  {
    return _children;
  }

  public String getTitle()
  {
    return _title;
  }

  public void setTitle(String title)
  {
    _title = title;
  }

  public void setTitlePortrait(String titlePortrait)
  {
    _titlePortrait = titlePortrait;
  }

  public String getTitlePortrait()
  {
    return _titlePortrait != null ? _titlePortrait : getTitle();
  }

  public String getMode()
  {
    return _mode;
  }

  public void setMode(String mode)
  {
    _mode = mode;
  }

  public String getIcon()
  {
    return _icon;
  }

  public void setIcon(String icon)
  {
    _icon = icon;
  }

  public String getShowAs()
  {
    return _showAs;
  }

  public void setShowAs(String showAs)
  {
    _showAs = showAs;
  }

  public boolean isShowAsTab()
  {
    return Constants.C_SHOW_AS_TAB.equals(_showAs);
  }

  public boolean isShowAsMenu()
  {
    return Constants.C_SHOW_AS_MENU.equals(_showAs);
  }

  public boolean isShowAsDocument()
  {
    return Constants.C_SHOW_AS_DOCUMENT.equals(_showAs);
  }

  public void setDomain(String domain)
  {
    _domain = domain;
  }

  public String getDomain()
  {
    return _domain;
  }

}
