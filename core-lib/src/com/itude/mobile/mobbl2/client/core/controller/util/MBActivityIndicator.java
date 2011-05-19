package com.itude.mobile.mobbl2.client.core.controller.util;

import android.app.Activity;
import android.app.ProgressDialog;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;

public class MBActivityIndicator
{
  private static ProgressDialog _dialog = null;
  private static boolean        _active = false;

  public static void show(final Activity activity)
  {
    activity.runOnUiThread(new Runnable()
    {
      public void run()
      {
        _dialog = ProgressDialog.show(activity, MBLocalizationService.getInstance().getTextForKey("title_loading"), MBLocalizationService
            .getInstance().getTextForKey("msg_loading"), true, false);
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
        _dialog.dismiss();
        _active = false;
      }
    });
  }

  public static boolean isActive()
  {
    return _active;
  }
}
