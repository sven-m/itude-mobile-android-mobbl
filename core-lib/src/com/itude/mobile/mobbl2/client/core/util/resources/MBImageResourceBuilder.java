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
package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBImageResourceBuilder implements MBResourceBuilder.Builder<Drawable>
{

  @Override
  public Drawable buildResource(MBResource resource)
  {

    // Only return an error if ways of getting the image fail
    Exception error = null;

    String url = resource.getUrl();
    if (url != null && url.startsWith("file://"))
    {

      Context baseContext = MBApplicationController.getInstance().getBaseContext();

      String imageName = url.substring(7);
      imageName = imageName.substring(0, imageName.indexOf(".")).toLowerCase(Locale.US);

      Resources resources = baseContext.getResources();

      try
      {
        int identifier = resources.getIdentifier(imageName, "drawable", baseContext.getPackageName());
        Drawable drawable = resources.getDrawable(identifier);
        if (drawable instanceof BitmapDrawable)
        {
          setBitmapGravity(resource.getAlign(), (BitmapDrawable) drawable);
        }
        return drawable;
      }
      catch (Exception e)
      {
        error = e;
      }
    }

    // Now attempt to get the image from different sources
    byte[] bytes = MBResourceService.getInstance().getResourceByURL(resource);
    if (bytes == null)
    {
      if (error != null)
      {
        Log.w(Constants.APPLICATION_NAME, "Warning: could not load file for resource=" + resource.getId(), error);
      }
      else
      {
        Log.w(Constants.APPLICATION_NAME, "Warning: could not load file for resource=" + resource.getId());
      }
      return null;
    }

    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    if (bitmap == null)
    {
      Log.w(Constants.APPLICATION_NAME, "Could not create image for resource=" + resource.getId());
    }

    Resources res = MBApplicationController.getInstance().getBaseContext().getResources();
    BitmapDrawable bitmapDrawable = new BitmapDrawable(res, bitmap);
    setBitmapGravity(resource.getAlign(), bitmapDrawable);

    return bitmapDrawable;
  }

  private void setBitmapGravity(String align, BitmapDrawable drawable)
  {
    if ("LEFT".equals(align))
    {
      drawable.setGravity(Gravity.LEFT);
    }
    else if ("RIGHT".equals(align))
    {
      drawable.setGravity(Gravity.RIGHT);
    }
    else if ("TOP".equals(align))
    {
      drawable.setGravity(Gravity.TOP);
    }
    else if ("BOTTOM".equals(align))
    {
      drawable.setGravity(Gravity.BOTTOM);
    }
    else if ("CENTER".equals(align))
    {
      drawable.setGravity(Gravity.CENTER);
    }
  }
}
