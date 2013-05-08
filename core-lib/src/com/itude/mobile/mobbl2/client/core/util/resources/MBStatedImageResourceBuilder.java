package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.R;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBItem;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBStatedImageResourceBuilder extends MBAbstractStatedResourceBuilder
{

  @Override
  public Drawable buildResource(MBResource resource)
  {
    Map<String, MBItem> items = resource.getItems();

    MBItem enabled = items.get(Constants.C_STATED_RESOURCE_STATE_ENABLED);
    MBItem selected = items.get(Constants.C_STATED_RESOURCE_STATE_SELECTED);
    MBItem pressed = items.get(Constants.C_STATED_RESOURCE_STATE_PRESSED);
    MBItem disabled = items.get(Constants.C_STATED_RESOURCE_STATE_DISABLED);
    MBItem checked = items.get(Constants.C_STATED_RESOURCE_STATE_CHECKED);

    StateListDrawable stateListDrawable = new StateListDrawable();

    if (pressed != null)
    {
      int[] itemStates = new int[]{R.attr.state_pressed};

      processItem(stateListDrawable, pressed, itemStates);
    }

    if (enabled != null)
    {
      int[] itemStates = new int[]{R.attr.state_enabled, -R.attr.state_selected};

      processItem(stateListDrawable, enabled, itemStates);
    }

    if (disabled != null)
    {
      int[] itemStates = new int[]{-R.attr.state_enabled};

      processItem(stateListDrawable, disabled, itemStates);
    }

    if (selected != null)
    {
      int[] itemStates = new int[]{R.attr.state_selected};

      processItem(stateListDrawable, selected, itemStates);
    }
    if (checked != null)
    {
      int[] itemStates = new int[]{R.attr.state_checked};

      processItem(stateListDrawable, checked, itemStates);
    }

    return stateListDrawable;
  }
}
