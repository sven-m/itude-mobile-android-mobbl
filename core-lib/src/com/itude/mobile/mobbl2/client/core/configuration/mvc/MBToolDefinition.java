package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBConditionalDefinition;

public class MBToolDefinition extends MBConditionalDefinition
{
  private String _type;
  private String _outcomeName;
  private String _icon;
  private String _title;
  private String _visibility;

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtil.appendIndentString(appendToMe, level).append("<Tool name='").append(getName()).append('\'')
        .append(getAttributeAsXml("type", _type)).append(getAttributeAsXml("title", _title)).append(getAttributeAsXml("icon", _icon))
        .append(getAttributeAsXml("outcome", _outcomeName)).append(getAttributeAsXml("preCondition", getPreCondition()))
        .append(getAttributeAsXml("visibility", _visibility)).append("/>\n");
  }

  public void setType(String type)
  {
    _type = type;
  }

  public String getType()
  {
    return _type;
  }

  public void setOutcomeName(String outcomeName)
  {
    _outcomeName = outcomeName;
  }

  public String getOutcomeName()
  {
    return _outcomeName;
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

  public void setVisibility(String visibility)
  {
    _visibility = visibility;
  }

  public String getVisibility()
  {
    return _visibility;
  }
}
