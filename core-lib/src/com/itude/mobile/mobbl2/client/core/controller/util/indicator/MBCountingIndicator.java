package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;

import com.itude.mobile.mobbl2.client.core.util.MBCustomAttributeContainer;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;

public abstract class MBCountingIndicator
{
  private final AtomicInteger _queue = new AtomicInteger(0);
  private boolean             _shown;

  public void increaseCount(final Activity activity)
  {
    increaseCount(activity, MBCustomAttributeContainer.EMPTY);
  }

  public void increaseCount(final Activity activity, final MBCustomAttributeContainer customAttributes)
  {
    if (_queue.incrementAndGet() > 0)
    {

      activity.runOnUiThread(new MBThread()
      {
        @Override
        public void runMethod()
        {
          if (_queue.get() > 0)

          if (!_shown)
          {
            show(activity, customAttributes);
            _shown = true;
          }
          else if (customAttributes.isHasCustomAttributes()) updateForAttributes(customAttributes);
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

  protected void updateForAttributes(MBCustomAttributeContainer customAttributes)
  {
  }

  protected abstract void show(Activity activity, MBCustomAttributeContainer customAttributes);

  protected abstract void dismiss(Activity activity);
}