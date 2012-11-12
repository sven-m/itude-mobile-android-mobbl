package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.Builder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilder;

public class MatrixPanelBuilder extends MBViewBuilder implements Builder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, MBViewState viewState, BuildState buildState)
  {
    buildState.resetMatrixRow();
    
    LinearLayout result = new LinearLayout(MBApplicationController.getInstance().getBaseContext());
    result.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    result.setOrientation(LinearLayout.VERTICAL);
    buildChildren(panel.getChildren(), result, viewState);

    getStyleHandler().styleMatrixContainer(panel, result);

    if (panel.getOutcomeName() != null && panel.getOutcomeName().length() > 0)
    {
      result.setOnClickListener(panel);
    }

    return result;

  }

}
