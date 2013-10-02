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
package com.itude.mobile.mobbl2.client.core.model;

import android.Manifest;
import android.content.pm.PackageManager;

import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;

public final class MBSession implements MBSessionInterface
{

  private static MBSessionInterface _instance;

  private MBSession()
  {

  }

  public static MBSessionInterface getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBSession();
    }

    return _instance;
  }

  public static void setInstance(MBSessionInterface session)
  {
    // To maintain a decent security, the user should be logged out as soon as the application is brought to the background; this is checked in MBViewManager.onPause.
    // However, the GET_TASKS permission is needed to verify it; to make sure an application has it when needed, an exception is thrown if an application tries to install
    // a MBSession without this permission.
    // Since the default MBSession doesn't do anything, this is only validated when installing an alternative session.
    if (MBApplicationController.getInstance().checkCallingOrSelfPermission(Manifest.permission.GET_TASKS) == PackageManager.PERMISSION_DENIED) throw new MBException(
        "The GET_TASKS permission is needed when using session management");

    _instance = session;
  }

  //
  //Override the following methods in an instance specific for your app; and register it app startup with setSharedInstance
  //
  @Override
  public MBDocument getDocument()
  {
    return null;
  }

  @Override
  public void logOff()
  {
  }

  @Override
  public boolean isLoggedOn()
  {
    return false;
  }

}
