/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl.core.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.mobbl.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;

public class MBResource extends MBComponentContainer
{
  private String              _id;
  private String              _type;
  private String              _viewType;
  private String              _url;
  private String              _align;
  private String              _color;

  private Map<String, MBItem> _items;
  private List<MBItem>        _sortedItems;
  private List<MBItem>        _sortedItemsReversed;

  public MBResource(MBResourceDefinition definition, MBDocument document, MBComponentContainer parent)
  {
    super(definition, null, null);

    _id = definition.getResourceId();
    _type = definition.getType();
    _viewType = definition.getViewType();
    _url = definition.getUrl();
    _align = definition.getAlign();
    _color = definition.getColor();

    if (definition.hasItems())
    {
      List<MBItemDefinition> sortedItems = definition.getSortedItems();

      int collectionSize = sortedItems.size();

      HashMap<String, MBItem> itemsTmp = new HashMap<String, MBItem>(collectionSize);
      List<MBItem> sortedItemsTmp = new ArrayList<MBItem>(collectionSize);

      for (MBItemDefinition itemDef : sortedItems)
      {
        MBItem item = MBComponentFactory.getComponentFromDefinition(itemDef, null, this);

        sortedItemsTmp.add(item);
        itemsTmp.put(item.getState(), item);
      }

      _items = Collections.unmodifiableMap(itemsTmp);
      _sortedItems = Collections.unmodifiableList(sortedItemsTmp);
    }
  }

  public String getId()
  {
    return _id;
  }

  public void setId(String id)
  {
    _id = id;
  }

  public String getUrl()
  {
    return _url;
  }

  public void setUrl(String url)
  {
    _url = url;
  }

  public String getAlign()
  {
    return _align;
  }

  public void setAlign(String align)
  {
    _align = align;
  }

  @Override
  public String getType()
  {
    return _type;
  }

  public void setType(String type)
  {
    _type = type;
  }

  public String getColor()
  {
    return _color;
  }

  public void setColor(String color)
  {
    _color = color;
  }

  public Map<String, MBItem> getItems()
  {
    return _items;
  }

  public void setItems(Map<String, MBItem> items)
  {
    _items = items;
  }

  public List<MBItem> getSortedItems()
  {
    return _sortedItems;
  }

  public void setSortedItems(List<MBItem> sortedItems)
  {
    _sortedItems = sortedItems;
    _sortedItemsReversed = null;
  }

  public List<MBItem> getSortedItemsReversed()
  {
    if (_sortedItemsReversed == null)
    {
      List<MBItem> sortedItems = new ArrayList<MBItem>(_sortedItems);
      Collections.reverse(sortedItems);
      _sortedItemsReversed = Collections.unmodifiableList(sortedItems);
    }

    return _sortedItemsReversed;
  }

  public String getViewType()
  {
    return _viewType;
  }

  public void setViewType(String viewType)
  {
    _viewType = viewType;
  }
}
