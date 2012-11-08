package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;

import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;

public abstract class MBCountingIndicator
{
  private AtomicInteger _queue = new AtomicInteger(0);
  private boolean       _shown;

  public void increaseCount(final Activity activity)
  {
    if (_queue.incrementAndGet() > 0)
    {

      activity.runOnUiThread(new MBThread()
      {
        @Override
        public void runMethod()
        {
          if (_queue.get() > 0 && !_shown)
          {
            show(activity);
            _shown = true;
          }
        }
      });
    }
  }

  public void decreaseCount(final Activity activity)
  {
    if (_queue.decrementAndGet() <= 0)
    {

      activity.runOnUiThread(new MBThread()
      {
        @Override
        public void runMethod()
        {
          if (_queue.get() <= 0 && _shown)
          {
            dismiss(activity);
            _shown = false;
          }
        }
      });
    }

  }

  protected abstract void show(Activity activity);

  protected abstract void dismiss(Activity activity);
}