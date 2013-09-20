package com.itude.mobile.mobbl2.client.core.controller;

import android.app.Activity;

import com.itude.mobile.mobbl2.client.core.util.helper.MBSecurityHelper;

public abstract class MBShutdownHandler
{

  protected void finish()
  {
    MBApplicationController.getInstance().setShuttingDown(true);
    MBSecurityHelper.getInstance().logOutIfCheckNotSelected();
    getActivity().finish();
    MBApplicationController.getInstance().setShuttingDown(false);
  }

  protected Activity getActivity()
  {
    return MBViewManager.getInstance();
  }

  public abstract void onShutdown();

}
