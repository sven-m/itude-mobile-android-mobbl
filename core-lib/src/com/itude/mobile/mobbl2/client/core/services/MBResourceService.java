package com.itude.mobile.mobbl2.client.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBBundleDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfiguration;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfigurationParser;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBBundleNotFoundException;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBResourceNotDefinedException;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBCacheManager;
import com.itude.mobile.mobbl2.client.core.util.resources.MBBundleBuilder;
import com.itude.mobile.mobbl2.client.core.util.resources.MBRemoteImageResource;
import com.itude.mobile.mobbl2.client.core.util.resources.MBResourceBuilder;
import com.itude.mobile.mobbl2.client.core.util.resources.MBResourceBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.MBBundle;
import com.itude.mobile.mobbl2.client.core.view.MBComponentFactory;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

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

  public <T extends MBResource> T getResource(String resourceId)
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

  public ColorStateList getColorStateListById(String resourceId)
  {
    //      MBResourceDefinition def = getResource(resourceId);
    //  
    //      MBAbstractStatedResourceBuilder builder = MBStatedResourceBuilderFactory.getInstance().getStatedResourceBuilder("Color", getConfig());
    //      ColorStateList result = builder.build((MBStatedResourceDefinition) def);
    //  
    //      return result;

    return null;

  }

  public Drawable getImageByURL(String url)
  {
    MBResourceBuilder builder = MBResourceBuilderFactory.getInstance().getResourceBuilder();

    MBResource resource = new MBRemoteImageResource(url);

    return builder.buildResource(resource);
  }

  //  private Drawable buildLayeredImage(MBLayeredResourceDefinition def)
  //  {
  //    List<MBItemDefinition> items = def.getItems();
  //
  //    if (items.size() == 0)
  //    {
  //      return null;
  //    }
  //
  //    Drawable[] layers = new Drawable[items.size()];
  //
  //    for (MBItemDefinition item : items)
  //    {
  //      String resource = item.getResource();
  //
  //      // FIXME: CH: don't know the purpose of this validation
  //      //      validateItemInLayeredResource(resource);
  //
  //      Drawable drawable = getImageByID(resource);
  //      layers[items.indexOf(item)] = drawable;
  //    }
  //
  //    LayerDrawable layerDrawable = new LayerDrawable(layers);
  //    return layerDrawable;
  //  }

  //  private void validateItemInLayeredResource(String itemResource) throws MBInvalidItemException
  //  {
  //    MBResourceDefinition resourceDef = getConfig().getResourceWithID(itemResource);
  //
  //    if (resourceDef instanceof MBStatedResourceDefinition)
  //    {
  //      throw new MBInvalidItemException("Layered resources can not contain stated resources");
  //    }
  //  }

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
          Log.w(Constants.APPLICATION_NAME, "Warning: could not load file=" + fileName + " based on URL=" + urlString);
        }
        if (isPng)
        {
          Log.i(Constants.APPLICATION_NAME, "png placed in cache: " + urlString);
          _pngCache.put(urlString, data);
        }

        return data;
      }
      catch (Exception e)
      {
        Log.w(Constants.APPLICATION_NAME, "Warning: could not load file=" + fileName + " based on URL=" + urlString);
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

    if (bundleDefs == null)
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

    Log.w(Constants.APPLICATION_NAME, "No resource found for id " + resourceId);

    return null;
  }
}
