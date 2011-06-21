package com.itude.mobile.mobbl2.client.core.controller.util;

import android.app.Activity;
import android.app.ProgressDialog;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;

public class MBActivityIndicator
{
  private static ProgressDialog _dialog = null;
  private static int            _queue  = 0;

  public static void show(final Activity activity)
  {
    if (_queue++ > 0)
    {
      return;
    }

    activity.runOnUiThread(new Runnable()
    {
      public void run()
      {
        _dialog = ProgressDialog.show(activity, MBLocalizationService.getInstance().getTextForKey("title_loading"), MBLocalizationService
            .getInstance().getTextForKey("msg_loading"), true, true);
      }
    });
  }

  public static void dismiss(final Activity activity, boolean force)
  {
    if (force)
    {
      doDismiss(activity);
      _queue = 0;
    }
    else if (--_queue == 0)
    {
      doDismiss(activity);
    }
  }

  private static void doDismiss(final Activity activity)
  {
    activity.runOnUiThread(new Runnable()
    {
      public void run()
      {
        _dialog.dismiss();
        _dialog = null;
      }
    });
  }

  public static boolean isActive()
  {
    return _queue > 0;
  }
}
