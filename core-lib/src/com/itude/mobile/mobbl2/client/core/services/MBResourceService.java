package com.itude.mobile.mobbl2.client.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfiguration;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfigurationParser;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBStatedResourceDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBDocumentFactory;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBBundleNotFoundException;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBResourceNotDefinedException;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.DataUtil;
import com.itude.mobile.mobbl2.client.core.util.MBBundleDefinition;
import com.itude.mobile.mobbl2.client.core.util.MBCacheManager;

public class MBResourceService
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

  public byte[] getResourceByID(String resourceId)
  {
    MBResourceDefinition def = getConfig().getResourceWithID(resourceId);
    if (def == null) throw new MBResourceNotDefinedException("Resource for ID=" + resourceId + " could not be found");

    return getResourceByURL(def.getUrl(), def.getCacheable(), def.getTtl());
  }

  public int getDrawableIdentifier(String drawableName)
  {
    Resources resources = MBApplicationController.getInstance().getBaseContext().getResources();

    return resources.getIdentifier(drawableName, "drawable", MBApplicationController.getInstance().getBaseContext().getPackageName());
  }

  public Drawable getImageByID(String resourceId)
  {

    // First attempt to get the image from local resource
    MBResourceDefinition def = getConfig().getResourceWithID(resourceId);
    if (def == null)
    {
      throw new MBResourceNotDefinedException("Resource for ID=" + resourceId + " could not be found");
    }

    if (def instanceof MBStatedResourceDefinition)
    {
      return buildStatedImage((MBStatedResourceDefinition) def);
    }
    else
    {
      return getImage(resourceId, def);
    }
  }

  private Drawable getImage(String resourceId, MBResourceDefinition def)
  {
    // Only return an error if ways of getting the image fail
    Exception error = null;

    if (def.getUrl() != null && def.getUrl().startsWith("file://"))
    {
      String imageName = def.getUrl().substring(7);
      imageName = imageName.substring(0, imageName.indexOf(".")).toLowerCase();
      Resources resources = MBApplicationController.getInstance().getBaseContext().getResources();

      try
      {
        int identifier = resources.getIdentifier(imageName, "drawable", MBApplicationController.getInstance().getBaseContext()
            .getPackageName());
        return resources.getDrawable(identifier);
      }
      catch (Exception e)
      {
        error = e;
      }

    }

    // Now attempt to get the image from different sources
    byte[] bytes = getResourceByID(resourceId);
    if (bytes == null)
    {
      if (error != null)
      {
        Log.w(Constants.APPLICATION_NAME, "Warning: could not load file for resource=" + resourceId, error);
      }
      else

      {
        Log.w(Constants.APPLICATION_NAME, "Warning: could not load file for resource=" + resourceId);
      }
      return null;
    }

    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    if (bitmap == null)
    {
      Log.w(Constants.APPLICATION_NAME, "Could not create image for resource=" + resourceId);
    }

    Resources res = MBApplicationController.getInstance().getBaseContext().getResources();

    return new BitmapDrawable(res, bitmap);
  }

  private Drawable buildStatedImage(MBStatedResourceDefinition def)
  {
    Map<String, MBItemDefinition> items = def.getItems();

    if (items.size() == 0)
    {
      return null;
    }

    MBItemDefinition enabled = items.get("enabled");
    MBItemDefinition selected = items.get("selected");
    MBItemDefinition pressed = items.get("pressed");
    MBItemDefinition disabled = items.get("disabled");

    StateListDrawable stateDrawable = new StateListDrawable();

    if (pressed != null)
    {
      String resource = pressed.getResource();
      Drawable drawable = getImage(resource, getConfig().getResourceWithID(resource));
      stateDrawable.addState(new int[]{R.attr.state_pressed}, drawable);
    }

    if (enabled != null)
    {
      String resource = enabled.getResource();
      Drawable drawable = getImage(resource, getConfig().getResourceWithID(resource));
      stateDrawable.addState(new int[]{R.attr.state_enabled, -R.attr.state_selected}, drawable);
    }

    if (disabled != null)
    {
      String resource = disabled.getResource();
      Drawable drawable = getImage(resource, getConfig().getResourceWithID(resource));
      stateDrawable.addState(new int[]{-R.attr.state_enabled}, drawable);
    }

    if (selected != null)
    {
      String resource = selected.getResource();
      Drawable drawable = getImage(resource, getConfig().getResourceWithID(resource));
      stateDrawable.addState(new int[]{R.attr.state_selected}, drawable);
    }

    return stateDrawable;
  }

  public byte[] getResourceByURL(String urlString)
  {
    return getResourceByURL(urlString, false, 0);
  }

  public byte[] getResourceByURL(String urlString, boolean cacheable, int ttl)
  {
    boolean isPng = urlString.endsWith(".png");
    if (isPng && _pngCache.containsKey(urlString)) return _pngCache.get(urlString);

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

  public List<Map<String, String>> getBundlesForLanguageCode(String languageCode)
  {
    List<Map<String, String>> result = new ArrayList<Map<String, String>>();

    List<MBBundleDefinition> bundleDefs = _config.getBundlesForLanguageCode(languageCode);
    if (bundleDefs == null)
    {
      String message = "No bundles defined for language with code " + languageCode;
      throw new MBBundleNotFoundException(message);
    }

    for (MBBundleDefinition def : bundleDefs)
    {
      byte[] data = getResourceByURL(def.getUrl());

      if (data == null)
      {
        String message = "Bundle with url " + def.getUrl() + " could not be loaded";
        throw new MBBundleNotFoundException(message);
      }

      MBDocument bundleDoc = MBDocumentFactory.getInstance()
          .getDocumentWithData(data, MBDocumentFactory.PARSER_XML,
                               MBMetadataService.getInstance().getDefinitionForDocumentName(MBConfigurationDefinition.DOC_SYSTEM_LANGUAGE));
      Map<String, String> dict = new HashMap<String, String>();
      result.add(dict);
      for (MBElement text : (List<MBElement>) bundleDoc.getValueForPath("/Text"))
      {
        dict.put(text.getValueForAttribute("key"), text.getValueForAttribute("value"));
      }

    }

    return result;
  }
}
