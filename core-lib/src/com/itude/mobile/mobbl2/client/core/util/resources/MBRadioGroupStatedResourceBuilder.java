package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.R;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.view.MBItem;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBRadioGroupStatedResourceBuilder extends MBAbstractStatedResourceBuilder
{

  @Override
  public Drawable buildResource(MBResource resource)
  {
    Map<String, MBItem> items = resource.getItems();

    MBItem checked = items.get("checked");
    MBItem unchecked = items.get("unchecked");

    StateListDrawable stateDrawable = new StateListDrawable();

    if (checked != null)
    {
      String itemResource = checked.getResource();
      Drawable drawable = MBResourceService.getInstance().getImageByID(itemResource);
      stateDrawable.addState(new int[]{R.attr.state_checked}, drawable);
    }
    if (unchecked != null)
    {
      String itemResource = unchecked.getResource();
      Drawable drawable = MBResourceService.getInstance().getImageByID(itemResource);
      stateDrawable.addState(new int[]{-R.attr.state_checked}, drawable);
    }

    return stateDrawable;
  }

}
