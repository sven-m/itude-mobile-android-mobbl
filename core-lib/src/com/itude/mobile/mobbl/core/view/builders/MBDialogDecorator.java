package com.itude.mobile.mobbl.core.view.builders;

import android.support.v4.app.Fragment;

import com.itude.mobile.mobbl.core.controller.MBDialogController;

public interface MBDialogDecorator
{
  public void show (MBDialogController dialog);
  
  public void presentFragment (Fragment fragment, int containerId, String name, boolean addToBackStack);
  
  public void hide ();

  public void emptiedBackStack();
}
