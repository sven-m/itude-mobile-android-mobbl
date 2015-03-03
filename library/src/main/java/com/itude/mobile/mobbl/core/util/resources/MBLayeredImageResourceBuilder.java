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
package com.itude.mobile.mobbl.core.util.resources;

import java.util.List;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;

import com.itude.mobile.mobbl.core.services.MBResourceService;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.view.MBItem;
import com.itude.mobile.mobbl.core.view.MBResource;

public class MBLayeredImageResourceBuilder implements MBResourceBuilder.Builder<Drawable>
{

  @Override
  public Drawable buildResource(MBResource resource)
  {
    List<MBItem> items = resource.getSortedItemsReversed();

    if (items.isEmpty())
    {
      return null;
    }

    Drawable[] layers = new Drawable[items.size()];

    for (int i = 0; i < items.size(); i++)
    {
      MBItem item = items.get(i);

      String itemResource = item.getResource();

      Drawable drawable = MBResourceService.getInstance().getImageByID(itemResource);

      String align = item.getAlign();
      if (drawable instanceof BitmapDrawable)
      {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        setBitmapGravity(align, bitmapDrawable);

        drawable = bitmapDrawable;
      }
      layers[i] = drawable;
    }

    LayerDrawable layerDrawable = new LayerDrawable(layers);
    return layerDrawable;
  }

  private void setBitmapGravity(String align, BitmapDrawable drawable)
  {
    if (Constants.C_GRAVITY_LEFT.equals(align))
    {
      drawable.setGravity(Gravity.START);
    }
    else if (Constants.C_GRAVITY_RIGHT.equals(align))
    {
      drawable.setGravity(Gravity.END);
    }
    else if (Constants.C_GRAVITY_TOP.equals(align))
    {
      drawable.setGravity(Gravity.TOP);
    }
    else if (Constants.C_GRAVITY_BOTTOM.equals(align))
    {
      drawable.setGravity(Gravity.BOTTOM);
    }
    else if (Constants.C_GRAVITY_CENTER.equals(align))
    {
      drawable.setGravity(Gravity.CENTER);
    }
    else
    {
      drawable.setGravity(Gravity.CENTER);
    }
  }
}
