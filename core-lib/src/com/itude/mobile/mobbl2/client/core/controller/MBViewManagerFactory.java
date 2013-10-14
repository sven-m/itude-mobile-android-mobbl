/*
 * (C) Copyright ItudeMobile.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl2.client.core.controller;

import android.util.Log;

import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.mobbl2.client.core.util.Constants;

/**
 * @author Coen Houtman
 *
 *  Factory to get an MBViewManager based on the device type.
 */
public class MBViewManagerFactory
{
  public static Class<? extends MBViewManager> getViewManagerClass()
  {
    if (DeviceUtil.getInstance().isTablet())
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
    else if (DeviceUtil.getInstance().isPhoneV14())
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
