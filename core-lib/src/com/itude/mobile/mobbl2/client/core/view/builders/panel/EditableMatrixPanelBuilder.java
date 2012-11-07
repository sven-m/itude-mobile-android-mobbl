package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.components.MBEditableMatrix;

public class EditableMatrixPanelBuilder extends MBViewBuilder implements Builder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, MBViewState viewState, BuildState buildState)
  {
  buildState.resetMatrixRow();

    Context context = MBApplicationController.getInstance().getBaseContext();

    // An EditableMatrix without any permissions equals a default Matrix
    if (!panel.isChildrenDeletable() && !panel.isChildrenDraggable() && !panel.isChildrenSelectable() && !panel.isChildrenLongClickable())
    {
      Builder matrixBuilder = MBViewBuilderFactory.getInstance().getPanelViewBuilder().getBuilderForType(Constants.C_MATRIX);
      return matrixBuilder.buildPanel(panel, viewState, buildState);
    }

    boolean initialiseInEditMode = false;

    if (panel.getMode().equals(Constants.C_EDITABLEMATRIX_MODE_EDITONLY) || panel.getMode().equals(Constants.C_EDITABLEMATRIX_MODE_EDIT))
    {
      // No buttons should be shown in the header of the matrix
      initialiseInEditMode = true;
    }

    // An edit button should be shown in the header of the matrix
    // MATRIX-ROW components should be shown as they would usually do
    MBEditableMatrix result = new MBEditableMatrix(context, initialiseInEditMode, panel);
    result.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    result.setOrientation(LinearLayout.VERTICAL);

    buildChildrenForEditableMatrix(panel.getChildren(), result.getCurrentContentView(), viewState);

    if (panel.getOutcomeName() != null && panel.getOutcomeName().length() > 0)
    {
      result.setOnClickListener(panel);
    }

    getStyleHandler().styleMatrixContainer(panel, result);

    result.connectMatrixListener();

    return result;

  }

  public static void buildChildrenForEditableMatrix(List<? extends MBComponent> children, ViewGroup parent, MBViewManager.MBViewState viewState)
  {

    int previousSiblingId = -1;

    for (MBComponent child : children)
    {

      MBViewBuilderFactory.getInstance().getStyleHandler().applyInsetsForComponent(child);

      View childView = child.buildViewWithMaxBounds(viewState);

      if (childView == null)
      {
        continue;
      }

      int childId = UniqueIntegerGenerator.getId();
      childView.setId(childId);

      if (previousSiblingId > -1)
      {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, previousSiblingId);
        childView.setLayoutParams(params);
      }
      else
      {
        // If first item is not an empty matrix header set the layoutparams 
        if (!(child instanceof MBPanel && ((MBPanel) child).getType() != null
              && ((MBPanel) child).getType().equals(Constants.C_MATRIXHEADER) && ((MBPanel) child).getChildren().size() <= 0))
        {
          LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
          childView.setLayoutParams(params);
        }
      }

      parent.addView(childView);

      previousSiblingId = childId;

    }

  }

}
