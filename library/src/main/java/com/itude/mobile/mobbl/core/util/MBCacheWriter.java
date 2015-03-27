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

import java.util.Hashtable;
import java.util.Map;

import com.itude.mobile.android.util.FileUtil;
import com.itude.mobile.android.util.log.MBLog;

public class MBCacheWriter extends Thread
{
  private Map<String, String>       _registry;
  private Map<String, String>       _documentTypes;
  private Hashtable<String, String> _ttls;
  private String                    _registryFileName;
  private String                    _ttlsFileName;
  private String                    _fileName;
  private byte[]                    _data;
  private Map<String, byte[]>       _temporaryMemoryCache;
  private String                    _key;

  public MBCacheWriter(Map<String, String> registry, String registryFileName, Map<String, String> documentTypes,
                       Hashtable<String, String> ttls, String ttlsFileName, String fileName, byte[] data,
                       Map<String, byte[]> temporaryMemoryCache, String key)
  {
    setRegistry(registry);
    setRegistryFileName(registryFileName);
    setDocumentTypes(documentTypes);
    setTtls(ttls);
    setTtlsFileName(ttlsFileName);
    setFileName(fileName);
    setData(data);
    setTemporaryMemoryCache(temporaryMemoryCache);
    setKey(key);
  }

  @Override
  public void run()
  {
    try
    {
      synchronized (getRegistry())
      {

        Hashtable<String, String> combined = new Hashtable<String, String>();

        for (String key : getRegistry().keySet())
        {
          String value = getRegistry().get(key);
          String docType = getDocumentTypes().get(key);

          if (docType != null) value = value + ":" + docType;
          combined.put(key, value);
        }

        boolean success = FileUtil.getInstance().writeObjectToFile(getTtls(), getTtlsFileName());
        success &= FileUtil.getInstance().writeObjectToFile(combined, getRegistryFileName());

        if (success && getData() != null)
        {
          success = FileUtil.getInstance().writeToFile(getData(), getFileName(), null);
          if (!success) MBLog.e(MBConstants.APPLICATION_NAME, "Error caching data in " + getFileName());
        }
        else if (!success) MBLog.w(MBConstants.APPLICATION_NAME, "Could not store the cache registry info in " + getRegistryFileName()
                                                               + " and/or " + getTtlsFileName() + " skipping writing to the cache!");
      }

    }
    finally
    {
      Map<String, byte[]> temporaryMemoryCache = getTemporaryMemoryCache();
      synchronized (temporaryMemoryCache)
      {
        if (getKey() != null) temporaryMemoryCache.remove(getKey());
      }
    }
  }

  ////////////////////////////

  public Map<String, String> getRegistry()
  {
    return _registry;
  }

  public void setRegistry(Map<String, String> registry)
  {
    _registry = registry;
  }

  public Map<String, String> getDocumentTypes()
  {
    return _documentTypes;
  }

  public void setDocumentTypes(Map<String, String> documentTypes)
  {
    _documentTypes = documentTypes;
  }

  public String getRegistryFileName()
  {
    return _registryFileName;
  }

  public void setRegistryFileName(String registryFileName)
  {
    _registryFileName = registryFileName;
  }

  public Hashtable<String, String> getTtls()
  {
    return _ttls;
  }

  public void setTtls(Hashtable<String, String> ttls)
  {
    _ttls = ttls;
  }

  public String getTtlsFileName()
  {
    return _ttlsFileName;
  }

  public void setTtlsFileName(String ttlsFileName)
  {
    _ttlsFileName = ttlsFileName;
  }

  public String getFileName()
  {
    return _fileName;
  }

  public void setFileName(String fileName)
  {
    _fileName = fileName;
  }

  public byte[] getData()
  {
    return _data;
  }

  public void setData(byte[] data)
  {
    if (data != null)
    {
      _data = data.clone();
    }
  }

  public Map<String, byte[]> getTemporaryMemoryCache()
  {
    return _temporaryMemoryCache;
  }

  public void setTemporaryMemoryCache(Map<String, byte[]> temporaryMemoryCache)
  {
    _temporaryMemoryCache = temporaryMemoryCache;
  }

  public String getKey()
  {
    return _key;
  }

  public void setKey(String key)
  {
    _key = key;
  }
}
