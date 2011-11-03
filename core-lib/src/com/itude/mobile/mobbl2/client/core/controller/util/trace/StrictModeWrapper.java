package com.itude.mobile.mobbl2.client.core.controller.util.trace;

import android.os.StrictMode;

public final class StrictModeWrapper
{

  static
  {
    try
    {
      Class.forName("android.os.StrictMode", true, Thread.currentThread().getContextClassLoader());
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private StrictModeWrapper()
  {
  }

  public static void checkAvailable()
  {
  }

  public static void enableDefaults()
  {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
  }
}
