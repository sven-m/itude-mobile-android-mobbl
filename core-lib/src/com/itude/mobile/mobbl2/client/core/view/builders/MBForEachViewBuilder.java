package com.itude.mobile.mobbl2.client.core.view.builders;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.view.MBForEach;

public class MBForEachViewBuilder extends MBViewBuilder
{

  public ViewGroup buildForEachView(MBForEach forEach)
  {

    LinearLayout view = new LinearLayout(MBApplicationController.getInstance().getBaseContext());
    view.setOrientation(LinearLayout.VERTICAL);
    buildChildren(forEach.getRows(), view);

    buildChildren(forEach.getChildren(), view);

    getStyleHandler().applyStyle(forEach, view);

    return view;
  }

}
