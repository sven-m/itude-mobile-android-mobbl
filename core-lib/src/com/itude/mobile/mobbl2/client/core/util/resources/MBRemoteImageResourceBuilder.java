package com.itude.mobile.mobbl2.client.core.util.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBRemoteImageResourceBuilder implements MBResourceBuilder.Builder<Drawable>
{

  @Override
  public Drawable buildResource(MBResource resource)
  {
    Drawable image;
    try
    {
      image = Drawable.createFromStream((InputStream) new URL(resource.getUrl()).getContent(), "src");
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

}
