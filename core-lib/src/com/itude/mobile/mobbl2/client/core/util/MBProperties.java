package com.itude.mobile.mobbl2.client.core.util;

import java.util.Hashtable;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;

public final class MBProperties
{
  private final MBDocument          _propertiesDoc;
  private final Map<String, String> _propertiesCache;
  private final Map<String, String> _systemPropertiesCache;

  private static MBProperties       _instance;

  private MBProperties()
  {
    _propertiesDoc = MBDataManagerService.getInstance().loadDocument(MBConfigurationDefinition.DOC_SYSTEM_PROPERTIES);
    _propertiesCache = new Hashtable<String, String>();
    _systemPropertiesCache = new Hashtable<String, String>();

  }

  public static MBProperties getInstance()
  {
    if (_instance == null) _instance = new MBProperties();
    return _instance;
  }

  public String getValueForProperty(String key)
  {
    String value = _propertiesCache.get(key);
    if (value == null)
    {
      String path = "/Application[0]/Property[name='" + key + "']/@value";
      value = (String) _propertiesDoc.getValueForPath(path);
      if (value != null) _propertiesCache.put(key, value);
    }
    return value;
  }

  public String getValueForSystemProperty(String key)
  {
    String value = _systemPropertiesCache.get(key);
    if (value == null)
    {
      String path = "/System[0]/Property[name='" + key + "']/@value";
      value = (String) _propertiesDoc.getValueForPath(path);
      _systemPropertiesCache.put(key, value);
    }
    return value;
  }

}
