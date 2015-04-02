/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
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
package com.itude.mobile.mobbl.core.controller.helpers;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Helper Class to be used for activities
 */
public class MBActivityHelper {

    /**
     * Default private constructor
     */
    private MBActivityHelper() {
    }

    /**
     * Checks if the application is in the background (i.e behind another application's Activity).
     *
     * @param context {@link Context}
     * @return true if another application is above this one.
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        if (context.checkCallingOrSelfPermission(Manifest.permission.GET_TASKS) == PackageManager.PERMISSION_DENIED)
            return false;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())
                    && !topActivity.getPackageName().equals("com.google.android.voicesearch")) {
                return true;
            }
        }

        return false;
    }
}