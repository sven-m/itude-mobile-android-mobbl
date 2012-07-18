package com.itude.mobile.mobbl2.client.core.controller;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBDevice;

/**
 * @author Coen Houtman
 *
 *  Factory to get an MBViewManager based on the device type.
 */
public class MBViewManagerFactory
{
  public static Class<? extends MBViewManager> getViewManagerClass()
  {
    if (MBDevice.getInstance().isTablet())
    {
      try
      {
        return (Class<? extends MBViewManager>) Class.forName("com.itude.mobile.mobbl2.client.core.controller.MBTabletViewManager");
      }
      catch (ClassNotFoundException e)
      {
        Log.e(Constants.APPLICATION_NAME, "Error initializing MBTabletViewManager");
        // Failed to load MBTabletViewManager, so return the default
      }
    }
    else if (MBDevice.getInstance().isPhoneV14())
    {
      try
      {
        return (Class<? extends MBViewManager>) Class.forName("com.itude.mobile.mobbl2.client.core.controller.MBPhoneViewManager");
      }
      catch (ClassNotFoundException e)
      {
        Log.e(Constants.APPLICATION_NAME, "Error initializing MBPhoneViewManager");
        // Failed to load MBPhoneViewManager, so return the default
      }
    }
    return MBViewManager.class;
  }
}
