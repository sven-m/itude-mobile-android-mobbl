package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import android.app.Activity;

public class MBIndicatorController
{
  private static MBIndicatorController _instance;

  public static MBIndicatorController getInstance()
  {
    if (_instance == null) _instance = new MBIndicatorController();
    return _instance;
  }

  private MBCountingIndicator _activityIndicator      = new MBActivityIndicator();
  private MBCountingIndicator _indeterminateIndicator = new MBIndeterminateProgressIndicator();

  public void setActivityIndicator(MBCountingIndicator activityIndicator)
  {
    _activityIndicator = activityIndicator;
  }

  public void setIndeterminateIndicator(MBCountingIndicator indeterminateIndicator)
  {
    _indeterminateIndicator = indeterminateIndicator;
  }

  void showIndeterminateProgressIndicator(Activity activity)
  {
    if (_indeterminateIndicator != null) _indeterminateIndicator.increaseCount(activity);
  }

  void hideIndeterminateProgressIndicator(Activity activity)
  {
    if (_indeterminateIndicator != null) _indeterminateIndicator.decreaseCount(activity);
  }
  
  void showActivityIndicator(Activity activity)
  {
    if (_activityIndicator != null) _activityIndicator.increaseCount(activity);
  }

  void hideActivityIndicator(Activity activity)
  {
    if (_activityIndicator != null) _activityIndicator.decreaseCount(activity);
  }

}
