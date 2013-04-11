package com.itude.mobile.mobbl2.client.core.util.resources;

import android.graphics.drawable.Drawable;

import com.itude.mobile.mobbl2.client.core.view.MBResource;

public class MBRadioGroupStatedResourceBuilder extends MBAbstractStatedResourceBuilder
{

  @Override
  public Drawable buildResource(MBResource resource)
  {
    // TODO Auto-generated method stub
    return null;
  }

  //  public MBRadioGroupStatedResourceBuilder(MBResourceConfiguration config)
  //  {
  //    super(config);
  //  }
  //
  //  @Override
  //  public <T> T build(MBStatedResourceDefinition def)
  //  {
  //    Map<String, MBItemDefinition> items = getItems(def);
  //
  //    MBItemDefinition checked = items.get("checked");
  //    MBItemDefinition unchecked = items.get("unchecked");
  //
  //    StateListDrawable stateDrawable = new StateListDrawable();
  //
  //    if (checked != null)
  //    {
  //      String resource = checked.getResource();
  //      validateItemInStatedResource(resource);
  //      Drawable drawable = getImageByID(resource);
  //      stateDrawable.addState(new int[]{R.attr.state_checked}, drawable);
  //    }
  //    if (unchecked != null)
  //    {
  //      String resource = unchecked.getResource();
  //      validateItemInStatedResource(resource);
  //      Drawable drawable = getImageByID(resource);
  //      stateDrawable.addState(new int[]{-R.attr.state_checked}, drawable);
  //    }
  //
  //    return (T) stateDrawable;
  //  }

}
