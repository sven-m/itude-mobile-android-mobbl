package com.itude.mobile.mobbl2.client.core.controller;

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
    if (MBDevice.getInstance().isTablet()) return MBTabletViewManager.class;
    return MBViewManager.class;
  }
}
