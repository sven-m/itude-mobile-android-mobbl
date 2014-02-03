package com.itude.mobile.mobbl.core.view.builders;

import android.support.v4.app.Fragment;

import com.itude.mobile.mobbl.core.controller.MBDialogController;

public abstract class MBDialogDecorator
{
  private MBDialogController _dialog;

  public MBDialogDecorator(MBDialogController dialog)
  {
    _dialog = dialog;
  }

  public MBDialogController getDialog()
  {
    return _dialog;
  }

  public void show()
  {
  }

  public abstract void presentFragment(Fragment fragment, int containerId, String name, boolean addToBackStack);

  public void hide()
  {
  }

  public void emptiedBackStack()
  {
  }
}
