package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBToolDefinition extends MBDefinition
{
  private String _type;
  private String _action;
  private String _icon;
  private String _title;
  private String _preCondition;

  public StringBuffer asXmlWithLevel(StringBuffer p_appendToMe, int level)
  {
    return StringUtilities.appendIndentString(p_appendToMe, level).append("<Tool name='").append(getName()).append('\'')
        .append(getAttributeAsXml("type", _type)).append(getAttributeAsXml("title", _title)).append(getAttributeAsXml("icon", _icon))
        .append("/>\n");
  }

  public void setType(String type)
  {
    _type = type;
  }

  public String getType()
  {
    return _type;
  }

  public void setAction(String action)
  {
    _action = action;
  }

  public String getAction()
  {
    return _action;
  }

  public void setIcon(String icon)
  {
    _icon = icon;
  }

  public String getIcon()
  {
    return _icon;
  }

  public void setTitle(String title)
  {
    _title = title;
  }

  public String getTitle()
  {
    return _title;
  }

  public void setPreCondition(String preCondition)
  {
    _preCondition = preCondition;
  }

  public String getPreCondition()
  {
    return _preCondition;
  }
}
