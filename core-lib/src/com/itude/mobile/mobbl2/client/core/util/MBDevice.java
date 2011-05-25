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

  public String getDeviceType()
  {
    if (isPhone()) return "Smartphone";
    else if (isTablet()) return "Tablet";
    else return "Unknown";
  }

  public String getOSVersion()
  {
    switch (Build.VERSION.SDK_INT)
    {
      case (Build.VERSION_CODES.BASE) : //$FALL-THROUGH$
      case (Build.VERSION_CODES.BASE_1_1) :
        return "Android 1";
      case (Build.VERSION_CODES.CUPCAKE) :
        return "Android 1.5 Cupcake";
      case (Build.VERSION_CODES.DONUT) :
        return "Android 1.6 Donut";
      case (Build.VERSION_CODES.ECLAIR) : //$FALL-THROUGH$
      case (Build.VERSION_CODES.ECLAIR_MR1) : //$FALL-THROUGH$
      case (Build.VERSION_CODES.ECLAIR_0_1) :
        return "Android 2.0/2.1 Eclair";
      case (Build.VERSION_CODES.FROYO) :
        return "Android 2.2 Froyo";
      case (Build.VERSION_CODES.GINGERBREAD) :
        return "Android 2.3 Gingerbread";
      case (Build.VERSION_CODES.GINGERBREAD_MR1) :
        return "Android 2.3.3 Gingerbread";
      case (Build.VERSION_CODES.HONEYCOMB) :
        return "Android 3.0 Honeycomb";
      default :
        return "Unknown";
    }
  }

  public boolean isPhone()
  {
    return _deviceType == DEVICE_TYPE_PHONE;
  }

  public boolean isTablet()
  {
    return _deviceType == DEVICE_TYPE_TABLET;
  }

  @Override
  public String toString()
  {
    String result = "Device Info:\n" + " - Type: " + getDeviceType() + "\n" + " - OS version: " + getOSVersion();

    return result;
  }
}
