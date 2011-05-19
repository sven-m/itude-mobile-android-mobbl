package com.itude.mobile.mobbl2.client.core.configuration.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.MBBundleDefinition;

public class MBResourceConfiguration extends MBDefinition
{
  private final Map<String, MBResourceDefinition> _resources;
  private final List<MBBundleDefinition>          _bundles;

  public MBResourceConfiguration()
  {
    _resources = new HashMap<String, MBResourceDefinition>();
    _bundles = new ArrayList<MBBundleDefinition>();
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

}
