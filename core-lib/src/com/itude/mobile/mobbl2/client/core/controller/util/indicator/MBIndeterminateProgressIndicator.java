package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import android.app.Activity;

import com.itude.mobile.mobbl2.client.core.util.MBCustomAttributeContainer;

public final class MBIndeterminateProgressIndicator extends MBCountingIndicator
{
  MBIndeterminateProgressIndicator()
  {

  }

  @Override
  protected void show(final Activity activity, MBCustomAttributeContainer customAttributes)
  {
    activity.setProgressBarIndeterminate(true);
    activity.setProgressBarIndeterminateVisibility(true);
  }

  @Override
  protected void dismiss(final Activity activity)
  {
    activity.setProgressBarIndeterminateVisibility(false);
  }
}
