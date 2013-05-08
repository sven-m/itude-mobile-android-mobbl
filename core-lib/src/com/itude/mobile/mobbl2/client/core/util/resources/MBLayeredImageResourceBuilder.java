package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.List;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;

import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
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
      drawable.setGravity(Gravity.LEFT);
    }
    else if (Constants.C_GRAVITY_RIGHT.equals(align))
    {
      drawable.setGravity(Gravity.RIGHT);
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
