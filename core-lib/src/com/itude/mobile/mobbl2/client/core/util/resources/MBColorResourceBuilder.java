package com.itude.mobile.mobbl2.client.core.util.resources;

import android.graphics.Color;

import com.itude.mobile.mobbl2.client.core.view.MBColorResource;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBColorResourceBuilder implements MBResourceBuilder.Builder<Integer>
{

  @Override
  public Integer buildResource(MBResource resource)
  {
    if (!(resource instanceof MBColorResource))
    {
      return null;
    }

    MBColorResource colorResource = (MBColorResource) resource;
    return Color.parseColor(colorResource.getColor());
  }

}
