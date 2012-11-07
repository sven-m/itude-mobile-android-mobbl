package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBComponentContainer;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.Builder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilder;

public class ListPanelBuilder extends MBViewBuilder implements Builder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, MBViewState viewState, BuildState buildState)
  {
    final Context context = MBApplicationController.getInstance().getBaseContext();
    LinearLayout result = new LinearLayout(context);
    result.setOrientation(LinearLayout.VERTICAL);   
    
    if (panel.getTitle() != null)
    {
      TextView title = new TextView(context);
      title.setText(panel.getTitle());
      result.addView(title);
      getStyleHandler().styleBasicPanelHeaderText(title);
    }
    buildChildren(panel.getChildren(), result, viewState);

      // Only add padding if this list isn't a direct child of a section
      MBComponentContainer parent = panel.getParent();
      boolean notDirectChildOfSection = (!(parent != null && parent instanceof MBPanel && (((MBPanel) parent).getType()) != null && ((MBPanel) parent)
          .getType().equals(Constants.C_SECTION)));

      
      getStyleHandler().styleListPanel(result, panel.getStyle(), notDirectChildOfSection);

   
      return result;

  }

}
