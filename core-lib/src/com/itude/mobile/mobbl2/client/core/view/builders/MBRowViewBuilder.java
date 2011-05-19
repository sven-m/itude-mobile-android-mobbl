package com.itude.mobile.mobbl2.client.core.view.builders;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBRow;

public class MBRowViewBuilder extends MBViewBuilder
{

  public ViewGroup buildRowView(MBRow row, MBViewManager.MBViewState viewState)
  {

    for (MBComponent child : row.getChildren())
    {
      Log.d(Constants.APPLICATION_NAME, "MBRowViewBuilder.buildRowView: " + child.getClass().getSimpleName());
    }

    LinearLayout rowView = new LinearLayout(MBApplicationController.getInstance().getBaseContext());
    buildChildren(row.getChildren(), rowView, viewState);
    getStyleHandler().applyStyle(row, rowView, viewState);
    return rowView;
  }

}
