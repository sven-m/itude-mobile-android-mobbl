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
package com.itude.mobile.mobbl.core.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;

import com.itude.mobile.mobbl.core.configuration.MBDefinition;

public abstract class MBCustomAttributeContainer
{
  private static final String                    BUNDLE_KEY = "custom";

  public static final MBCustomAttributeContainer EMPTY      = new MBCustomAttributeContainer()
                                                            {};

  private Map<String, String>                    _custom;

  protected MBCustomAttributeContainer()
  {
    _custom = Collections.emptyMap();
  }

  protected MBCustomAttributeContainer(MBCustomAttributeContainer copy)
  {
    _custom = cloneMap(copy._custom);
  }

  protected MBCustomAttributeContainer(MBDefinition definition)
  {
    _custom = cloneMap(definition.getCustom());
  }

  public String getCustom(String attribute)
  {
    return _custom.get(attribute);
  }

  public void setCustom(String attribute, String value)
  {
    if (_custom.isEmpty()) _custom = new HashMap<String, String>();
    _custom.put(attribute, value);
  }

  public boolean isHasCustomAttributes()
  {
    return !_custom.isEmpty();
  }

  protected String parseCustomValue(String value)
  {
    return value;
  }

  public void writeToBundle(Bundle bundle)
  {
    if (!_custom.isEmpty())
    {
      Bundle mapBundle = new Bundle();
      for (Map.Entry<String, String> entry : _custom.entrySet())
        mapBundle.putString(entry.getKey(), entry.getValue());

      bundle.putBundle(BUNDLE_KEY, bundle);
    }
  }

  public void readFromBundle(Bundle bundle)
  {
    Bundle mapBundle = bundle.getBundle(BUNDLE_KEY);
    if (mapBundle != null)
    {
      _custom = new HashMap<String, String>();
      for (String key : mapBundle.keySet())
        _custom.put(key, mapBundle.getString(key));
    }
    else _custom = Collections.emptyMap();
  }

  private static Map<String, String> cloneMap(Map<String, String> map)
  {
    if (map.isEmpty()) return Collections.emptyMap();
    else
    {
      Map<String, String> result = new HashMap<String, String>(map);
      return result;
    }
  }
}
