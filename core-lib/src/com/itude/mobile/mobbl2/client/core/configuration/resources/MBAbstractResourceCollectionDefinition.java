package com.itude.mobile.mobbl2.client.core.configuration.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.util.Constants;

public abstract class MBAbstractResourceCollectionDefinition extends MBResourceDefinition
{
  private Map<String, MBItemDefinition> _items;
  private List<MBItemDefinition>        _sortedItems;

  public MBAbstractResourceCollectionDefinition()
  {
    _items = new HashMap<String, MBItemDefinition>();
    _sortedItems = new ArrayList<MBItemDefinition>();
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
    _sortedItems.add(child);
  }

  public void setItems(Map<String, MBItemDefinition> items)
  {
    _items = items;
  }

  public Map<String, MBItemDefinition> getItems()
  {
    return _items;
  }

  public void setSortedItems(ArrayList<MBItemDefinition> sortedItems)
  {
    _sortedItems = sortedItems;
  }

  public List<MBItemDefinition> getSortedItems()
  {
    return _sortedItems;
  }

  @Override
  public String toString()
  {
    return asXmlWithLevel(new StringBuffer(), 0).toString();
  }
}
