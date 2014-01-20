package com.itude.mobile.mobbl.core.view.builders.contentview;

import android.view.View;

import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.view.builders.MBContentViewWrapper;

public class MBDefaultContentViewWrapper implements MBContentViewWrapper
{

  @Override
  public View buildContentView(MBViewManager viewManager, View mainContainer)
  {
    return mainContainer;
  }

}
