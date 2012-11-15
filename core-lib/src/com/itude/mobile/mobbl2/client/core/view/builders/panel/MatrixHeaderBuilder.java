package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MatrixHeaderBuilder extends MBBasePanelBuilder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, BuildState buildState)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();

    RelativeLayout headerPanelContainer = new RelativeLayout(context);
    headerPanelContainer.setTag(Constants.C_MATRIXHEADER_CONTAINER);
    headerPanelContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

    RelativeLayout.LayoutParams headerPanelParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    headerPanelParams.addRule(RelativeLayout.CENTER_VERTICAL);
    LinearLayout headerPanel = new LinearLayout(context);
    headerPanel.setTag(Constants.C_MATRIXHEADER);
    headerPanel.setLayoutParams(headerPanelParams);
    headerPanel.setOrientation(LinearLayout.VERTICAL);

    ArrayList<MBComponent> children = panel.getChildren();
    ArrayList<MBComponent> matrixLabels = new ArrayList<MBComponent>();
    ArrayList<MBComponent> matrixTitles = new ArrayList<MBComponent>();

    for (Iterator<MBComponent> iterator = children.iterator(); iterator.hasNext();)
    {
      MBComponent mbComponent = iterator.next();
      if (mbComponent instanceof MBField)
      {
        MBField field = (MBField) mbComponent;
        if (!field.isHidden())
        {
          if (Constants.C_FIELD_MATRIXTITLE.equals(field.getType()))
          {
            matrixTitles.add(mbComponent);
          }
          else
          {
            if (field.getStyle() == null)
            {
              field.setStyle(Constants.C_FIELD_STYLE_MATRIXCOLUMN);
            }
            matrixLabels.add(mbComponent);
          }
        }

      }
      else
      {
        matrixLabels.add(mbComponent);
      }
    }

    MBStyleHandler styleHandler = getStyleHandler();
    if (matrixTitles.isEmpty() && matrixLabels.isEmpty())
    {
      // use the stylehandler for the divider to let the header
      // act as a top divider
      headerPanel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
      styleHandler.styleDivider(headerPanel);
      return headerPanel;
    }
    else
    {
      styleHandler.styleMatrixHeader(headerPanel);
    }

    //Header
    if (!matrixTitles.isEmpty())
    {
      RelativeLayout headerLabel = new RelativeLayout(context);
      headerLabel.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT));
      headerLabel.setTag(Constants.C_MATRIXTITLEROW);
      styleHandler.styleMatrixHeaderTitleRow(panel, headerLabel);

      buildChildrenForMatrixHeader(matrixTitles, headerLabel);

      headerPanel.addView(headerLabel);
    }

    // Row with labels
    if (!matrixLabels.isEmpty())
    {
      LinearLayout headerRow = new LinearLayout(context);
      headerRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
      headerRow.setOrientation(LinearLayout.HORIZONTAL);
      headerRow.setGravity(Gravity.CENTER_VERTICAL);

      styleHandler.styleMatrixHeaderLabelRow(headerRow);

      buildChildren(matrixLabels, headerRow);
      headerPanel.addView(headerRow);
    }

    headerPanelContainer.addView(headerPanel);

    panel.attachView(headerPanelContainer);

    return headerPanelContainer;
  }

  private void buildChildrenForMatrixHeader(List<? extends MBComponent> children, ViewGroup parent)
  {
    for (MBComponent child : children)
    {
      View childView = child.buildView();
      if (childView == null) continue;

      RelativeLayout.LayoutParams childViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      childViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
      childView.setLayoutParams(childViewParams);

      parent.addView(childView);
    }
  }

  @Override
  protected void buildChildren(List<? extends MBComponent> matrixRowLabels, ViewGroup parent)
  {

    boolean needsToProcessFirstLabel = true;
    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    for (MBComponent child : matrixRowLabels)
      if (child instanceof MBField)
      {

        View childView = child.buildView();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
        childView.setLayoutParams(params);
        styleHandler.styleMatrixHeaderRowChild(childView, (MBField) child, needsToProcessFirstLabel);

        // Add cell to matrix row
        parent.addView(childView);

        // First Matrix-Cell should be aligned to the left, next ones should be centered if no alignment property was set
        if (isFieldWithType(child, Constants.C_FIELD_MATRIXCELL) || (isFieldWithType(child, Constants.C_FIELD_LABEL)))
        {
          if (needsToProcessFirstLabel)
          {
            needsToProcessFirstLabel = false;
            continue;
          }
          else if (((MBField) child).getAlignment() == null)
          {
            ((TextView) childView).setGravity(Gravity.CENTER_HORIZONTAL);
          }
        }
      }

  }

}
