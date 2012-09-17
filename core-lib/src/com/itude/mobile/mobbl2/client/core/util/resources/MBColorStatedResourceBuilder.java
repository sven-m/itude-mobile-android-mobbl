package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.R;
import android.content.res.ColorStateList;
import android.graphics.Color;

import com.itude.mobile.mobbl2.client.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfiguration;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBStatedResourceDefinition;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBResourceNotDefinedException;

public class MBColorStatedResourceBuilder extends MBAbstractStatedResourceBuilder
{

  public MBColorStatedResourceBuilder(MBResourceConfiguration config)
  {
    super(config);
  }

  @Override
  public <T> T build(MBStatedResourceDefinition def)
  {
    // We are assuming a list has been provided
    Map<String, MBItemDefinition> items = ((MBStatedResourceDefinition) def).getItems();

    final int statedItemCount = items.size();
    if (statedItemCount == 0)
    {
      return null;
    }

    int[][] states = new int[statedItemCount][];
    int[] colors = new int[statedItemCount];
    int counter = 0;

    MBItemDefinition enabled = items.get("enabled");
    MBItemDefinition selected = items.get("selected");
    MBItemDefinition pressed = items.get("pressed");
    MBItemDefinition disabled = items.get("disabled");
    MBItemDefinition checked = items.get("checked");

    if (pressed != null)
    {
      String resource = pressed.getResource();
      validateItemInStatedResource(resource);

      states[counter] = new int[]{R.attr.state_pressed};
      colors[counter] = getColorById(resource);

      counter++;
    }

    if (enabled != null)
    {
      String resource = enabled.getResource();
      validateItemInStatedResource(resource);

      states[counter] = new int[]{R.attr.state_enabled, -R.attr.state_selected};
      colors[counter] = getColorById(resource);

      counter++;
    }

    if (disabled != null)
    {
      String resource = disabled.getResource();
      validateItemInStatedResource(resource);

      states[counter] = new int[]{-R.attr.state_enabled};
      colors[counter] = getColorById(resource);

      counter++;
    }

    if (selected != null)
    {
      String resource = selected.getResource();
      validateItemInStatedResource(resource);

      states[counter] = new int[]{R.attr.state_selected};
      colors[counter] = getColorById(resource);

      counter++;
    }
    if (checked != null)
    {
      String resource = checked.getResource();
      validateItemInStatedResource(resource);

      states[counter] = new int[]{R.attr.state_checked};
      colors[counter] = getColorById(resource);

      counter++;
    }

    return (T) new ColorStateList(states, colors);
  }

  public int getColorById(String resourceId)
  {
    MBResourceDefinition def = MBResourceService.getInstance().getConfig().getResourceWithID(resourceId);

    if (def == null)
    {
      throw new MBResourceNotDefinedException("Resource for ID=" + resourceId + " could not be found");
    }

    return Color.parseColor(def.getColor());
  }

}
