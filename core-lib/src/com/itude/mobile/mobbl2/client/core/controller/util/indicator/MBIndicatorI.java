package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import android.app.Activity;

public interface MBIndicatorI
{
  public abstract void show(Activity activity);

  public abstract void dismiss(Activity activity);

  public boolean isActive();
}