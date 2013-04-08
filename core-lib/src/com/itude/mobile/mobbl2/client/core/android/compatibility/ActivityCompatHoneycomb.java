/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itude.mobile.mobbl2.client.core.android.compatibility;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;

/**
 * Implementation of activity compatibility that can call Honeycomb APIs.
 * Copied from Android Compatibility Package.
 */
public final class ActivityCompatHoneycomb
{
  private ActivityCompatHoneycomb()
  {
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static void invalidateOptionsMenu(final Activity activity)
  {
    activity.runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        activity.invalidateOptionsMenu();
      }
    });
  }

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  public static void enableHomeButton(final Activity activity, final ActionBar actionBar)
  {
    activity.runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          actionBar.setHomeButtonEnabled(true);
        }
        catch (NoSuchMethodError e)
        {
          // not running on ICS, so ignore
        }
      }
    });
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  static void dump(Activity activity, String prefix, FileDescriptor fd, PrintWriter writer, String[] args)
  {
    activity.dump(prefix, fd, writer, args);
  }
}
