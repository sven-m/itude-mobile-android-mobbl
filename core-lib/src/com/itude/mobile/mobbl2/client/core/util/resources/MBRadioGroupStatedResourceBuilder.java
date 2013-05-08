package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.R;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBItem;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBRadioGroupStatedResourceBuilder extends MBAbstractStatedResourceBuilder
{

  @Override
  public Drawable buildResource(MBResource resource)
  {
    Map<String, MBItem> items = resource.getItems();

    MBItem checked = items.get(Constants.C_STATED_RESOURCE_STATE_CHECKED);
    MBItem unchecked = items.get(Constants.C_STATED_RESOURCE_STATE_UNCHECKED);

    StateListDrawable stateListDrawable = new StateListDrawable();

    if (checked != null)
    {
      int[] itemStates = new int[]{R.attr.state_checked};

      processItem(stateListDrawable, checked, itemStates);
    }
    if (unchecked != null)
    {
      int[] itemStates = new int[]{-R.attr.state_checked};

      processItem(stateListDrawable, unchecked, itemStates);
    }

    return stateListDrawable;
  }

}
