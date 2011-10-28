package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import android.app.Activity;

public final class MBIndeterminateProgressIndicator implements MBIndicatorI
{
  private static MBIndeterminateProgressIndicator _instance = null;

  private boolean                                 _active   = false;

  private MBIndeterminateProgressIndicator()
  {
  }

  public static MBIndeterminateProgressIndicator getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBIndeterminateProgressIndicator();
    }

    return _instance;
  }

  @Override
  public void show(final Activity activity)
  {
    activity.runOnUiThread(new Runnable()
    {
      public void run()
      {
        activity.setProgressBarIndeterminate(true);
        activity.setProgressBarIndeterminateVisibility(true);
        _active = true;
      }
    });
  }

  @Override
  public void dismiss(final Activity activity)
  {
    activity.runOnUiThread(new Runnable()
    {
      public void run()
      {
        activity.setProgressBarIndeterminateVisibility(false);
        _active = false;
      }
    });
  }

  @Override
  public boolean isActive()
  {
    return _active;
  }
}
