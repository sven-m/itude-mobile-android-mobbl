package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;

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

  void showIndeterminateProgressIndicator()
  {
    if (_indeterminateIndicator != null) _indeterminateIndicator.increaseCount(getActivity());
  }

  void hideIndeterminateProgressIndicator()
  {
    if (_indeterminateIndicator != null) _indeterminateIndicator.decreaseCount(getActivity());
  }

  private MBViewManager getActivity()
  {
    return MBViewManager.getInstance();
  }

  void showActivityIndicator()
  {
    if (_activityIndicator != null) _activityIndicator.increaseCount(getActivity());
  }

  void hideActivityIndicator()
  {
    if (_activityIndicator != null) _activityIndicator.decreaseCount(getActivity());
  }

}
