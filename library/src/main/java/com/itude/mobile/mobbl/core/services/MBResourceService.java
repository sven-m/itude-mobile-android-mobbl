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
package com.itude.mobile.mobbl.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.configuration.mvc.MBBundleDefinition;
import com.itude.mobile.mobbl.core.configuration.resources.MBResourceConfiguration;
import com.itude.mobile.mobbl.core.configuration.resources.MBResourceConfigurationParser;
import com.itude.mobile.mobbl.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.services.exceptions.MBBundleNotFoundException;
import com.itude.mobile.mobbl.core.services.exceptions.MBResourceNotDefinedException;
import com.itude.mobile.mobbl.core.util.MBConstants;
import com.itude.mobile.mobbl.core.util.MBCacheManager;
import com.itude.mobile.mobbl.core.util.resources.MBBundleBuilder;
import com.itude.mobile.mobbl.core.util.resources.MBResourceBuilder;
import com.itude.mobile.mobbl.core.util.resources.MBResourceBuilderFactory;
import com.itude.mobile.mobbl.core.view.MBBundle;
import com.itude.mobile.mobbl.core.view.MBComponentFactory;
import com.itude.mobile.mobbl.core.view.MBResource;

/**
 * Service to handle resources
 */
public final class MBResourceService
{

  public static final String        RESOURCE_CONFIG_FILE_NAME = "resources.xml";

  private MBResourceConfiguration   _config;
  private static MBResourceService  _instance;
  private final Map<String, byte[]> _pngCache                 = new HashMap<String, byte[]>();

  private MBResourceService()
  {

  }

  public static MBResourceService getInstance()
  {
    // double-check to prevent unneeded synchronization
    if (_instance == null)
    {
      synchronized (MBResourceService.class)
      {
        if (_instance == null)
        {
          _instance = new MBResourceService();

          MBResourceConfigurationParser parser = new MBResourceConfigurationParser();

          byte[] data = DataUtil.getInstance().readFromAssetOrFile(RESOURCE_CONFIG_FILE_NAME);
          _instance.setConfig((MBResourceConfiguration) parser.parseData(data, RESOURCE_CONFIG_FILE_NAME));
        }
      }

    }

    return _instance;
  }

  public MBResourceConfiguration getConfig()
  {
    return _config;
  }

  public void setConfig(MBResourceConfiguration config)
  {
    _config = config;
  }

  public MBResource getResource(String resourceId)
  {
    MBResourceDefinition def = getConfig().getResourceWithID(resourceId);
    if (def == null)
    {
      throw new MBResourceNotDefinedException("Resource for ID=" + resourceId + " could not be found");
    }

    return MBComponentFactory.getComponentFromDefinition(def, null, null);
  }

  public int getDrawableIdentifier(String drawableName)
  {
    Context baseContext = MBApplicationController.getInstance().getBaseContext();
    Resources resources = baseContext.getResources();

    return resources.getIdentifier(drawableName, "drawable", baseContext.getPackageName());
  }

  public Drawable getImageByID(String resourceId)
  {
    MBResource resource = getResource(resourceId);

    return getImage(resource);
  }

  public Drawable getImage(MBResource resource)
  {
    MBResourceBuilder builder = MBResourceBuilderFactory.getInstance().getResourceBuilder();
    return builder.buildResource(resource);
  }

  public int getColor(String resourceId)
  {
    MBResource resource = getResource(resourceId);

    MBResourceBuilder builder = MBResourceBuilderFactory.getInstance().getResourceBuilder();
    return (Integer) builder.buildResource(resource);
  }

  public ColorStateList getColorStateListById(String resourceId)
  {
    MBResource resource = getResource(resourceId);

    MBResourceBuilder builder = MBResourceBuilderFactory.getInstance().getResourceBuilder();
    return builder.buildResource(resource);
  }

  public Drawable getImageByURL(String url)
  {
    MBResourceBuilder builder = MBResourceBuilderFactory.getInstance().getResourceBuilder();

    MBResource resource = new MBResource(null, null, null);
    resource.setType("REMOTE_IMAGE");
    resource.setUrl(url);

    return builder.buildResource(resource);
  }

  public byte[] getResourceByURL(MBResource resource)
  {
    return getResourceByURL(resource.getUrl(), false, 0);
  }

  public List<byte[]> getResourceByURL(MBBundle bundle)
  {
    ArrayList<byte[]> bundleBytes = new ArrayList<byte[]>();

    List<String> urls = bundle.getUrls();

    for (String url : urls)
    {
      byte[] bytes = getResourceByURL(url, false, 0);

      if (bytes == null)
      {
        String message = "Bundle with url " + url + " could not be loaded";
        throw new MBBundleNotFoundException(message);
      }

      bundleBytes.add(bytes);
    }

    return bundleBytes;
  }

  public byte[] getResourceByURL(String urlString, boolean cacheable, int ttl)
  {
    boolean isPng = urlString.endsWith(".png");
    if (isPng && _pngCache.containsKey(urlString))
    {
      return _pngCache.get(urlString);
    }

    if (urlString.startsWith("file://"))
    {
      String fileName = urlString.substring(7);
      byte[] data;

      try
      {
        data = DataUtil.getInstance().readFromAssetOrFile(fileName);

        if (data == null)
        {
          MBLog.w(MBConstants.APPLICATION_NAME, "Warning: could not load file=" + fileName + " based on URL=" + urlString);
        }
        if (isPng)
        {
          MBLog.i(MBConstants.APPLICATION_NAME, "png placed in cache: " + urlString);
          _pngCache.put(urlString, data);
        }

        return data;
      }
      catch (Exception e)
      {
        MBLog.w(MBConstants.APPLICATION_NAME, "Warning: could not load file=" + fileName + " based on URL=" + urlString);
      }
    }

    if (cacheable)
    {
      byte[] data = MBCacheManager.getDataForKey(urlString);
      if (data != null)
      {
        return data;
      }
    }

    return null;
  }

  public MBBundle getBundle(String languageCode)
  {
    List<MBBundleDefinition> bundleDefs = getConfig().getBundlesForLanguageCode(languageCode);

    if (bundleDefs == null || bundleDefs.isEmpty())
    {
      String message = "No bundles defined for language with code " + languageCode;
      throw new MBBundleNotFoundException(message);
    }

    MBBundleDefinition firstBundleDef = bundleDefs.remove(0);
    MBBundle bundle = MBComponentFactory.getComponentFromDefinition(firstBundleDef, null, null);

    for (MBBundleDefinition def : bundleDefs)
    {
      bundle.addUrl(def.getUrl());
    }

    MBBundleBuilder builder = MBResourceBuilderFactory.getInstance().getBundleResourceBuilder();

    return builder.buildBundle(bundle);
  }

  public String getUrlById(String resourceId)
  {
    MBResourceDefinition resDef = getConfig().getResourceWithID(resourceId);
    if (resDef != null)
    {
      return resDef.getUrl();
    }

    MBLog.w(MBConstants.APPLICATION_NAME, "No resource found for id " + resourceId);

    return null;
  }
}
