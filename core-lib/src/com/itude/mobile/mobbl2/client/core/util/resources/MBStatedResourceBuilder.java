package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.R;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.itude.mobile.mobbl2.client.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfiguration;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBStatedResourceDefinition;

public class MBStatedResourceBuilder extends MBAbstractStatedResourceBuilder
{

  public MBStatedResourceBuilder(MBResourceConfiguration config)
  {
    super(config);
  }

  @Override
  public <T> T build(MBStatedResourceDefinition def)
  {
    Map<String, MBItemDefinition> items = getItems(def);

    MBItemDefinition enabled = items.get("enabled");
    MBItemDefinition selected = items.get("selected");
    MBItemDefinition pressed = items.get("pressed");
    MBItemDefinition disabled = items.get("disabled");
    MBItemDefinition checked = items.get("checked");

    StateListDrawable stateDrawable = new StateListDrawable();

    if (pressed != null)
    {
      String resource = pressed.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{R.attr.state_pressed}, drawable);
    }

    if (enabled != null)
    {
      String resource = enabled.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{R.attr.state_enabled, -R.attr.state_selected}, drawable);
    }

    if (disabled != null)
    {
      String resource = disabled.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{-R.attr.state_enabled}, drawable);
    }

    if (selected != null)
    {
      String resource = selected.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{R.attr.state_selected}, drawable);
    }
    if (checked != null)
    {
      String resource = checked.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{R.attr.state_checked}, drawable);
    }

    return (T) stateDrawable;
  }

}
