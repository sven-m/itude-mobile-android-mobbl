package com.itude.mobile.mobbl2.client.core.util;

import android.os.Build;

/**
 * @author coenhoutman
 * 
 * The class provides methods for other classes to check what kind of device the application is running on.
 */
public class MBDevice
{
  private static final int DEVICE_TYPE_TABLET = 0;
  private static final int DEVICE_TYPE_PHONE  = 1;

  private static MBDevice  _instance;

  private int              _deviceType        = -1;

  private MBDevice()
  {
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB) _deviceType = DEVICE_TYPE_TABLET;
    else _deviceType = DEVICE_TYPE_PHONE;
  }

  public static MBDevice getInstance()
  {
    if (_instance == null)
    {
      synchronized (MBDevice.class)
      {
        if (_instance == null) _instance = new MBDevice();
      }
    }

    return _instance;
  }

  public boolean isPhone()
  {
    return _deviceType == DEVICE_TYPE_PHONE;
  }

  public boolean isTablet()
  {
    return _deviceType == DEVICE_TYPE_TABLET;
  }
}
