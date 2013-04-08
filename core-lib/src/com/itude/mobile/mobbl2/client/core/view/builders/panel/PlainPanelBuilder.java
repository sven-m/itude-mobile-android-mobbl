package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;

public class PlainPanelBuilder extends MBBasePanelBuilder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, BuildState buildState)
  {
    LinearLayout result = new LinearLayout(MBApplicationController.getInstance().getBaseContext());

    final Context context = MBApplicationController.getInstance().getBaseContext();

    getStyleHandler().styleBasicPanelHeader(result, panel.getStyle());

    if (panel.getTitle() != null)
    {
      TextView title = new TextView(context);
      title.setText(panel.getTitle());
      result.addView(title);
      getStyleHandler().styleBasicPanelHeaderText(title);
    }
    buildChildren(panel.getChildren(), result);
    return result;
  }

}
