package com.itude.mobile.mobbl2.client.core.controller.util;

import android.app.Activity;

public class MBIndeterminateProgressIndicator
{
  private static boolean _active = false;

  public static void show(final Activity activity)
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

  public static void dismiss(final Activity activity)
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

  public static boolean isActive()
  {
    return _active;
  }
}
