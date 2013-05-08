package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.R;
import android.content.res.ColorStateList;

import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.view.MBItem;
import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBColorStatedResourceBuilder implements MBResourceBuilder.Builder<ColorStateList>
{

  private int[][] _states;
  private int[]   _colors;
  private int     _counter;

  @Override
  public ColorStateList buildResource(MBResource resource)
  {
    Map<String, MBItem> items = resource.getItems();

    final int statedItemCount = items.size();
    if (statedItemCount == 0)
    {
      return null;
    }

    _states = new int[statedItemCount][];
    _colors = new int[statedItemCount];
    _counter = 0;

    MBItem enabled = items.get("enabled");
    MBItem selected = items.get("selected");
    MBItem pressed = items.get("pressed");
    MBItem disabled = items.get("disabled");
    MBItem checked = items.get("checked");

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

    return new ColorStateList(_states, _colors);
  }

  private void processItem(MBItem item, int[] itemStates)
  {
    String itemResource = item.getResource();

    int color = MBResourceService.getInstance().getColor(itemResource);

    _states[_counter] = itemStates;
    _colors[_counter] = color;

    _counter++;
  }
}
