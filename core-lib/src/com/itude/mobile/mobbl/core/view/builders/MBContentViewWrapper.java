package com.itude.mobile.mobbl.core.view.builders;

import android.view.View;

import com.itude.mobile.mobbl.core.controller.MBViewManager;

public interface MBContentViewWrapper
{
  public View buildContentView(MBViewManager viewManager, View mainContainer);
}
