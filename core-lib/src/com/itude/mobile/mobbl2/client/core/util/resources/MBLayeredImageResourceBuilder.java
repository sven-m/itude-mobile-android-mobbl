package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.List;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;

import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.view.MBItem;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBLayeredImageResourceBuilder implements MBResourceBuilder.Builder<Drawable>
{

  @Override
  public Drawable buildResource(MBResource resource)
  {
    List<MBItem> items = resource.getSortedItemsReversed();

    if (items.size() == 0)
    {
      return null;
    }

    Drawable[] layers = new Drawable[items.size()];

    for (MBItem item : items)
    {
      String itemResource = item.getResource();

      Drawable drawable = MBResourceService.getInstance().getImageByID(itemResource);

      String align = item.getAlign();
      if (drawable instanceof BitmapDrawable)
      {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        setBitmapGravity(align, bitmapDrawable);

        drawable = bitmapDrawable;
      }
      layers[items.indexOf(item)] = drawable;
    }

    LayerDrawable layerDrawable = new LayerDrawable(layers);
    return layerDrawable;
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
    else
    {
      drawable.setGravity(Gravity.CENTER);
    }
  }
}
