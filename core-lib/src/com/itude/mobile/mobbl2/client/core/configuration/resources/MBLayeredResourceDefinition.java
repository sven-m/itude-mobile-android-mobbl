package com.itude.mobile.mobbl2.client.core.configuration.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBLayeredResourceDefinition extends MBAbstractResourceCollectionDefinition
{
  /**
   * @see com.itude.mobile.mobbl2.client.core.configuration.resources.MBAbstractResourceCollectionDefinition#getSortedItems()
   * @return a reversed list of item definitions
   */
  @Override
  public List<MBItemDefinition> getSortedItems()
  {
    List<MBItemDefinition> sortedItems = super.getSortedItems();

    if (sortedItems == null)
    {
      return null;
    }

    ArrayList<MBItemDefinition> tmpList = new ArrayList<MBItemDefinition>(sortedItems);
    Collections.reverse(tmpList);

    return tmpList;
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtilities.appendIndentString(appendToMe, level).append("<LayeredResource name='").append(getName()).append("' >");
    for (MBItemDefinition item : getItems().values())
    {
      item.asXmlWithLevel(appendToMe, level + 2);
    }

    return StringUtilities.appendIndentString(appendToMe, level).append("</LayeredResource>");
  }
}
