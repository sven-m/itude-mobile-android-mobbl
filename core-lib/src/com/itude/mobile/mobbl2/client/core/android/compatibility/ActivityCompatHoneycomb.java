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

import android.app.Activity;

/**
 * Implementation of activity compatibility that can call Honeycomb APIs.
 * Copied from Android Compatibility Package.
 */
public final class ActivityCompatHoneycomb
{
  private ActivityCompatHoneycomb()
  {
  }

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

  static void dump(Activity activity, String prefix, FileDescriptor fd, PrintWriter writer, String[] args)
  {
    activity.dump(prefix, fd, writer, args);
  }
}
