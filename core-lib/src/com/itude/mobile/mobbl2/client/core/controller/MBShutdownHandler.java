package com.itude.mobile.mobbl2.client.core.controller;

import android.app.Activity;

public abstract class MBShutdownHandler
{

  protected void finish()
  {
    getActivity().finish();
  }

  protected Activity getActivity()
  {
    return MBViewManager.getInstance();
  }

  public abstract void onShutdown();

}
