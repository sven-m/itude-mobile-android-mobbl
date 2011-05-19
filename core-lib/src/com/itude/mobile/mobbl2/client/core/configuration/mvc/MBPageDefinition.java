package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.List;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBPageDefinition extends MBPanelDefinition
{
  public enum MBPageType {
    MBPageTypesNormal, MBPageTypesPopup, MBPageTypesErrorPage
  };

  private String     _documentName;
  private String     _rootPath;
  private MBPageType _pageType;
  private String     _orientationPermissions;

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

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer p_appendToMe, int level)
  {
    StringUtilities.appendIndentString(p_appendToMe, level).append("<Page name='").append(getName()).append("' document='")
        .append(_documentName).append("'").append(getAttributeAsXml("title", getTitle())).append(">\n");

    List<MBDefinition> children = getChildren();

    for (int i = 0; i < children.size(); i++)
    {
      MBPanelDefinition panelDef = (MBPanelDefinition) children.get(i);
      panelDef.asXmlWithLevel(p_appendToMe, level + 2);
    }
    return StringUtilities.appendIndentString(p_appendToMe, level).append("</Page>\n");
  }

}
