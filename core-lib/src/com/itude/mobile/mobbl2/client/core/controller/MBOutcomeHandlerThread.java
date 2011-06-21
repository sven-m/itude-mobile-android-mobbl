package com.itude.mobile.mobbl2.client.core.controller;

import android.os.Looper;
import android.util.Log;

public class MBOutcomeHandlerThread extends Thread
{
  public MBOutcomeHandlerThread(String name)
  {
    super(name);
    // TODO Auto-generated constructor stub
  }

  private MBOutcomeHandler _outcomeHandler;

  @Override
  public void run()
  {
    try
    {
      Looper.prepare();

      _outcomeHandler = new MBOutcomeHandler();

      Looper.loop();
    }
    catch (Throwable e)
    {
      Log.e("MOBBL", "Thread stopped due to error, good luck with it", e);
    }
  }

  public MBOutcomeHandler getOutcomeHandler()
  {
    return _outcomeHandler;
  }
}
