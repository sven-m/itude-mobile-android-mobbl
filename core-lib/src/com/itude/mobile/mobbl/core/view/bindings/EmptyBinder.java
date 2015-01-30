package com.itude.mobile.mobbl.core.view.bindings;

import android.view.View;

public class EmptyBinder extends BaseViewBinder
{
  private final int id;

  protected EmptyBinder(int id)
  {
    this.id = id;
  }

  public static EmptyBinder getInstance(int id)
  {
    return new EmptyBinder(id);
  }

  @Override
  protected View bindSpecificView(BuildState state)
  {
    View view = state.parent.findViewById(id);
    return view;

  }

}