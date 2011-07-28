package com.itude.mobile.mobbl2.client.core.view.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBDevice;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBComponentContainer;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.components.MBEditableMatrix;

public class MBPanelViewBuilder extends MBViewBuilder
{

  private boolean _isFirstRow = true;

  public ViewGroup buildPanelView(MBPanel panel, MBViewManager.MBViewState viewState)
  {
    ViewGroup view;

    String panelType = panel.getType();
    if (Constants.C_PLAIN.equals(panelType))
    {
      view = buildBasicPanel(panel, viewState);
    }
    else if (Constants.C_LIST.equals(panelType))
    {
      view = buildListPanel(panel);
    }
    else if (Constants.C_SECTION.equals(panelType))
    {
      view = buildSectionPanel(panel);
    }
    else if (Constants.C_ROW.equals(panelType))
    {
      view = buildRowPanel(panel);
    }
    else if (Constants.C_MATRIX.equals(panelType))
    {
      view = buildMatrix(panel, null);
    }
    else if (Constants.C_MATRIXHEADER.equals(panelType))
    {
      view = buildMatrixHeaderPanel(panel);
    }
    else if (Constants.C_MATRIXROW.equals(panelType))
    {
      view = buildMatrixRowPanel(panel);
    }
    else if (Constants.C_EDITABLEMATRIX.equals(panelType))
    {
      view = buildEditableMatrix(panel, viewState);
    }
    else
    {
      // Build a non scrolling basic panel
      view = buildBasicPanel(panel, viewState);
    }
    getStyleHandler().applyStyle(panel, view, viewState);
    return view;

  }

  public ViewGroup buildMatrix(MBPanel panel, MBViewState viewState)
  {
    LinearLayout result = new LinearLayout(MBApplicationController.getInstance().getBaseContext());
    result.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    result.setOrientation(LinearLayout.VERTICAL);
    buildChildren(panel.getChildren(), result, viewState);

    getStyleHandler().styleMatrixContainer(result);

    if (panel.getOutcomeName() != null && panel.getOutcomeName().length() > 0)
    {
      result.setOnClickListener(panel);
    }

    return result;
  }

