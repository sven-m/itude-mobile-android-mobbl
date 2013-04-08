package com.itude.mobile.mobbl2.client.core.configuration.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.itude.mobile.android.util.StringUtil;

public class MBLayeredResourceDefinition extends MBAbstractResourceCollectionDefinition
{
  /**
   * @see com.itude.mobile.mobbl2.client.core.configuration.resources.MBAbstractResourceCollectionDefinition#getSortedItems()
   * @return a reversed list of item definitions
   */
  @Override
  public List<MBItemDefinition> getItems()
  {
    List<MBItemDefinition> items = super.getItems();

    if (items == null)
    {
      return null;
    }

    ArrayList<MBItemDefinition> tmpList = new ArrayList<MBItemDefinition>(items);
    Collections.reverse(tmpList);

    return tmpList;
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<LayeredResource name='").append(getName()).append("' >");
    for (MBItemDefinition item : getItems())
    {
      item.asXmlWithLevel(appendToMe, level + 2);
    }

    return StringUtil.appendIndentString(appendToMe, level).append("</LayeredResource>");
  }
}
