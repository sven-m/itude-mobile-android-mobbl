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
package com.itude.mobile.mobbl2.client.core.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.android.util.FileUtil;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBXmlDocumentParser;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;

public final class MBCacheManager
{
  public static final String        CACHE_DIR           = "cache";
  public static final String        CACHE_REGISTRY_FILE = "cache_registry.plist";
  public static final String        CACHE_TTL_FILE      = "cache_ttl.plist";

  private static MBCacheManager     _instance;
  private Map<String, String>       _registry;
  private Map<String, String>       _documentTypes;
  private Hashtable<String, String> _ttls;
  private Map<String, byte[]>       _temporaryMemoryCache;
  private final String              _registryFileName   = CACHE_DIR + File.separator + CACHE_REGISTRY_FILE;
  private final String              _ttlsFileName       = CACHE_DIR + File.separator + CACHE_TTL_FILE;

  @SuppressWarnings("unchecked")
  protected void init()
  {
    Log.d(Constants.APPLICATION_NAME, "init CacheManager");

    /* 
     * AD: When writing our ttls to a file we should also read them. 
     * Otherwise what will happen when closing and reopening the application is that the _ttls hashtable is empty
     * even though we've set values in this before closing down the application.
     * Not reading the _ttls from the file can result in the doGetValueForKey method not finding the ttl value for the given key
     * Not finding the ttl will disregard any ttl values that belong to the key. 
     * When disregarding the ttl the document will ALWAYS be loaded from the cache (if available).
     */
    _ttls = (Hashtable<String, String>) FileUtil.getInstance().readObjectFromFile(_ttlsFileName);
    if (_ttls == null)
    {
      _ttls = new Hashtable<String, String>();
    }

    _documentTypes = new Hashtable<String, String>();
    _registry = new HashMap<String, String>();
    _temporaryMemoryCache = new HashMap<String, byte[]>();
  }

  private MBCacheManager()
  {
    init();

    @SuppressWarnings("unchecked")
    Hashtable<String, String> combined = (Hashtable<String, String>) FileUtil.getInstance().readObjectFromFile(_registryFileName);
    if (combined == null)
    {
      combined = new Hashtable<String, String>();
    }

    for (String key : combined.keySet())
    {
      String value = combined.get(key);
      String[] split = value.split(":");

      _registry.put(key, split[0]);
      if (split.length > 1)
      {
        _documentTypes.put(key, split[1]);
      }
    }
  }

