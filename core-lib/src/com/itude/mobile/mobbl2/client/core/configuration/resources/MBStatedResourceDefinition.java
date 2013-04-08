package com.itude.mobile.mobbl2.client.core.configuration.resources;

import com.itude.mobile.android.util.StringUtil;

public class MBStatedResourceDefinition extends MBAbstractResourceCollectionDefinition
{
  private String _type;

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<StatedResource id='").append(getResourceId()).append("' type='")
        .append(getType()).append("' >");
    for (MBItemDefinition item : getItems())
    {
      item.asXmlWithLevel(appendToMe, level + 2);
    }

    return StringUtil.appendIndentString(appendToMe, level).append("</StatedResource>");
  }

  public String getType()
  {
    return _type;
  }

  public void setType(String type)
  {
    _type = type;
  }
}
