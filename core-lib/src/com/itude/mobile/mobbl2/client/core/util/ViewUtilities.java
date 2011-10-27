package com.itude.mobile.mobbl2.client.core.util;

import android.view.View;
import android.view.ViewGroup;

public final class ViewUtilities
{

  private ViewUtilities()
  {
  }

  public static View detachView(View view)
  {
    if (view != null && view.getParent() != null)
    {
      ViewGroup parent = (ViewGroup) view.getParent();
      parent.removeView(view);
    }
    
    return view;
  }

}
