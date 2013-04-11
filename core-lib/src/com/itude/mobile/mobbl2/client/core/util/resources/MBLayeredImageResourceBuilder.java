package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

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
      layers[items.indexOf(item)] = drawable;
    }

    LayerDrawable layerDrawable = new LayerDrawable(layers);
    return layerDrawable;
  }
}
