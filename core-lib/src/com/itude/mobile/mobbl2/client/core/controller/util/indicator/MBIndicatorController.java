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

  private MBIndicatorI _activityIndicator;
  private MBIndicatorI _indeterminateIndicator;

  public void setActivityIndicator(MBIndicatorI activityIndicator)
  {
    _activityIndicator = activityIndicator;
  }

  public void setIndeterminateIndicator(MBIndicatorI indeterminateIndicator)
  {
    _indeterminateIndicator = indeterminateIndicator;
  }

  void showIndeterminateProgressIndicator()
  {
    if (_indeterminateIndicator != null) _indeterminateIndicator.show(getActivity());
  }

  void hideIndeterminateProgressIndicator()
  {
    if (_indeterminateIndicator != null) _indeterminateIndicator.dismiss(getActivity());
  }

  private MBViewManager getActivity()
  {
    return MBViewManager.getInstance();
  }

  void showActivityIndicator()
  {
    if (_activityIndicator != null) _activityIndicator.show(getActivity());
  }

  void hideActivityIndicator()
  {
    if (_activityIndicator != null) _activityIndicator.dismiss(getActivity());
  }

}
