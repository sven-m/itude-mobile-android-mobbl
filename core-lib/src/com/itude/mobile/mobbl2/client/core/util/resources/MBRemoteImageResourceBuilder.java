/*
 * (C) Copyright ItudeMobile.
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
