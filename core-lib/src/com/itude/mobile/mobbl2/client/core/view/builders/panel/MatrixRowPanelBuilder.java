package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;

// TODO: this class is idiotic; refactor
public class MatrixRowPanelBuilder extends MBBasePanelBuilder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, BuildState buildState)
  {
    buildState.increaseMatrixRow();

    final Context context = MBApplicationController.getInstance().getBaseContext();

    MBStyleHandler styleHandler = getStyleHandler();

    // the parent of all widgets in this row
    RelativeLayout rowPanel = new RelativeLayout(context);
    rowPanel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    rowPanel.setTag(Constants.C_MATRIXROW);

    List<MBComponent> matrixRowLabels = new ArrayList<MBComponent>();
    List<MBField> matrixRowTitles = new ArrayList<MBField>();
    List<MBField> matrixRowDescription = new ArrayList<MBField>();

    groupChildren(panel, matrixRowLabels, matrixRowTitles, matrixRowDescription);

    View prev = null;

    RelativeLayout.LayoutParams linearContainerParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT);
    linearContainerParams.addRule(RelativeLayout.CENTER_VERTICAL);

    LinearLayout linearContainer = new LinearLayout(context);
    linearContainer.setOrientation(LinearLayout.VERTICAL);
    linearContainer.setLayoutParams(linearContainerParams);

    rowPanel.addView(linearContainer);

    prev = buildMatrixRowPanelHeader(panel, linearContainer, matrixRowTitles);
    prev = buildMatrixRowPanelLabels(panel, linearContainer, matrixRowLabels, prev);
    buildMatrixRowPanelLabels(panel, linearContainer, matrixRowDescription, prev);

    boolean isClickable = false;
    if (panel.getOutcomeName() != null)
    {
      isClickable = true;

      rowPanel.setClickable(true);
      rowPanel.setFocusable(true);
      rowPanel.setOnClickListener(panel);
    }

    String rowStyle;
    if (panel.getStyle() != null)
    {
      rowStyle = panel.getStyle();
    }
    else if (matrixRowTitles.size() > 0 && matrixRowLabels.size() > 0)
    {
      rowStyle = Constants.C_STYLE_DOUBLE_LINED_MATRIX_ROW;
    }
    else
    {
      rowStyle = Constants.C_STYLE_SINGLE_LINED_MATRIX_ROW;
    }

    styleHandler.styleMatrixRowPanel(panel, rowPanel, isClickable, rowStyle, buildState.getMatrixRow());
    return rowPanel;
  }

  private void groupChildren(MBPanel panel, List<MBComponent> matrixRowLabels, List<MBField> matrixRowTitles,
                             List<MBField> matrixRowDescription)
  {
    List<MBComponent> children = panel.getChildren();
    for (MBComponent mbComponent : children)
    {
      if (mbComponent instanceof MBField)
      {
        MBField field = (MBField) mbComponent;
        if (!field.isHidden())
        {
          if (Constants.C_FIELD_MATRIXTITLE.equals(field.getType()))
          {
            matrixRowTitles.add(field);
          }
          else if (Constants.C_FIELD_MATRIXDESCRIPTION.equals(field.getType()))
          {
            matrixRowDescription.add(field);
          }
          else
          {
            matrixRowLabels.add(mbComponent);
          }
        }
      }
      else
      {
        matrixRowLabels.add(mbComponent);
      }
    }
  }

  private View buildMatrixRowPanelLabels(MBPanel panel, ViewGroup rowPanel, List<? extends MBComponent> matrixRowLabels, View headers)
  {
    // Row with labels
    if (matrixRowLabels.isEmpty())
    {
      return headers;
    }
    RelativeLayout.LayoutParams rowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    if (headers != null)
    {
      rowParams.addRule(RelativeLayout.BELOW, headers.getId());
    }

    LinearLayout row = new LinearLayout(rowPanel.getContext());
    row.setLayoutParams(rowParams);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER_VERTICAL);

    buildChildren(matrixRowLabels, row);

    getStyleHandler().styleMatrixRow(panel, row);

    rowPanel.addView(row);
    int id = UniqueIntegerGenerator.getId();
    row.setId(id);

    return row;
  }

  private View buildMatrixRowPanelHeader(MBPanel panel, ViewGroup rowPanel, List<MBField> matrixRowTitles)
  {
    View result;
    if (matrixRowTitles.isEmpty()) return null;
    if (matrixRowTitles.size() > 1)
    {
      LinearLayout rowHeaderLabel = new LinearLayout(rowPanel.getContext());
      getStyleHandler().styleMatrixRow(panel, rowHeaderLabel);
      rowHeaderLabel.setOrientation(LinearLayout.HORIZONTAL);
      buildChildren(matrixRowTitles, rowHeaderLabel);
      rowPanel.addView(rowHeaderLabel);

      result = rowHeaderLabel;
    }
    else
    {
      buildChildren(matrixRowTitles, rowPanel);
      // get the last child added to the rowpanel (this is our one and only label)
      View current = rowPanel.getChildAt(rowPanel.getChildCount() - 1);
      current.setPadding(MBScreenUtilities.FOUR, MBScreenUtilities.TWO, MBScreenUtilities.FOUR, MBScreenUtilities.TWO);

      result = current;

    }

    int id = UniqueIntegerGenerator.getId();
    result.setId(id);

    return result;
  }

}
