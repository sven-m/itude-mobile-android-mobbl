package com.itude.mobile.mobbl2.client.core.view.builders;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.view.MBForEach;

public class MBForEachViewBuilder extends MBViewBuilder
{

  public ViewGroup buildForEachView(MBForEach forEach, MBViewManager.MBViewState viewState)
  {

    LinearLayout view = new LinearLayout(MBApplicationController.getInstance().getBaseContext());
    view.setOrientation(LinearLayout.VERTICAL);
    buildChildren(forEach.getRows(), view, viewState);

    buildChildren(forEach.getChildren(), view, viewState);

    getStyleHandler().applyStyle(forEach, view, viewState);

    return view;
  }

}
