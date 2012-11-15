package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;

public class MatrixPanelBuilder extends MBBasePanelBuilder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel,BuildState buildState)
  {
    buildState.resetMatrixRow();
    
    LinearLayout result = new LinearLayout(MBApplicationController.getInstance().getBaseContext());
    result.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    result.setOrientation(LinearLayout.VERTICAL);
    buildChildren(panel.getChildren(), result);

    getStyleHandler().styleMatrixContainer(panel, result);

    if (StringUtilities.isNotEmpty (panel.getOutcomeName()))
    {
      result.setOnClickListener(panel);
    }

    return result;

  }
}
