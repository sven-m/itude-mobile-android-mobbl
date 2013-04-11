package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.R;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.view.MBItem;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBStatedImageResourceBuilder extends MBAbstractStatedResourceBuilder
{

  @Override
  public Drawable buildResource(MBResource resource)
  {
    Map<String, MBItem> items = resource.getItems();

    MBItem enabled = items.get("enabled");
    MBItem selected = items.get("selected");
    MBItem pressed = items.get("pressed");
    MBItem disabled = items.get("disabled");
    MBItem checked = items.get("checked");

    StateListDrawable stateDrawable = new StateListDrawable();

    if (pressed != null)
    {
      String itemResource = pressed.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = MBResourceService.getInstance().getImageByID(itemResource);
      stateDrawable.addState(new int[]{R.attr.state_pressed}, drawable);
    }

    if (enabled != null)
    {
      String itemResource = enabled.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = MBResourceService.getInstance().getImageByID(itemResource);
      stateDrawable.addState(new int[]{R.attr.state_enabled, -R.attr.state_selected}, drawable);
    }

    if (disabled != null)
    {
      String itemResource = disabled.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = MBResourceService.getInstance().getImageByID(itemResource);
      stateDrawable.addState(new int[]{-R.attr.state_enabled}, drawable);
    }

    if (selected != null)
    {
      String itemResource = selected.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = MBResourceService.getInstance().getImageByID(itemResource);
      stateDrawable.addState(new int[]{R.attr.state_selected}, drawable);
    }
    if (checked != null)
    {
      String itemResource = checked.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = MBResourceService.getInstance().getImageByID(itemResource);
      stateDrawable.addState(new int[]{R.attr.state_checked}, drawable);
    }

    return stateDrawable;
  }
}
