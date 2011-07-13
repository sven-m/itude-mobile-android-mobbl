package com.itude.mobile.mobbl2.client.core.controller.util;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.ProgressDialog;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;

public final class MBActivityIndicator
{
  private static ProgressDialog _dialog = null;
  private static AtomicInteger  _queue  = new AtomicInteger(0);

  private MBActivityIndicator()
  {
  }

  public static void show(final Activity activity)
  {
    if (_queue.getAndIncrement() > 0)
    {
      return;
    }

    activity.runOnUiThread(new Runnable()
    {
      public void run()
      {
        _dialog = ProgressDialog.show(activity, MBLocalizationService.getInstance().getTextForKey("title_loading"), MBLocalizationService
            .getInstance().getTextForKey("msg_loading"), true, false);
      }
    });
  }

  public static void dismiss(final Activity activity)
  {
    if (_queue.decrementAndGet() > 0) return;

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
    return _queue.get() > 0;
  }
}
