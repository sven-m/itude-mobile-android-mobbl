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
package com.itude.mobile.mobbl2.client.core.configuration.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.MBIncludableDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBBundleDefinition;

public class MBResourceConfiguration extends MBDefinition implements MBIncludableDefinition
{
  private final Map<String, MBResourceDefinition> _resources;
  private final List<MBBundleDefinition>          _bundles;

  public MBResourceConfiguration()
  {
    _resources = new HashMap<String, MBResourceDefinition>();
    _bundles = new ArrayList<MBBundleDefinition>();
  }

  @Override
  public void addChildElement(MBResourceDefinition child)
  {
    addResource(child);
  }

  @Override
  public void addChildElement(MBBundleDefinition child)
  {
    addBundle(child);
  }

  public void addResource(MBResourceDefinition definition)
  {
    _resources.put(definition.getResourceId(), definition);
  }

  public void addBundle(MBBundleDefinition bundle)
  {
    _bundles.add(bundle);
  }

  public MBResourceDefinition getResourceWithID(String getResourceWithID)
  {
    return _resources.get(getResourceWithID);
  }

  public List<MBBundleDefinition> getBundlesForLanguageCode(String languageCode)
  {
    List<MBBundleDefinition> returnList = new ArrayList<MBBundleDefinition>();
    for (MBBundleDefinition def : _bundles)
    {
      if (def.getLanguageCode().equals(languageCode))
      {
        returnList.add(def);
      }
    }

    return returnList;
  }

  @Override
  public void addAll(MBIncludableDefinition otherDefinition)
  {
    MBResourceConfiguration otherConfig = null;

    if (otherDefinition instanceof MBResourceConfiguration)
    {
      otherConfig = (MBResourceConfiguration) otherDefinition;
    }

    for (MBResourceDefinition resourceDef : otherConfig.getResources().values())
    {
      addResource(resourceDef);
    }

    for (MBBundleDefinition bundleDef : otherConfig.getBundles())
    {
      addBundle(bundleDef);
    }
  }

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    StringUtil.appendIndentString(appendToMe, level).append("<Resources>\n");
    for (MBBundleDefinition bundle : _bundles)
    {
      bundle.asXmlWithLevel(appendToMe, level + 2);
    }
    for (MBResourceDefinition resource : _resources.values())
    {
      resource.asXmlWithLevel(appendToMe, level + 2);
    }

    StringUtil.appendIndentString(appendToMe, level).append("</Resources>");

    return appendToMe;
  }

  public Map<String, MBResourceDefinition> getResources()
  {
    return _resources;
  }

  public List<MBBundleDefinition> getBundles()
  {
    return _bundles;
  }
}
