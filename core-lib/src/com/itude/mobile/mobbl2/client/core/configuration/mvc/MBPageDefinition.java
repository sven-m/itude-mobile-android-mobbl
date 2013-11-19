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

import java.util.List;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;

public class MBPageDefinition extends MBPanelDefinition
{
  public enum MBPageType {
    MBPageTypesNormal, MBPageTypesPopup, MBPageTypesErrorPage
  };

  private String     _documentName;
  private String     _rootPath;
  private MBPageType _pageType;
  private String     _orientationPermissions;
  private boolean    _scrollable        = true;
  private boolean    _reloadOnDocChange = false;

  public String getRootPath()
  {
    return _rootPath;
  }

  public void setRootPath(String rootPath)
  {
    _rootPath = rootPath;
  }

  public MBPageType getPageType()
  {
    return _pageType;
  }

  public void setPageType(MBPageType pageType)
  {
    _pageType = pageType;
  }

  public String getDocumentName()
  {
    return _documentName;
  }

  public void setDocumentName(String name)
  {

    int location = name.indexOf("/");
    if (location > -1)
    {
      _documentName = name.substring(0, location);
      String rp = name.substring(location);
      if (!rp.endsWith("/"))
      {
        rp = rp + "/";
      }
      _rootPath = rp;
    }
    else
    {
      _documentName = name;
      _rootPath = "";
    }

  }

  public String getOrientationPermissions()
  {
    return _orientationPermissions;
  }

  public void setOrientationPermissions(String orientationPermissions)
  {
    _orientationPermissions = orientationPermissions;
  }

  public boolean isScrollable()
  {
    return _scrollable;
  }

  public void setScrollable(boolean scrollable)
  {
    _scrollable = scrollable;
  }

  public boolean isReloadOnDocChange()
  {
    return _reloadOnDocChange;
  }

  public void setReloadOnDocChange(boolean reloadOnDocChange)
  {
    _reloadOnDocChange = reloadOnDocChange;
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<Page name='").append(getName()).append("' document='").append(_documentName)
        .append("'").append(getAttributeAsXml("title", getTitle())).append(">\n");

    List<MBDefinition> children = getChildren();

    for (int i = 0; i < children.size(); i++)
    {
      MBPanelDefinition panelDef = (MBPanelDefinition) children.get(i);
      panelDef.asXmlWithLevel(appendToMe, level + 2);
    }
    return StringUtil.appendIndentString(appendToMe, level).append("</Page>\n");
  }

  @Override
  public String toString()
  {
    return asXmlWithLevel(new StringBuffer(), 0).toString();
  }

}
