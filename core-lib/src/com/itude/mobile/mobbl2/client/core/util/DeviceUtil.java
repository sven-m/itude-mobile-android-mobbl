package com.itude.mobile.mobbl2.client.core.util;

import android.content.Context;
import android.provider.Settings;

public class DeviceUtil
{
  private static DeviceUtil _instance;
  private Context           _context;

  private DeviceUtil()
  {
  }

  public static DeviceUtil getInstance()
  {
    if (_instance == null)
    {
      _instance = new DeviceUtil();
    }

    return _instance;
  }

  public void setContext(Context context)
  {
    _context = context;
  }

  public String getUniqueID()
  {
    String androidID = Settings.Secure.getString(_context.getContentResolver(), Settings.Secure.ANDROID_ID);

    if (androidID == null)
    {
      return "";
    }

    return androidID;
  }
}
