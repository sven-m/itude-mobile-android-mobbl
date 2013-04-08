package com.itude.mobile.mobbl2.client.core.configuration.resources;

import java.util.ArrayList;
import java.util.List;

public abstract class MBAbstractResourceCollectionDefinition extends MBResourceDefinition
{
  private List<MBItemDefinition> _items;

  public MBAbstractResourceCollectionDefinition()
  {
    _items = new ArrayList<MBItemDefinition>();
  }

  @Override
  public void addChildElement(MBItemDefinition child)
  {
    addItem(child);
  }

  public void addItem(MBItemDefinition child)
  {
    _items.add(child);
  }

  public void setItems(List<MBItemDefinition> items)
  {
    _items = items;
  }

  public List<MBItemDefinition> getItems()
  {
    return _items;
  }

  @Override
  public String toString()
  {
    return asXmlWithLevel(new StringBuffer(), 0).toString();
  }
}