  public ViewGroup buildEditableMatrix(MBPanel panel, MBViewState viewState)
  {

    Context context = MBApplicationController.getInstance().getBaseContext();

    // An EditableMatrix without any permissions equals a default Matrix
    if (!panel.isChildrenDeletable() && !panel.isChildrenDraggable() && !panel.isChildrenSelectable() && !panel.isChildrenLongClickable())
    {
      return buildMatrix(panel, viewState);
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
    //    buildChildren(panel.getChildren(), result.getCurrentContentView(), bounds, viewState);

    buildChildrenForEditableMatrix(panel.getChildren(), result.getCurrentContentView(), viewState);

    if (panel.getOutcomeName() != null && panel.getOutcomeName().length() > 0)
    {
      result.setOnClickListener(panel);
    }

    getStyleHandler().styleMatrixContainer(result);

    result.connectMatrixListener();

    return result;

  }

  public void buildChildrenForEditableMatrix(List<? extends MBComponent> children, ViewGroup parent, MBViewManager.MBViewState viewState)
  {

    int previousSiblingId = -1;

    for (MBComponent child : children)
    {

      getStyleHandler().applyInsetsForComponent(child);

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

  private LinearLayout buildBasicPanel(LinearLayout viewToFill, MBPanel panel, MBViewState viewState)
  {
    final Context context = MBApplicationController.getInstance().getBaseContext();

    getStyleHandler().styleBasicPanelHeader(viewToFill);
    if (panel.getTitle() != null)
    {
      TextView title = new TextView(context);
      title.setText(panel.getTitle());
      viewToFill.addView(title);
      getStyleHandler().styleBasicPanelHeaderText(title);
    }
    buildChildren(panel.getChildren(), viewToFill, viewState);
    return viewToFill;
  }

  private LinearLayout buildBasicPanel(MBPanel panel, MBViewState viewState)
  {
    LinearLayout result = new LinearLayout(MBApplicationController.getInstance().getBaseContext());

    return buildBasicPanel(result, panel, viewState);
  }

  public ViewGroup buildListPanel(MBPanel panel)
  {
    // Make sure the first row added in this list has a top border
    _isFirstRow = true;

    LinearLayout panelView = buildBasicPanel(panel, null);
    panelView.setOrientation(LinearLayout.VERTICAL);

    // Only add padding if this list isn't a direct child of a section
    MBComponentContainer parent = panel.getParent();
    String panelType;
    if (!(parent != null && parent instanceof MBPanel && (panelType = ((MBPanel) parent).getType()) != null && panelType
        .equals(Constants.C_SECTION)))
    {
      getStyleHandler().styleListPanelContainer(panelView);
    }

    return panelView;
  }

  private ViewGroup buildSectionPanel(MBPanel panel)
  {
    boolean hasTitle = false;
    Context context = MBApplicationController.getInstance().getBaseContext();

    LinearLayout panelView = new LinearLayout(MBApplicationController.getInstance().getBaseContext());
    panelView.setOrientation(LinearLayout.VERTICAL);

    if (panel.getTitle() != null)
    {
      // Show header at top of the section
      hasTitle = true;

      LinearLayout header = new LinearLayout(panelView.getContext());
      header.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      header.setOrientation(LinearLayout.VERTICAL);

      TextView title = new TextView(header.getContext());
      title.setText(panel.getTitle());

      getStyleHandler().styleSectionHeaderText(title);

      header.addView(title);

      getStyleHandler().styleSectionHeader(header);

      panelView.addView(header);
    }
    else
    {
      // Make sure that at least a top border is visible
      LinearLayout topBorder = new LinearLayout(context);
      topBorder.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
      getStyleHandler().styleDivider(topBorder);

      panelView.addView(topBorder);
    }

    // Section takes care of top border. First row doesn't need to.
    _isFirstRow = false;

    buildChildren(panel.getChildren(), panelView, null);

    getStyleHandler().styleSectionContainer(panelView, hasTitle);
    return panelView;
  }

  @SuppressWarnings("unchecked")
  private ViewGroup buildRowPanel(MBPanel panel)
  {

    final Context context = MBApplicationController.getInstance().getBaseContext();
    final MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();
    HashMap<String, Object> childIds = new HashMap<String, Object>();

    RelativeLayout rowPanel = new RelativeLayout(context);
    rowPanel.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    rowPanel.setTag(childIds);

    int topBorderID = -1;
    if (_isFirstRow)
    {
      // Add top border if this is the first row

      // Left Border
      RelativeLayout.LayoutParams topBorderParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 1);

      LinearLayout topBorder = new LinearLayout(context);
      topBorderID = UniqueIntegerGenerator.getId();
      topBorder.setId(topBorderID);
      childIds.put("BorderTopID", topBorderID);
      topBorder.setLayoutParams(topBorderParams);
      topBorder.setTag("topBorder");
      styleHandler.styleDivider(topBorder);
      rowPanel.addView(topBorder);
    }

    // Left Border
    RelativeLayout.LayoutParams leftBorderParams = new RelativeLayout.LayoutParams(1, RelativeLayout.LayoutParams.FILL_PARENT);
    leftBorderParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    if (topBorderID != -1)
    {
      leftBorderParams.addRule(RelativeLayout.BELOW, topBorderID);
    }

    LinearLayout leftBorder = new LinearLayout(context);
    int leftBorderId = UniqueIntegerGenerator.getId();
    leftBorder.setId(leftBorderId);
    childIds.put("BorderLeftID", leftBorderId);
    leftBorder.setLayoutParams(leftBorderParams);
    leftBorder.setTag("leftBorder");
    styleHandler.styleDivider(leftBorder);
    rowPanel.addView(leftBorder);
    //

    // Right Border
    RelativeLayout.LayoutParams rightBorderParams = new RelativeLayout.LayoutParams(1, RelativeLayout.LayoutParams.FILL_PARENT);
    rightBorderParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    if (topBorderID != -1)
    {
      leftBorderParams.addRule(RelativeLayout.BELOW, topBorderID);
    }

    LinearLayout rightBorder = new LinearLayout(context);
    int rightBorderId = UniqueIntegerGenerator.getId();
    rightBorder.setId(rightBorderId);
    childIds.put("BorderRightID", rightBorderId);
    rightBorder.setLayoutParams(rightBorderParams);
    rightBorder.setTag("rightBorder");
    styleHandler.styleDivider(rightBorder);
    rowPanel.addView(rightBorder);
    //

    // Content view
    buildChildrenForRowPanel(panel.getChildren(), rowPanel, null);

    // Bottom and (possible) top border params
    RelativeLayout.LayoutParams bottomBorderParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 1);
    for (Integer id : (List<Integer>) childIds.get("RowChildIDs"))
    {
      if (topBorderID != -1)
      {
        ((RelativeLayout.LayoutParams) rowPanel.findViewById(id).getLayoutParams()).addRule(RelativeLayout.BELOW, id);
      }

      bottomBorderParams.addRule(RelativeLayout.BELOW, id);
    }
    //

    LinearLayout bottomBorder = new LinearLayout(context);
    int bottomBorderId = UniqueIntegerGenerator.getId();
    bottomBorder.setId(bottomBorderId);
    childIds.put("BorderBottomID", bottomBorderId);
    bottomBorder.setLayoutParams(bottomBorderParams);
    styleHandler.styleDivider(bottomBorder);

    rowPanel.addView(bottomBorder);
    //

    // Make sure left and right borders are above bottom border
    leftBorderParams.addRule(RelativeLayout.ABOVE, bottomBorderId);
    rightBorderParams.addRule(RelativeLayout.ABOVE, bottomBorderId);
    //

    //    styleHandler.styleRow(rowView);

    // Arrow and clickable style of row
    if (panel.getOutcomeName() != null)
    {
      RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      arrowParams.addRule(RelativeLayout.CENTER_VERTICAL);

      ImageView arrow = new ImageView(rowPanel.getContext());
      arrow.setLayoutParams(arrowParams);
      arrow.setMinimumWidth(MBScreenUtilities.FORTY);
      arrow.setImageDrawable(MBResourceService.getInstance().getImageByID(Constants.C_ARROW));

      rowPanel.addView(arrow);

      rowPanel.setClickable(true);
      rowPanel.setFocusable(true);
      rowPanel.setOnClickListener(panel);

      getStyleHandler().styleClickableRow(rowPanel);
    }

    // Processed the row so the next one isn't the first one anymore
    _isFirstRow = false;

    return rowPanel;
  }

  @SuppressWarnings("unchecked")
  private void buildChildrenForRowPanel(List<? extends MBComponent> children, ViewGroup parent, MBViewManager.MBViewState viewState)
  {
    final Context context = parent.getContext();
    final MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    Integer leftBorderID = -1;
    Integer rightBorderID = -1;
    HashMap<String, Object> childIds = (HashMap<String, Object>) parent.getTag();

    if (childIds.containsKey("BorderLeftID"))
    {
      leftBorderID = (Integer) childIds.get("BorderLeftID");
    }
    if (childIds.containsKey("BorderRightID"))
    {
      rightBorderID = (Integer) childIds.get("BorderRightID");
    }

    List<Integer> rowChildIDs = new ArrayList<Integer>();
    childIds.put("RowChildIDs", rowChildIDs);
    int previousButton = -1;

    LinearLayout nonButtonLayout = null;
    boolean processingLabel = false;
    LinearLayout labelLayout = null;

    for (MBComponent child : children)
    {
      View childView = child.buildViewWithMaxBounds(viewState);
      if (childView == null) continue;

      int childID = UniqueIntegerGenerator.getId();

      RelativeLayout.LayoutParams childParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      childParams.addRule(RelativeLayout.CENTER_VERTICAL);

      if (!isFieldWithType(child, Constants.C_FIELD_BUTTON))
      {
        if (nonButtonLayout == null)
        {
          RelativeLayout.LayoutParams nonButtonLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);

          if (leftBorderID != -1)
          {
            nonButtonLayoutParams.addRule(RelativeLayout.RIGHT_OF, leftBorderID);
          }
          if (rightBorderID != -1)
          {
            nonButtonLayoutParams.addRule(RelativeLayout.LEFT_OF, rightBorderID);
          }

          nonButtonLayout = new LinearLayout(context);
          nonButtonLayout.setLayoutParams(nonButtonLayoutParams);

          rowChildIDs.add(childID);
          nonButtonLayout.setId(childID);
          styleHandler.styleRow(nonButtonLayout);
        }

        if (isFieldWithType(child, Constants.C_FIELD_LABEL) && !processingLabel)
        {
          processingLabel = true;
          LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);
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
          LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT, 1);
          childView.setLayoutParams(params);
          nonButtonLayout.addView(childView);
        }
      }
      else
      {
        // Right alignment positioned left of possible previously added buttons or the rightborder

        rowChildIDs.add(childID);
        childView.setId(childID);

        styleHandler.styleRowButton(childParams);

        childParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;

        if (previousButton == -1)
        {
          if (rightBorderID != -1)
          {
            childParams.addRule(RelativeLayout.LEFT_OF, rightBorderID);
          }
          else
          {
            childParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
          }
        }
        else
        {
          childParams.addRule(RelativeLayout.LEFT_OF, previousButton);
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

  private boolean isFieldWithType(MBComponent child, String type)
  {
    return child instanceof MBField && ((MBField) child).getType() != null && ((MBField) child).getType().equals(type);
  }

  private ViewGroup buildMatrixHeaderPanel(MBPanel panel)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();

    LinearLayout headerPanel = new LinearLayout(context);
    headerPanel.setTag(Constants.C_MATRIXHEADER);
    headerPanel.setOrientation(LinearLayout.VERTICAL);

    ArrayList<MBComponent> children = panel.getChildren();
    ArrayList<MBComponent> matrixLabels = new ArrayList<MBComponent>();
    ArrayList<MBComponent> matrixTitle = new ArrayList<MBComponent>();

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
            matrixTitle.add(mbComponent);
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

    if (matrixTitle.isEmpty() && matrixLabels.isEmpty())
    {
      // use the stylehandler for the divider to let the header
      // act as a top divider
      headerPanel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
      getStyleHandler().styleDivider(headerPanel);
      return headerPanel;
    }
    else getStyleHandler().styleMatrixHeader(headerPanel);

    //Header
    if (!matrixTitle.isEmpty())
    {
      RelativeLayout headerLabel = new RelativeLayout(context);
      headerLabel.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT));
      headerLabel.setTag(Constants.C_MATRIXTITLEROW);
      getStyleHandler().styleMatrixHeaderRow(headerLabel);

      buildChildren(matrixTitle, headerLabel, null);

      if (panel.getParent() instanceof MBPanel && ((MBPanel) panel.getParent()).getType().equals(Constants.C_EDITABLEMATRIX))
      {
        MBPanel parent = (MBPanel) panel.getParent();

        // Only show button if not in editonly mode
        if (!parent.getMode().equals(Constants.C_EDITABLEMATRIX_MODE_EDITONLY))
        {
          RelativeLayout.LayoutParams editModeButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);
          editModeButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

          Button editModeButton = new Button(context);
          editModeButton.setLayoutParams(editModeButtonParams);
          editModeButton.setTag(Constants.C_EDITABLEMATRIX_EDITBUTTON);
          String modeText;
          if (parent.getMode().equals(Constants.C_EDITABLEMATRIX_MODE_EDIT))
          {
            // When in editing mode show "Save" button
            modeText = MBLocalizationService.getInstance().getTextForKey("Gereed");
          }
          else
          {
            // When not editing mode show "Edit" button
            modeText = MBLocalizationService.getInstance().getTextForKey("Wijzig");
          }
          editModeButton.setText(modeText);
          editModeButton.setGravity(Gravity.CENTER_VERTICAL);
          headerLabel.addView(editModeButton);

          MBViewBuilderFactory.getInstance().getStyleHandler().styleEditableMatrixModeButton(editModeButton);

        }
      }

