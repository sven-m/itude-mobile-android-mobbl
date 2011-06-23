package com.itude.mobile.mobbl2.client.core.controller;

import android.os.HandlerThread;

public class MBOutcomeHandlerThread extends HandlerThread
{
  private MBOutcomeHandler _outcomeHandler;

  public MBOutcomeHandlerThread(String name)
  {
    super(name);
  }

  @Override
  public void run()
  {
    super.run();
    _outcomeHandler = null;
  }

  @Override
  protected void onLooperPrepared()
  {
    _outcomeHandler = new MBOutcomeHandler();
  }

  public MBOutcomeHandler getOutcomeHandler()
  {
    return _outcomeHandler;
  }
}
