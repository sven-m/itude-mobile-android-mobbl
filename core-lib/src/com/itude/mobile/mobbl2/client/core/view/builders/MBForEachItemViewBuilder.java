package com.itude.mobile.mobbl2.client.core.view.builders;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.view.MBForEachItem;

public class MBForEachItemViewBuilder extends MBViewBuilder
{

  public ViewGroup buildForEachItemView(MBForEachItem row)
  {
    LinearLayout rowView = new LinearLayout(MBApplicationController.getInstance().getBaseContext());
    buildChildren(row.getChildren(), rowView);
    getStyleHandler().applyStyle(row, rowView);
    return rowView;
  }

}
