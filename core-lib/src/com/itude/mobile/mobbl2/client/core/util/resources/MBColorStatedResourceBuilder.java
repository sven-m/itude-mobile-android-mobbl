package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.R;
import android.content.res.ColorStateList;

import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.view.MBItem;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBColorStatedResourceBuilder implements MBResourceBuilder.Builder<ColorStateList>
{

  @Override
  public ColorStateList buildResource(MBResource resource)
  {
    Map<String, MBItem> items = resource.getItems();

    final int statedItemCount = items.size();
    if (statedItemCount == 0)
    {
      return null;
    }

    int[][] states = new int[statedItemCount][];
    int[] colors = new int[statedItemCount];
    int counter = 0;

    MBItem enabled = items.get("enabled");
    MBItem selected = items.get("selected");
    MBItem pressed = items.get("pressed");
    MBItem disabled = items.get("disabled");
    MBItem checked = items.get("checked");

    if (pressed != null)
    {
      String itemResource = pressed.getResource();

      states[counter] = new int[]{R.attr.state_pressed};
      colors[counter] = MBResourceService.getInstance().getColor(itemResource);

      counter++;
    }

    if (enabled != null)
    {
      String itemResource = enabled.getResource();

      states[counter] = new int[]{R.attr.state_enabled, -R.attr.state_selected};
      colors[counter] = MBResourceService.getInstance().getColor(itemResource);

      counter++;
    }

    if (disabled != null)
    {
      String itemResource = disabled.getResource();

      states[counter] = new int[]{-R.attr.state_enabled};
      colors[counter] = MBResourceService.getInstance().getColor(itemResource);

      counter++;
    }

    if (selected != null)
    {
      String itemResource = selected.getResource();

      states[counter] = new int[]{R.attr.state_selected};
      colors[counter] = MBResourceService.getInstance().getColor(itemResource);

      counter++;
    }
    if (checked != null)
    {
      String itemResource = checked.getResource();

      states[counter] = new int[]{R.attr.state_checked};
      colors[counter] = MBResourceService.getInstance().getColor(itemResource);

      counter++;
    }

    return new ColorStateList(states, colors);
  }
}
