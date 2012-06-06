package com.itude.mobile.mobbl2.client.core.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.Gravity;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBLayeredResourceDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfiguration;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfigurationParser;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBStatedResourceDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.exceptions.MBInvalidItemException;
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

  public byte[] getResourceByID(String resourceId)
  {
    MBResourceDefinition def = getConfig().getResourceWithID(resourceId);
    if (def == null) throw new MBResourceNotDefinedException("Resource for ID=" + resourceId + " could not be found");

    return getResourceByURL(def.getUrl(), def.getCacheable(), def.getTtl());
  }

  public int getDrawableIdentifier(String drawableName)
  {
    Context baseContext = MBApplicationController.getInstance().getBaseContext();
    Resources resources = baseContext.getResources();

    return resources.getIdentifier(drawableName, "drawable", baseContext.getPackageName());
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
    else if (def instanceof MBLayeredResourceDefinition)
    {
      return buildLayeredImage((MBLayeredResourceDefinition) def);
    }
    else
    {
      return getImage(resourceId, def);
    }
  }

  public Drawable getImageByURL(String url)
  {
    Drawable image;
    try
    {
      image = Drawable.createFromStream((InputStream) new URL(url).getContent(), "src");
    }
    catch (MalformedURLException e)
    {
      Log.e(Constants.APPLICATION_NAME, "Not a correct img source: " + e.getMessage());
      image = MBResourceService.getInstance().getImageByID(Constants.C_ICON_TRANSPARENT);
    }
    catch (IOException e)
    {
      Log.e(Constants.APPLICATION_NAME, "Could not read img: " + e.getMessage());
      image = MBResourceService.getInstance().getImageByID(Constants.C_ICON_TRANSPARENT);
    }

    return image;
  }

  private Drawable getImage(String resourceId, MBResourceDefinition def)
  {
    // Only return an error if ways of getting the image fail
    Exception error = null;

    if (def.getUrl() != null && def.getUrl().startsWith("file://"))
    {

      Context baseContext = MBApplicationController.getInstance().getBaseContext();

      String imageName = def.getUrl().substring(7);
      imageName = imageName.substring(0, imageName.indexOf(".")).toLowerCase();

      Resources resources = baseContext.getResources();

      try
      {
        int identifier = resources.getIdentifier(imageName, "drawable", baseContext.getPackageName());
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
    MBItemDefinition checked = items.get("checked");

    StateListDrawable stateDrawable = new StateListDrawable();

    if (pressed != null)
    {
      String resource = pressed.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{R.attr.state_pressed}, drawable);
    }

    if (enabled != null)
    {
      String resource = enabled.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{R.attr.state_enabled, -R.attr.state_selected}, drawable);
    }

    if (disabled != null)
    {
      String resource = disabled.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{-R.attr.state_enabled}, drawable);
    }

    if (selected != null)
    {
      String resource = selected.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{R.attr.state_selected}, drawable);
    }
    if (checked != null)
    {
      String resource = checked.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{R.attr.state_checked}, drawable);
    }

    return stateDrawable;
  }

  private Drawable buildLayeredImage(MBLayeredResourceDefinition def)
  {
    List<MBItemDefinition> items = def.getSortedItems();

    if (items.size() == 0)
    {
      return null;
    }

    Drawable[] layers = new Drawable[items.size()];

    for (MBItemDefinition item : items)
    {
      String resource = item.getResource();
      validateItemInLayeredResource(resource);

      Drawable drawable = getImageByID(resource);
      if (drawable instanceof BitmapDrawable)
      {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        bitmapDrawable.setGravity(Gravity.CENTER);
        drawable = bitmapDrawable;
      }
      layers[items.indexOf(item)] = drawable;
    }

    LayerDrawable layerDrawable = new LayerDrawable(layers);
    return layerDrawable;
  }

  private void validateItemInStatedResource(String itemResource) throws MBInvalidItemException
  {
    MBResourceDefinition targetResourceDef = getConfig().getResourceWithID(itemResource);

    if (targetResourceDef instanceof MBStatedResourceDefinition)
    {
      throw new MBInvalidItemException("A stated resource can not have a stated item");
    }
  }

  private void validateItemInLayeredResource(String itemResource) throws MBInvalidItemException
  {
    MBResourceDefinition resourceDef = getConfig().getResourceWithID(itemResource);

    if (resourceDef instanceof MBStatedResourceDefinition)
    {
      throw new MBInvalidItemException("Layered resources can not contain stated resources");
    }
  }

  public byte[] getResourceByURL(String urlString)
  {
    return getResourceByURL(urlString, false, 0);
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
