package com.itude.mobile.mobbl.core.view.builders.contentview;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.view.builders.MBContentViewWrapper;

public class MBDefaultContentViewWrapper implements MBContentViewWrapper
{

  @Override
  public View buildContentView(MBViewManager viewManager, int emplacementId)
  {
    FrameLayout container = new FrameLayout(viewManager);
    LayoutParams layout = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    container.setLayoutParams(layout);
    container.setId(emplacementId);
    return container;
  }

}
