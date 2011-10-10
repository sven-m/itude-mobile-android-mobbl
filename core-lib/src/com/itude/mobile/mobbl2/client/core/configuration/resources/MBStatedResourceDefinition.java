package com.itude.mobile.mobbl2.client.core.configuration.resources;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBStatedResourceDefinition extends MBResourceDefinition
{
  private Map<String, MBItemDefinition> _items;

  public MBStatedResourceDefinition()
  {
    _items = new HashMap<String, MBItemDefinition>();
  }

  @Override
  public void addChildElement(MBItemDefinition child)
  {
    addItem(child);
  }

  public void addItem(MBItemDefinition child)
  {
    if (_items.containsKey(child.getState()))
    {
      Log.w(Constants.APPLICATION_NAME, "Item definition overridden: multiple definitions with the same state for resource " + getName());
    }

    _items.put(child.getState(), child);
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtilities.appendIndentString(appendToMe, level).append("<StatedResource name='").append(getName()).append("' >");
    for (MBItemDefinition item : _items.values())
    {
      item.asXmlWithLevel(appendToMe, level + 2);
    }

    return StringUtilities.appendIndentString(appendToMe, level).append("</StatedResource>");
  }

  @Override
  public String toString()
  {
    return asXmlWithLevel(new StringBuffer(), 0).toString();
  }
}
