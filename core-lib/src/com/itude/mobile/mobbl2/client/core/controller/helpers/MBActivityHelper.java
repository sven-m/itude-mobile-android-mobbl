package com.itude.mobile.mobbl2.client.core.controller.helpers;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

public class MBActivityHelper
{

  /**
  * Checks if the application is in the background (i.e behind another application's Activity).
  *
  * @param context
  * @return true if another application is above this one.
  */
  public static boolean isApplicationBroughtToBackground(final Context context)
  {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningTaskInfo> tasks = am.getRunningTasks(1);
    if (!tasks.isEmpty())
    {
      ComponentName topActivity = tasks.get(0).topActivity;
      if (!topActivity.getPackageName().equals(context.getPackageName())
          && !topActivity.getPackageName().equals("com.google.android.voicesearch"))
      {
        return true;
      }
    }

    return false;
  }
}
