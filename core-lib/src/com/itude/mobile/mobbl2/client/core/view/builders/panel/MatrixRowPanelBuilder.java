package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.Builder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

// TODO: this class is idiotic; refactor
public class MatrixRowPanelBuilder extends MBViewBuilder implements Builder
{
  
  @Override
  public ViewGroup buildPanel(MBPanel panel, MBViewState viewState, BuildState buildState)
  {
buildState.increaseMatrixRow()
;

    if (panel.getParent() != null && panel.getParent() instanceof MBPanel)
    {
      // Determine whether to build a default matrix row panel or a matrix row panel in edit mode
      MBPanel parent = (MBPanel) panel.getParent();
      if (parent.getType().equals(Constants.C_EDITABLEMATRIX)
          && parent.getMode() != null
          && (parent.getMode().equals(Constants.C_EDITABLEMATRIX_MODE_EDIT) || parent.getMode()
              .equals(Constants.C_EDITABLEMATRIX_MODE_EDITONLY)))
      {
        return buildEditableMatrixRowPanel(panel, buildState);
      }
    }

    return buildReadOnlyMatrixRowPanel(panel, buildState);
  }
  

  private ViewGroup buildReadOnlyMatrixRowPanel(MBPanel panel, BuildState buildState)
  {
    final Context context = MBApplicationController.getInstance().getBaseContext();

    boolean isClickable = false;

    MBStyleHandler styleHandler = getStyleHandler();

    // the parent of all widgets in this row
    RelativeLayout rowPanel = new RelativeLayout(context);
    rowPanel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    ArrayList<MBComponent> children = panel.getChildren();
    ArrayList<MBComponent> matrixRowLabels = new ArrayList<MBComponent>();
    ArrayList<MBComponent> matrixRowTitles = new ArrayList<MBComponent>(); // typically just one
    ArrayList<MBComponent> matrixRowDescription = new ArrayList<MBComponent>(); // typically just one

    for (MBComponent mbComponent : children)
    {
      if (mbComponent instanceof MBField)
      {
        MBField field = (MBField) mbComponent;
        if (!field.isHidden())
        {
          if (Constants.C_FIELD_MATRIXTITLE.equals(field.getType()))
          {
            matrixRowTitles.add(mbComponent);
          }
          else if (Constants.C_FIELD_MATRIXDESCRIPTION.equals(field.getType()))
          {
            matrixRowDescription.add(mbComponent);
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

    // -1: nothing added yet (so use rowPanel as parent)
    int currentId = -1;

    RelativeLayout.LayoutParams linearContainerParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    linearContainerParams.addRule(RelativeLayout.CENTER_VERTICAL);

    LinearLayout linearContainer = new LinearLayout(context);
    linearContainer.setOrientation(LinearLayout.VERTICAL);
    linearContainer.setLayoutParams(linearContainerParams);

    rowPanel.addView(linearContainer);

    if (matrixRowTitles.size() > 0)
    {
      currentId = buildMatrixRowPanelHeader(panel, linearContainer, matrixRowTitles, currentId);
    }

    if (matrixRowLabels.size() > 0)
    {
      currentId = buildMatrixRowPanelLabels(panel, linearContainer, matrixRowLabels, currentId);
    }

    if (matrixRowDescription.size() > 0)
    {
      buildMatrixRowPanelLabels(panel, linearContainer, matrixRowDescription, currentId);
    }

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

  

  private int buildMatrixRowPanelLabels(MBPanel panel, ViewGroup rowPanel, ArrayList<MBComponent> matrixRowLabels, int currentId)
  {
    // Row with labels
    if (matrixRowLabels.isEmpty())
    {
      return currentId;
    }
    RelativeLayout.LayoutParams rowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    if (currentId != -1)
    {
      rowParams.addRule(RelativeLayout.BELOW, currentId);
    }

    LinearLayout row = new LinearLayout(rowPanel.getContext());
    row.setLayoutParams(rowParams);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER_VERTICAL);

    // Add children to panel
    buildMatrixRowPanelChildren(matrixRowLabels, row, false);

    getStyleHandler().styleMatrixRow(panel, row);

    rowPanel.addView(row);
    currentId = UniqueIntegerGenerator.getId();
    row.setId(currentId);

    return currentId;
  }


  private int buildMatrixRowPanelHeader(MBPanel panel, ViewGroup rowPanel, ArrayList<MBComponent> matrixRowTitles, int currentId)
  {

    if (matrixRowTitles.isEmpty()) return currentId;
    if (matrixRowTitles.size() > 1)
    {
      LinearLayout rowHeaderLabel = new LinearLayout(rowPanel.getContext());
      getStyleHandler().styleMatrixRow(panel, rowHeaderLabel);
      rowHeaderLabel.setOrientation(LinearLayout.HORIZONTAL);
      buildChildren(matrixRowTitles, rowHeaderLabel, null);
      rowPanel.addView(rowHeaderLabel);

      currentId = UniqueIntegerGenerator.getId();
      rowHeaderLabel.setId(currentId);
    }
    else
    {
      buildChildren(matrixRowTitles, rowPanel, null);
      // get the last child added to the rowpanel (this is our one and only label)
      View current = rowPanel.getChildAt(rowPanel.getChildCount() - 1);
      current.setPadding(MBScreenUtilities.FOUR, MBScreenUtilities.TWO, MBScreenUtilities.FOUR, MBScreenUtilities.TWO);
      currentId = UniqueIntegerGenerator.getId();
      current.setId(currentId);

    }

    return currentId;
  }

  

  private ViewGroup buildEditableMatrixRowPanel(MBPanel panel, BuildState buildState)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();
    MBStyleHandler styleHandler = getStyleHandler();

    RelativeLayout borderWrapper = new RelativeLayout(context);
    borderWrapper.setTag(Constants.C_MATRIXROW);
    borderWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

    RelativeLayout.LayoutParams relativeContainerParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);
    relativeContainerParams.addRule(RelativeLayout.CENTER_VERTICAL);
    RelativeLayout relativeContainer = new RelativeLayout(context);
    relativeContainer.setLayoutParams(relativeContainerParams);
    relativeContainer.setId(UniqueIntegerGenerator.getId());

    // Add deletebutton or checkbutton to container in relativelayout
    // Row content will be on the right side of one of these widgets
    RelativeLayout.LayoutParams leftButtonContainerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    leftButtonContainerParams.addRule(RelativeLayout.CENTER_VERTICAL);
    LinearLayout leftButtonContainer = new LinearLayout(context);
    leftButtonContainer.setId(UniqueIntegerGenerator.getId());
    leftButtonContainer.setOrientation(LinearLayout.HORIZONTAL);
    leftButtonContainer.setLayoutParams(leftButtonContainerParams);
    leftButtonContainer.setTag(Constants.C_EDITABLEMATRIX_LEFTBUTTONSCONTAINER);

    if (((MBPanel) panel.getParent()).isChildrenSelectable())
    {
      CheckBox checkBox = new CheckBox(context);
      checkBox.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      checkBox.setTag(Constants.C_EDITABLEMATRIX_CHECKBOX);
      leftButtonContainer.addView(checkBox);
    }
    if (((MBPanel) panel.getParent()).isChildrenDeletable())
    {
      Button deleteButton = new Button(context);
      deleteButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      deleteButton.setTag(Constants.C_EDITABLEMATRIX_DELETEBUTTON);
      leftButtonContainer.addView(deleteButton);
      styleHandler.styleButtonWithName(deleteButton, Constants.C_EDITABLEMATRIX_DELETEBUTTON);
    }

    relativeContainer.addView(leftButtonContainer);
    //

    // Add draggable buttons to container in relativelayout if draggable is allowed
    // Row content will be on the right side of one of these widgets

    RelativeLayout.LayoutParams rightButtonContainerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    rightButtonContainerParams.addRule(RelativeLayout.CENTER_VERTICAL);
    rightButtonContainerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    LinearLayout rightButtonContainer = new LinearLayout(context);
    rightButtonContainer.setId(UniqueIntegerGenerator.getId());
    rightButtonContainer.setOrientation(LinearLayout.HORIZONTAL);
    rightButtonContainer.setLayoutParams(rightButtonContainerParams);
    rightButtonContainer.setTag(Constants.C_EDITABLEMATRIX_RIGHTBUTTONSCONTAINER);

    if (((MBPanel) panel.getParent()).isChildrenDraggable())
    {

      ImageButton upButton = new ImageButton(context);
      upButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      upButton.setTag(Constants.C_EDITABLEMATRIX_UPBUTTON);
      rightButtonContainer.addView(upButton);
      styleHandler.styleImageButtonWithName(upButton, Constants.C_EDITABLEMATRIX_UPBUTTON);

      ImageButton downButton = new ImageButton(context);
      downButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      downButton.setTag(Constants.C_EDITABLEMATRIX_DOWNBUTTON);
      rightButtonContainer.addView(downButton);
      styleHandler.styleImageButtonWithName(downButton, Constants.C_EDITABLEMATRIX_DOWNBUTTON);
    }

    relativeContainer.addView(rightButtonContainer);
    //

    // Row panel
    RelativeLayout.LayoutParams rowPanelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
        RelativeLayout.LayoutParams.FILL_PARENT);
    rowPanelParams.addRule(RelativeLayout.RIGHT_OF, leftButtonContainer.getId());
    rowPanelParams.addRule(RelativeLayout.LEFT_OF, rightButtonContainer.getId());
    rowPanelParams.addRule(RelativeLayout.CENTER_VERTICAL);
    LinearLayout rowPanel = new LinearLayout(context);
    rowPanel.setId(UniqueIntegerGenerator.getId());
    rowPanel.setLayoutParams(rowPanelParams);
    rowPanel.setOrientation(LinearLayout.VERTICAL);

    ArrayList<MBComponent> children = panel.getChildren();
    ArrayList<MBComponent> matrixRowLabels = new ArrayList<MBComponent>();
    ArrayList<MBComponent> matrixRowTitles = new ArrayList<MBComponent>();

    for (Iterator<MBComponent> iterator = children.iterator(); iterator.hasNext();)
    {
      MBComponent mbComponent = iterator.next();
      if (mbComponent instanceof MBField)
      {
        MBField field = (MBField) mbComponent;
        if (Constants.C_FIELD_MATRIXTITLE.equals(field.getType()))
        {
          matrixRowTitles.add(mbComponent);
        }
        else
        {
          matrixRowLabels.add(mbComponent);
        }
      }
      else
      {
        matrixRowLabels.add(mbComponent);
      }
    }

    int currentId = -1;

    //Header
    if (!matrixRowTitles.isEmpty())
    {
      currentId = buildMatrixRowPanelHeader(panel, rowPanel, matrixRowTitles, currentId);
    }

    // Row with labels
    if (!matrixRowLabels.isEmpty())
    {
      buildMatrixRowPanelLabels(panel, rowPanel, matrixRowLabels, currentId);
    }

    relativeContainer.addView(rowPanel);
    //

    boolean isClickable = false;
    if (((MBPanel) panel.getParent()).isChildrenClickable())
    {
      isClickable = true;

      borderWrapper.setClickable(true);
      borderWrapper.setFocusable(true);
    }

    borderWrapper.addView(relativeContainer);

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

    styleHandler.styleMatrixRowPanel(panel, borderWrapper, isClickable, rowStyle, buildState.getMatrixRow());
    return borderWrapper;
  }


  static void buildMatrixRowPanelChildren(ArrayList<MBComponent> matrixRowLabels, ViewGroup parent, boolean buildingHeaderPanelChildren)
  {

    boolean needsToProcessFirstLabel = true;
    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    for (MBComponent child : matrixRowLabels)
    {

      View childView = child.buildViewWithMaxBounds(null);

      // TODO buildViewWithMaxBounds should never return null
      if (childView == null)
      {
        continue;
      }

      // If header items are added we need to change their layout parameters to stretch the whole width of the header
      if (buildingHeaderPanelChildren)
      {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);
        childView.setLayoutParams(params);
        styleHandler.styleMatrixHeaderRowChild(childView, (MBField) child, needsToProcessFirstLabel);
      }

      // Add cell to matrix row
      parent.addView(childView);

      // First Matrix-Cell should be aligned to the left, next ones should be centered if no alignment property was set
      if (isComponentOfType(child, Constants.C_FIELD_MATRIXCELL) || (isComponentOfType(child, Constants.C_FIELD_LABEL)))
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
