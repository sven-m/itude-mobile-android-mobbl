package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;

public class SectionPanelBuilder extends MBBasePanelBuilder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel,  BuildState buildState)
  {
      boolean hasTitle = false;
      Context context = MBApplicationController.getInstance().getBaseContext();

      LinearLayout panelView = new LinearLayout(context);
      panelView.setOrientation(LinearLayout.VERTICAL);

      if (panel.getTitle() != null)
      {
        // Show header at top of the section
        hasTitle = true;

        LinearLayout header = new LinearLayout(context);
        header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        header.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(header.getContext());
        title.setText(panel.getTitle());

        getStyleHandler().styleSectionHeaderText(title);
        getStyleHandler().styleSectionHeaderText(title, panel);

        header.addView(title);

        getStyleHandler().styleSectionHeader(header);
        getStyleHandler().styleSectionHeader(header, panel);

        panelView.addView(header);
      }

      buildChildren(panel.getChildren(), panelView);

      getStyleHandler().styleSectionContainer(panelView, hasTitle);
      getStyleHandler().styleSectionContainer(panelView, hasTitle, panel);

      return panelView;
   
  }

}