  public static MBCacheManager getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBCacheManager();
    }

    return _instance;
  }

  private byte[] doGetValueForKey(String key)
  {

    String fileName = null;
    synchronized (_registry)
    {
      fileName = _registry.get(key);

      // check ttl
      if (fileName != null)
      {
        String ttl = _ttls.get(key);
        if (StringUtil.isNotBlank(ttl))
        {
          long maxAge = Long.parseLong(ttl);
          if (maxAge != 0 && maxAge < System.currentTimeMillis())
          {
            fileName = null;
            doExpireDataForKey(key);
          }
        }
      }
    }
    if (fileName == null)
    {
      return null;
    }
    // First try to get it from the temporary memory cache; a writer could be busy writing the file right now:

    byte[] data = null;
    synchronized (_temporaryMemoryCache)
    {
      data = _temporaryMemoryCache.get(key);
    }

    if (data == null)
    {
      data = FileUtil.getInstance().getByteArray(CACHE_DIR + File.separator + fileName);
    }

    return data;
  }

  private void doSetValue(byte[] data, String key, int ttl)
  {
    // Put the data in the temporary memory cache; to avoid reading a file a writer is not yet done with:
    synchronized (_temporaryMemoryCache)
    {
      _temporaryMemoryCache.put(key, data);
    }

    String fileName = null;
    synchronized (_registry)
    {
      fileName = _registry.get(key);
      if (fileName == null)
      {
        int maxKey = 0;
        for (String value : _registry.values())
        {
          maxKey = Math.max(maxKey, Integer.parseInt(value));
        }
        maxKey++;
        fileName = "" + maxKey;
        _registry.put(key, fileName);
      }

      // Set maximum age based on ttl and the time of 'now':
      long maxAge = 0;
      if (ttl > 0)
      {
        maxAge = System.currentTimeMillis() + ttl;
      }
      _ttls.put(key, "" + maxAge);

      MBCacheWriter writer = new MBCacheWriter(_registry, _registryFileName, _documentTypes, _ttls, _ttlsFileName, CACHE_DIR
                                                                                                                   + File.separator
                                                                                                                   + fileName, data,
          _temporaryMemoryCache, key);
      writer.run();
    }
  }

  private void flushRegistry()
  {
    MBCacheWriter writer = new MBCacheWriter(_registry, _registryFileName, _documentTypes, _ttls, _ttlsFileName, null, null,
        _temporaryMemoryCache, null);
    writer.start();
  }

  private void deleteCachedFile(String key)
  {
    synchronized (_registry)
    {
      String fileName = _registry.get(key);
      _registry.remove(key);
      _ttls.remove(key);
      _temporaryMemoryCache.remove(key);
      _documentTypes.remove(key);

      FileUtil.getInstance().remove(CACHE_DIR + File.separator + fileName);
    }
  }

  private void doExpireDataForKey(String key)
  {
    Log.d(Constants.APPLICATION_NAME, "removing " + key + "from cache");
    deleteCachedFile(key);
    flushRegistry();
  }

  private void doExpireAllDocuments()
  {

    Set<String> keySet = new HashSet<String>(_registry.keySet());

    boolean doneOne = false;
    for (String key : keySet)
    {
      int indexOfColon = key.indexOf(":");
      if (indexOfColon != -1)
      {
        String documentName = key.substring(0, indexOfColon);

        // Is it a valid document? If so delete the entry
        if (MBMetadataService.getInstance().getDefinitionForDocumentName(documentName, false) != null)
        {
          deleteCachedFile(key);
          doneOne = true;
        }
      }
    }
    if (doneOne) flushRegistry();
  }

  private MBDocument doGetDocumentForKey(String key)
  {
    byte[] zipped = doGetValueForKey(key);
    if (zipped == null) return null;

    byte[] data = DataUtil.getInstance().decompress(zipped);
    if (data == null)
    {
      Log.e(Constants.APPLICATION_NAME, "Error decompressing cached document");
      return null;
    }

    String documentName = _documentTypes.get(key);
    MBDocumentDefinition def = MBMetadataService.getInstance().getDefinitionForDocumentName(documentName);
    return MBXmlDocumentParser.getDocumentWithData(data, def);
  }

  private void doSetDocument(MBDocument document, String key, int ttl)
  {
    String docType = document.getDocumentName();
    _documentTypes.put(key, docType);
    try
    {
      StringBuffer sb = new StringBuffer(4096);
      byte[] data = document.asXmlWithLevel(sb, 0, true).toString().getBytes(Constants.C_ENCODING);
      byte[] zipped = DataUtil.getInstance().compress(data);

      doSetValue(zipped, key, ttl);
    }
    catch (UnsupportedEncodingException e)
    {
      Log.w(Constants.APPLICATION_NAME, e);
    }
  }

  public static byte[] getDataForKey(String key)
  {
    return getInstance().doGetValueForKey(key);
  }

  public static void setData(byte[] data, String key, int ttl)
  {
    getInstance().doSetValue(data, key, ttl);
  }

  public static void expireDataForKey(String key)
  {
    getInstance().doExpireDataForKey(key);
  }

  public static void expireDocumentForKey(String key)
  {
    getInstance().doExpireDataForKey(key);
  }

  public static void expireAllDocuments()
  {
    getInstance().doExpireAllDocuments();
  }

  public static MBDocument documentForKey(String key)
  {
    return getInstance().doGetDocumentForKey(key);
  }

  public static void setDocument(MBDocument document, String key, int ttl)
  {
    getInstance().doSetDocument(document, key, ttl);
  }

}
