package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import android.app.Activity;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBIndicator
{
  // type 'none' obviously doesn't show anything; it's there to have a handle for some fringe cases in which we show indicators pretty much all of the time, with some small exceptions
  public enum Type {
    none, indeterminate, activity
  };

  private final Type     _type;
  private boolean        _active;
  private final Activity _activity;

  protected MBIndicator(Type type)
  {
    _type = type;
    _active = true;
    _activity = MBViewManager.getInstance();
  }

  protected MBIndicator(Activity activity, Type type)
  {
    _type = type;
    _active = true;
    _activity = activity;
  }

  private void showIndicator()
  {
    switch (_type)
    {
      case indeterminate :
        MBIndicatorController.getInstance().showIndeterminateProgressIndicator(_activity);
        break;
      case activity :
        MBIndicatorController.getInstance().showActivityIndicator(_activity);
        break;
      case none :
        break;
    }
  }

  private void hideIndicator()
  {
    switch (_type)
    {
      case indeterminate :
        MBIndicatorController.getInstance().hideIndeterminateProgressIndicator(_activity);
        break;
      case activity :
        MBIndicatorController.getInstance().hideActivityIndicator(_activity);
        break;
      case none :
        break;
    }

  }

  public void release()
  {
    if (!_active) throw new MBException("Trying to release already released MBIndicator");
    _active = false;
    hideIndicator();
  }

  /**
   * @deprecated Only exists to support applications that don't cleanly use the framework, like Actum. Don't use this in any new application! 
   */
  @Deprecated
  public static MBIndicator show(Activity activity, Type type)
  {
    MBIndicator indicator = new MBIndicator(activity, type);
    indicator.showIndicator();
    return indicator;

  }

  public static MBIndicator show(Type type)
  {
    MBIndicator indicator = new MBIndicator(type);
    indicator.showIndicator();
    return indicator;
  }
}
