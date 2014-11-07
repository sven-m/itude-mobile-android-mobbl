package com.itude.mobile.mobbl.core.util.log;

import android.util.Log;

import com.itude.mobile.mobbl.core.BuildConfig;
import com.itude.mobile.mobbl.core.util.Constants;

public class MBLog
{

  public static String TAG = Constants.APPLICATION_NAME;

  private static boolean shouldLog(int logLevel)
  {
    if (BuildConfig.DEBUG || logLevel > Log.DEBUG)
    {
      /*
       * In case of a release version of the application we only want to show the levels 
       * that are more important than debug level
       */
      return true;
    }

    return false;
  }

  public static void d(String message)
  {
    if (shouldLog(Log.DEBUG))
    {
      Log.d(TAG, message);
    }
  }

}
