package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.Builder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class RowPanelBuilder extends MBViewBuilder implements Builder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, MBViewState viewState, BuildState buildState)
  {

    final Context context = MBApplicationController.getInstance().getBaseContext();
    HashMap<String, Object> childIds = new HashMap<String, Object>();

    RelativeLayout rowPanel = new RelativeLayout(context);
    rowPanel.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    rowPanel.setTag(childIds);

    // Content view
    buildChildrenForRowPanel(panel.getChildren(), rowPanel, null);

    // Arrow and clickable style of row
    MBStyleHandler styleHandler = getStyleHandler();
    if (panel.getOutcomeName() != null)
    {
      rowPanel.setClickable(true);
      rowPanel.setFocusable(true);
      rowPanel.setOnClickListener(panel);

      styleHandler.styleClickableRow(rowPanel, panel.getStyle());
    }
    else
    {
      // Make sure to style the unclickable row
      styleHandler.styleRow(rowPanel, panel.getStyle());
    }

    return rowPanel;

  }

  /*
   * FIXME needs refactoring. Implementation too specific
   */
  private void buildChildrenForRowPanel(List<? extends MBComponent> children, ViewGroup parent, MBViewManager.MBViewState viewState)
  {
    final Context context = parent.getContext();
    final MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    int previousButton = -1;

    LinearLayout nonButtonLayout = null;
    boolean processingLabel = false;
    LinearLayout labelLayout = null;

    for (MBComponent child : children)
    {
      View childView = child.buildViewWithMaxBounds(viewState);
      if (childView == null) continue;

      int childID = UniqueIntegerGenerator.getId();

      RelativeLayout.LayoutParams childParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      childParams.addRule(RelativeLayout.CENTER_VERTICAL);

      if (!isFieldWithType(child, Constants.C_FIELD_BUTTON) && !isFieldWithType(child, Constants.C_FIELD_IMAGEBUTTON))
      {
        if (nonButtonLayout == null)
        {
          RelativeLayout.LayoutParams nonButtonLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);
          nonButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
          nonButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

          nonButtonLayout = new LinearLayout(context);
          nonButtonLayout.setLayoutParams(nonButtonLayoutParams);
          nonButtonLayout.setGravity(Gravity.CENTER_VERTICAL);

          nonButtonLayout.setId(childID);
          styleHandler.styleRowAlignment(nonButtonLayout);
        }

        if (isFieldWithType(child, Constants.C_FIELD_LABEL) && !processingLabel)
        {
          processingLabel = true;
          LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
          labelLayout = new LinearLayout(context);
          labelLayout.setLayoutParams(params);
          labelLayout.setOrientation(LinearLayout.VERTICAL);

          labelLayout.addView(childView);
          nonButtonLayout.addView(labelLayout);
        }
        else if (isFieldWithType(child, Constants.C_FIELD_SUBLABEL) && processingLabel)
        {
          // add this child below the previous child (the label in the if-block above)
          labelLayout.addView(childView);

          processingLabel = false;
        }
        else
        {
          LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT, 1);
          childView.setLayoutParams(params);
          nonButtonLayout.addView(childView);
        }
      }
      else
      {
        childView.setId(childID);

        childParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;

        styleHandler.styleRowButton(childView, childParams);

        if (previousButton == -1)
        {
          styleHandler.styleRowButtonAligment(child, childParams);
        }
        else
        {
          childParams.addRule(RelativeLayout.LEFT_OF, previousButton);

          if (nonButtonLayout != null && children.lastIndexOf(child) == children.size() - 1)
          {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nonButtonLayout.getLayoutParams();
            layoutParams.addRule(RelativeLayout.LEFT_OF, childID);
          }
        }

        previousButton = childID;

        childView.setLayoutParams(childParams);
        parent.addView(childView);
      }
    }

    if (nonButtonLayout != null)
    {
      parent.addView(nonButtonLayout);
    }

  }

}
