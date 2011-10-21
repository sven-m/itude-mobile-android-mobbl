package com.itude.mobile.mobbl2.client.core.configuration.resources;

import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBStatedResourceDefinition extends MBAbstractResourceCollectionDefinition
{

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtilities.appendIndentString(appendToMe, level).append("<StatedResource name='").append(getName()).append("' >");
    for (MBItemDefinition item : getItems().values())
    {
      item.asXmlWithLevel(appendToMe, level + 2);
    }

    return StringUtilities.appendIndentString(appendToMe, level).append("</StatedResource>");
  }
}
