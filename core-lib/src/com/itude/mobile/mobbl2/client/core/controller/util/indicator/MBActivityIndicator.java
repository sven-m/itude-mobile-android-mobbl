package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.ProgressDialog;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;

public final class MBActivityIndicator implements MBIndicatorI
{
  private static MBActivityIndicator _instance;

  private ProgressDialog             _dialog = null;
  private AtomicInteger              _queue  = new AtomicInteger(0);

  private MBActivityIndicator()
  {
  }

  public static MBActivityIndicator getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBActivityIndicator();
    }
    return _instance;
  }

  @Override
  public void show(final Activity activity)
  {
    activity.runOnUiThread(new MBThread()
    {
      @Override
      public void runMethod()
      {
        if (_queue.getAndIncrement() > 0)
        {
          return;
        }
        _dialog = ProgressDialog.show(activity, MBLocalizationService.getInstance().getTextForKey("title_loading"), MBLocalizationService
            .getInstance().getTextForKey("msg_loading"), true, false);
      }
    });
  }

  @Override
  public void dismiss(final Activity activity)
  {

    activity.runOnUiThread(new MBThread()
    {
      @Override
      public void runMethod()
      {
        if (_queue.decrementAndGet() > 0)
        {
          return;
        }
        _dialog.dismiss();
        _dialog = null;
      }
    });
  }

  @Override
  public boolean isActive()
  {
    return _queue.get() > 0;
  }
}