      headerPanel.addView(headerLabel);
    }

    // Row with labels
    if (!matrixLabels.isEmpty())
    {
      LinearLayout headerRow = new LinearLayout(headerPanel.getContext());
      headerRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      headerRow.setOrientation(LinearLayout.HORIZONTAL);

      getStyleHandler().styleMatrixHeaderRow(headerRow);

      buildMatrixRowPanelChildren(matrixLabels, headerRow, true);

      headerPanel.addView(headerRow);
    }

    return headerPanel;
  }

  private ViewGroup buildMatrixRowPanel(MBPanel panel)
  {

    if (panel.getParent() != null && panel.getParent() instanceof MBPanel)
    {
      // Determine whether to build a default matrix row panel or a matrix row panel in edit mode
      MBPanel parent = (MBPanel) panel.getParent();
      if (parent.getType().equals(Constants.C_EDITABLEMATRIX)
          && parent.getMode() != null
          && (parent.getMode().equals(Constants.C_EDITABLEMATRIX_MODE_EDIT) || parent.getMode()
              .equals(Constants.C_EDITABLEMATRIX_MODE_EDITONLY)))
      {
        return buildEditableMatrixRowPanel(panel);
      }
    }

    return buildReadOnlyMatrixRowPanel(panel);
  }

  private ViewGroup buildReadOnlyMatrixRowPanel(MBPanel panel)
  {
    final Context context = MBApplicationController.getInstance().getBaseContext();

    // the parent of all widgets in this row
    RelativeLayout rowPanel = new RelativeLayout(context);
    rowPanel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    ArrayList<MBComponent> children = panel.getChildren();
    ArrayList<MBComponent> matrixRowLabels = new ArrayList<MBComponent>();
    ArrayList<MBComponent> matrixRowTitles = new ArrayList<MBComponent>(); // typically just one

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

    // Left border
    RelativeLayout.LayoutParams leftBorderParams = new RelativeLayout.LayoutParams(1, RelativeLayout.LayoutParams.WRAP_CONTENT);
    View leftBorder = new View(context);
    leftBorder.setLayoutParams(leftBorderParams);
    leftBorder.setId(UniqueIntegerGenerator.getId());
    leftBorder.setBackgroundColor(Color.DKGRAY);
    rowPanel.addView(leftBorder);
    //

    // Right border
    RelativeLayout.LayoutParams rightBorderParams = new RelativeLayout.LayoutParams(1, RelativeLayout.LayoutParams.WRAP_CONTENT);
    rightBorderParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    View rightBorder = new View(context);
    rightBorder.setLayoutParams(rightBorderParams);
    rightBorder.setId(UniqueIntegerGenerator.getId());
    rightBorder.setBackgroundColor(Color.DKGRAY);
    rowPanel.addView(rightBorder);
    //

    currentId = buildMatrixRowPanelHeader(panel, rowPanel, matrixRowTitles, currentId, leftBorder.getId(), rightBorder.getId());

    currentId = buildMatrixRowPanelLabels(panel, rowPanel, matrixRowLabels, currentId, leftBorder.getId(), rightBorder.getId());

    // Bottom border
    RelativeLayout.LayoutParams bottomBorderParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 1);
    bottomBorderParams.addRule(RelativeLayout.BELOW, currentId);
    View bottomBorder = new View(context);
    bottomBorder.setId(UniqueIntegerGenerator.getId());
    bottomBorder.setLayoutParams(bottomBorderParams);
    bottomBorder.setBackgroundColor(Color.DKGRAY);
    rowPanel.addView(bottomBorder);
    //

    rightBorderParams.addRule(RelativeLayout.ABOVE, bottomBorder.getId());
    leftBorderParams.addRule(RelativeLayout.ABOVE, bottomBorder.getId());

    if (panel.getOutcomeName() != null)
    {
      // Arrow
      ImageView arrow = new ImageView(rowPanel.getContext());
      arrow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
      arrow.setMinimumWidth(MBScreenUtilities.FORTY);
      arrow.setImageDrawable(MBResourceService.getInstance().getImageByID(Constants.C_ARROW));
      RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      arrowParams.addRule(RelativeLayout.CENTER_VERTICAL);
      arrow.setLayoutParams(arrowParams);

      rowPanel.addView(arrow);

      rowPanel.setClickable(true);
      rowPanel.setFocusable(true);
      rowPanel.setOnClickListener(panel);
      getStyleHandler().styleClickableRow(rowPanel);
    }

    return rowPanel;
  }

  private int buildMatrixRowPanelLabels(MBPanel panel, RelativeLayout rowPanel, ArrayList<MBComponent> matrixRowLabels, int currentId,
                                        int leftBorderId, int rightBorderId)
  {
    // Row with labels
    if (matrixRowLabels.isEmpty()) return currentId;
    RelativeLayout.LayoutParams rowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    if (currentId != -1) rowParams.addRule(RelativeLayout.BELOW, currentId);

    if (leftBorderId != -1)
    {
      rowParams.addRule(RelativeLayout.RIGHT_OF, leftBorderId);
    }
    if (rightBorderId != -1)
    {
      rowParams.addRule(RelativeLayout.LEFT_OF, rightBorderId);
    }

    LinearLayout row = new LinearLayout(rowPanel.getContext());
    row.setLayoutParams(rowParams);
    row.setOrientation(LinearLayout.HORIZONTAL);

    getStyleHandler().styleMatrixRow(panel, row);

    // Add children to panel
    //    buildChildren(p_matrixRowLabels, row, false, p_bounds, null);
    buildMatrixRowPanelChildren(matrixRowLabels, row, false);

    rowPanel.addView(row);
    currentId = UniqueIntegerGenerator.getId();
    row.setId(currentId);

    return currentId;
  }

  private void buildMatrixRowPanelChildren(ArrayList<MBComponent> matrixRowLabels, ViewGroup parent, boolean buildingHeaderPanelChildren)
  {

    boolean needsToProcessFirstLabel = true;

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
        LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);
        childView.setLayoutParams(params);
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

  private int buildMatrixRowPanelHeader(MBPanel panel, RelativeLayout rowPanel, ArrayList<MBComponent> matrixRowTitles, int currentId,
                                        int leftBorderId, int rightBorderId)
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
      // Position the view between the borders (if necessary)
      if (leftBorderId != -1 || rightBorderId != -1)
      {

        if (current.getLayoutParams() instanceof RelativeLayout.LayoutParams)
        {
          final RelativeLayout.LayoutParams rowParams = (RelativeLayout.LayoutParams) current.getLayoutParams();

          if (leftBorderId != -1)
          {
            rowParams.addRule(RelativeLayout.RIGHT_OF, leftBorderId);
          }
          if (rightBorderId != -1)
          {
            rowParams.addRule(RelativeLayout.LEFT_OF, rightBorderId);
          }
        }

      }

      currentId = UniqueIntegerGenerator.getId();
      current.setId(currentId);
    }

    return currentId;
  }

  private ViewGroup buildEditableMatrixRowPanel(MBPanel panel)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();

    RelativeLayout borderWrapper = new RelativeLayout(context);
    borderWrapper.setTag(Constants.C_MATRIXROW);
    borderWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

    RelativeLayout relativeContainer = new RelativeLayout(context);
    relativeContainer.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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
      getStyleHandler().styleButtonWithName(deleteButton, Constants.C_EDITABLEMATRIX_DELETEBUTTON);
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

      if (MBDevice.getInstance().isPhone())
      {
        Button upButton = new Button(context);
        upButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        upButton.setTag(Constants.C_EDITABLEMATRIX_UPBUTTON);
        rightButtonContainer.addView(upButton);
        getStyleHandler().styleButtonWithName(upButton, Constants.C_EDITABLEMATRIX_UPBUTTON);

        Button downButton = new Button(context);
        downButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        downButton.setTag(Constants.C_EDITABLEMATRIX_DOWNBUTTON);
        rightButtonContainer.addView(downButton);
        getStyleHandler().styleButtonWithName(downButton, Constants.C_EDITABLEMATRIX_DOWNBUTTON);
      }
      else
      {
        Button dragButton = new Button(context);
        dragButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        dragButton.setTag(Constants.C_EDITABLEMATRIX_DRAGBUTTON);
        rightButtonContainer.addView(dragButton);
        getStyleHandler().styleButtonWithName(dragButton, Constants.C_EDITABLEMATRIX_DRAGBUTTON);
      }
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
    ArrayList<MBComponent> matrixRowTitle = new ArrayList<MBComponent>();

    for (Iterator<MBComponent> iterator = children.iterator(); iterator.hasNext();)
    {
      MBComponent mbComponent = iterator.next();
      if (mbComponent instanceof MBField)
      {
        MBField field = (MBField) mbComponent;
        if (Constants.C_FIELD_MATRIXTITLE.equals(field.getType()))
        {
          matrixRowTitle.add(mbComponent);
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

    //Header
    if (!matrixRowTitle.isEmpty())
    {
      LinearLayout rowHeaderLabel = new LinearLayout(context);
      rowHeaderLabel.setOrientation(LinearLayout.HORIZONTAL);
      rowHeaderLabel.setPadding(MBScreenUtilities.TWO, MBScreenUtilities.TWO, MBScreenUtilities.FOUR, MBScreenUtilities.TWO);

      buildChildren(matrixRowTitle, rowHeaderLabel, null);
      rowPanel.addView(rowHeaderLabel);
    }

    // Row with labels
    if (!matrixRowLabels.isEmpty())
    {
      LinearLayout row = new LinearLayout(context);
      row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
      row.setOrientation(LinearLayout.HORIZONTAL);
      getStyleHandler().styleMatrixRow(panel, row);

      buildChildren(matrixRowLabels, row, null);

      rowPanel.addView(row);
    }

    relativeContainer.addView(rowPanel);
    //

    if (((MBPanel) panel.getParent()).isChildrenClickable() || ((MBPanel) panel.getParent()).isChildrenSelectable())
    {
      borderWrapper.setClickable(true);
      borderWrapper.setFocusable(true);
      getStyleHandler().styleClickableRow(borderWrapper);
    }

    borderWrapper.addView(relativeContainer);

    addBorders(borderWrapper, relativeContainer);

    return borderWrapper;
  }

  private void addBorders(ViewGroup parent, ViewGroup aroundView)
  {

    // Left divider
    RelativeLayout.LayoutParams leftDividerParams = new RelativeLayout.LayoutParams(1, RelativeLayout.LayoutParams.FILL_PARENT);
    leftDividerParams.addRule(RelativeLayout.ALIGN_TOP, aroundView.getId());
    leftDividerParams.addRule(RelativeLayout.ALIGN_BOTTOM, aroundView.getId());
    LinearLayout leftDivider = new LinearLayout(parent.getContext());
    leftDivider.setId(UniqueIntegerGenerator.getId());
    leftDivider.setLayoutParams(leftDividerParams);
    getStyleHandler().styleDivider(leftDivider);
    parent.addView(leftDivider);
    ((RelativeLayout.LayoutParams) aroundView.getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, leftDivider.getId());
    //

    // Right divider
    RelativeLayout.LayoutParams rightDividerParams = new RelativeLayout.LayoutParams(1, RelativeLayout.LayoutParams.FILL_PARENT);
    rightDividerParams.addRule(RelativeLayout.ALIGN_TOP, aroundView.getId());
    rightDividerParams.addRule(RelativeLayout.ALIGN_BOTTOM, aroundView.getId());
    rightDividerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    LinearLayout rightDivider = new LinearLayout(parent.getContext());
    rightDivider.setId(UniqueIntegerGenerator.getId());
    rightDivider.setLayoutParams(rightDividerParams);
    getStyleHandler().styleDivider(rightDivider);
    parent.addView(rightDivider);
    ((RelativeLayout.LayoutParams) aroundView.getLayoutParams()).addRule(RelativeLayout.LEFT_OF, rightDivider.getId());
    //

    // Bottom divider
    RelativeLayout.LayoutParams bottomDividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 1);
    bottomDividerParams.addRule(RelativeLayout.BELOW, aroundView.getId());
    LinearLayout divider = new LinearLayout(parent.getContext());
    divider.setLayoutParams(bottomDividerParams);
    getStyleHandler().styleDivider(divider);
    parent.addView(divider);
    //
  }

  public ViewGroup buildClickabletMatrixPanel(ViewGroup parent)
  {
    int matrixId = UniqueIntegerGenerator.getId();
    parent.setId(matrixId);

    // We create a container so we can add an leaf to show this section is clickable
    RelativeLayout container = new RelativeLayout(parent.getContext());
    container.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    container.addView(parent);

    // Add the leaf to the container
    ImageView leaf = new ImageView(parent.getContext());

    RelativeLayout.LayoutParams indicatorParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    indicatorParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    indicatorParams.addRule(RelativeLayout.ALIGN_BOTTOM, matrixId);

    // Indicator will be positioned on the bottom right of the parent
    leaf.setLayoutParams(indicatorParams);
    leaf.setImageDrawable(MBResourceService.getInstance().getImageByID(Constants.C_LEAF));

    container.addView(leaf);

    return container;
  }

}
