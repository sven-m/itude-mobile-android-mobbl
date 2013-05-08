package com.itude.mobile.mobbl2.client.core.util.resources;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.itude.mobile.mobbl2.client.core.configuration.resources.exceptions.MBInvalidItemException;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.view.MBItem;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public abstract class MBAbstractStatedResourceBuilder implements MBResourceBuilder.Builder<Drawable>
{
  protected void validateItemInStatedResource(MBResource resource) throws MBInvalidItemException
  {
  }

  protected void processItem(StateListDrawable stateListDrawable, MBItem item, int[] itemStates)
  {
    String itemResource = item.getResource();
    Drawable drawable = MBResourceService.getInstance().getImageByID(itemResource);

    stateListDrawable.addState(itemStates, drawable);
  }
}
