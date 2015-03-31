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
package com.itude.mobile.mobbl.core.configuration.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.configuration.MBDefinition;
import com.itude.mobile.mobbl.core.util.MBConstants;

/**
 * {@link MBDefinition} Class for a resource file
 *
 */
public class MBResourceDefinition extends MBDefinition
{
  private String                        _resourceId;
  private String                        _type;
  private String                        _url;
  private String                        _color;
  private String                        _align;
  private String                        _languageCode;
  private String                        _viewType;
  private boolean                       _cacheable;
  private int                           _ttl;

  private Map<String, MBItemDefinition> _items;
  private List<MBItemDefinition>        _sortedItems;

  public MBResourceDefinition()
  {
    _items = new HashMap<String, MBItemDefinition>();
    _sortedItems = new ArrayList<MBItemDefinition>();
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtil.appendIndentString(appendToMe, level).append("<Resource id='").append(getResourceId()).append("' type='")
        .append(getType()).append("' url='").append(getUrl()).append("' cacheable='").append(getCacheable()).append("' ttl='")
        .append(getTtl()).append("' color='").append(getColor()).append("' align='").append(getAlign()).append("' />");
  }

  @Override
  public void addChildElement(MBItemDefinition child)
  {
    addItem(child);
  }

  public void addItem(MBItemDefinition child)
  {
    String state = child.getState();
    if (StringUtil.isNotBlank(state) && _items.containsKey(state))
    {
      MBLog.w(MBConstants.APPLICATION_NAME, "Item definition overridden: multiple definitions with the same state for resource " + getName());
    }

    _items.put(child.getState(), child);
    _sortedItems.add(child);
  }

  public String getResourceId()
  {
    return _resourceId;
  }

  public void setResourceId(String resourceId)
  {
    _resourceId = resourceId;
  }

  public String getUrl()
  {
    return _url;
  }

  public void setUrl(String url)
  {
    _url = url;
  }

  public boolean getCacheable()
  {
    return _cacheable;
  }

  public void setCacheable(boolean cacheable)
  {
    _cacheable = cacheable;
  }

  public int getTtl()
  {
    return _ttl;
  }

  public void setTtl(int ttl)
  {
    _ttl = ttl;
  }

  public String getColor()
  {
    return _color;
  }

  public void setColor(String color)
  {
    _color = color;
  }

  public String getAlign()
  {
    return _align;
  }

  public void setAlign(String align)
  {
    _align = align;
  }

  public String getType()
  {
    return _type;
  }

  public void setType(String type)
  {
    _type = type;
  }

  public String getLanguageCode()
  {
    return _languageCode;
  }

  public void setLanguageCode(String languageCode)
  {
    _languageCode = languageCode;
  }

  public String getViewType()
  {
    return _viewType;
  }

  public void setViewType(String viewType)
  {
    _viewType = viewType;
  }

  public boolean hasItems()
  {
    return !_items.isEmpty();
  }

  public Map<String, MBItemDefinition> getItems()
  {
    return _items;
  }

  public void setItems(Map<String, MBItemDefinition> items)
  {
    _items = items;
  }

  public List<MBItemDefinition> getSortedItems()
  {
    return _sortedItems;
  }

  public void setSortedItems(List<MBItemDefinition> sortedItems)
  {
    _sortedItems = sortedItems;
  }

}
