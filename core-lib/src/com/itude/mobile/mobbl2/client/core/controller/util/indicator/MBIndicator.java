package com.itude.mobile.mobbl2.client.core.controller.util.indicator;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBIndicator
{
  // type 'none' obviously doesn't show anything; it's there to have a handle for some fringe cases in which we show indicators pretty much all of the time, with some small exceptions
  public enum Type {
    none, indeterminate, activity
  };

  private final Type _type;
  private boolean    _active;

  protected MBIndicator(Type type)
  {
    _type = type;
    _active = true;
  }

  private void showIndicator()
  {
    switch (_type)
    {
      case indeterminate :
        MBIndicatorController.getInstance().showIndeterminateProgressIndicator();
        break;
      case activity :
        MBIndicatorController.getInstance().showActivityIndicator();
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
        MBIndicatorController.getInstance().hideIndeterminateProgressIndicator();
        break;
      case activity :
        MBIndicatorController.getInstance().hideActivityIndicator();
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

  public static MBIndicator show(Type type)
  {
    MBIndicator indicator = new MBIndicator(type);
    indicator.showIndicator();
    return indicator;
  }
}
