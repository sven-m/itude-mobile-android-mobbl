package com.itude.mobile.mobbl2.client.core.configuration.resources;

import com.itude.mobile.android.util.StringUtil;


public class MBStatedResourceDefinition extends MBAbstractResourceCollectionDefinition
{

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<StatedResource name='").append(getResourceId()).append("' >");
    for (MBItemDefinition item : getItems().values())
    {
      item.asXmlWithLevel(appendToMe, level + 2);
    }

    return StringUtil.appendIndentString(appendToMe, level).append("</StatedResource>");
  }
}
