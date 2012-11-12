package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.Builder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilder;
import com.itude.mobile.mobbl2.client.core.view.components.MBSegmentedControlContainer;

public class SegmentedControlPanelBuilder extends MBViewBuilder implements Builder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, MBViewState viewState, BuildState buildState)
  {
    final Context context = MBApplicationController.getInstance().getBaseContext();

    MBSegmentedControlContainer scc = new MBSegmentedControlContainer(context, panel);
    scc.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    scc.setOrientation(LinearLayout.VERTICAL);

    getStyleHandler().styleSegmentedControlContainer(scc, panel);

    return scc;
  }

}
