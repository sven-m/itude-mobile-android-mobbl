package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.R;
import android.graphics.drawable.Drawable;

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

    if (pressed != null)
    {
      int[] itemStates = new int[]{R.attr.state_pressed};

      processItem(pressed, itemStates);
    }

    if (enabled != null)
    {
      int[] itemStates = new int[]{R.attr.state_enabled, -R.attr.state_selected};

      processItem(enabled, itemStates);
    }

    if (disabled != null)
    {
      int[] itemStates = new int[]{-R.attr.state_enabled};

      processItem(disabled, itemStates);
    }

    if (selected != null)
    {
      int[] itemStates = new int[]{R.attr.state_selected};

      processItem(selected, itemStates);
    }
    if (checked != null)
    {
      int[] itemStates = new int[]{R.attr.state_checked};

      processItem(checked, itemStates);
    }

    return getStateListDrawable();
  }
}
