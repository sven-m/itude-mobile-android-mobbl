package com.itude.mobile.mobbl2.client.core.util.resources;

import java.util.Map;

import android.R;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.itude.mobile.mobbl2.client.core.configuration.resources.MBItemDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfiguration;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBStatedResourceDefinition;

public class MBRadioGroupStatedResourceBuilder extends MBAbstractStatedResourceBuilder
{

  public MBRadioGroupStatedResourceBuilder(MBResourceConfiguration config)
  {
    super(config);
  }

  @Override
  public <T> T build(MBStatedResourceDefinition def)
  {
    Map<String, MBItemDefinition> items = getItems(def);

    MBItemDefinition checked = items.get("checked");
    MBItemDefinition unchecked = items.get("unchecked");

    StateListDrawable stateDrawable = new StateListDrawable();

    if (checked != null)
    {
      String resource = checked.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{R.attr.state_checked}, drawable);
    }
    if (unchecked != null)
    {
      String resource = unchecked.getResource();
      validateItemInStatedResource(resource);
      Drawable drawable = getImageByID(resource);
      stateDrawable.addState(new int[]{-R.attr.state_checked}, drawable);
    }

    return (T) stateDrawable;
  }

}
