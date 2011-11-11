package com.itude.mobile.mobbl2.client.core.view.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBComponentContainer;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.components.MBEditableMatrix;

public class MBPanelViewBuilder extends MBViewBuilder
{

  private boolean _isFirstRow      = true;
  private int     _matrixRowNumber = 0;

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
    _matrixRowNumber = 0;

    LinearLayout result = new LinearLayout(MBApplicationController.getInstance().getBaseContext());
    result.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    result.setOrientation(LinearLayout.VERTICAL);
    buildChildren(panel.getChildren(), result, viewState);

    getStyleHandler().styleMatrixContainer(panel, result);

    if (panel.getOutcomeName() != null && panel.getOutcomeName().length() > 0)
    {
      result.setOnClickListener(panel);
    }

    return result;
  }

  public ViewGroup buildEditableMatrix(MBPanel panel, MBViewState viewState)
  {
    _matrixRowNumber = 0;

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

    buildChildrenForEditableMatrix(panel.getChildren(), result.getCurrentContentView(), viewState);

    if (panel.getOutcomeName() != null && panel.getOutcomeName().length() > 0)
    {
      result.setOnClickListener(panel);
    }

    getStyleHandler().styleMatrixContainer(panel, result);

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

    LinearLayout panelView = new LinearLayout(context);
    panelView.setOrientation(LinearLayout.VERTICAL);

    if (panel.getTitle() != null)
    {
      // Show header at top of the section
      hasTitle = true;

      LinearLayout header = new LinearLayout(context);
      header.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      header.setOrientation(LinearLayout.VERTICAL);

      TextView title = new TextView(header.getContext());
      title.setText(panel.getTitle());

      getStyleHandler().styleSectionHeaderText(title);

      header.addView(title);

      getStyleHandler().styleSectionHeader(header);

      panelView.addView(header);
    }

    buildChildren(panel.getChildren(), panelView, null);

    getStyleHandler().styleSectionContainer(panelView, hasTitle);
    return panelView;
  }

  private ViewGroup buildRowPanel(MBPanel panel)
  {

    final Context context = MBApplicationController.getInstance().getBaseContext();
    HashMap<String, Object> childIds = new HashMap<String, Object>();

    RelativeLayout rowPanel = new RelativeLayout(context);
    rowPanel.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    rowPanel.setTag(childIds);

    // Content view
    buildChildrenForRowPanel(panel.getChildren(), rowPanel, null);

    // Arrow and clickable style of row
    if (panel.getOutcomeName() != null)
    {
      rowPanel.setClickable(true);
      rowPanel.setFocusable(true);
      rowPanel.setOnClickListener(panel);

      getStyleHandler().styleClickableRow(rowPanel);
    }

    // Processed the row so the next one isn't the first one anymore
    _isFirstRow = false;

    return rowPanel;
  }

  private void buildChildrenForMatrixHeader(List<? extends MBComponent> children, ViewGroup parent)
  {
    for (MBComponent child : children)
    {
      View childView = child.buildViewWithMaxBounds(null);
      if (childView == null) continue;

      RelativeLayout.LayoutParams childViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      childViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
      childView.setLayoutParams(childViewParams);

      parent.addView(childView);
    }
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

      RelativeLayout.LayoutParams childParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      childParams.addRule(RelativeLayout.CENTER_VERTICAL);

      if (!isFieldWithType(child, Constants.C_FIELD_BUTTON) && !isFieldWithType(child, Constants.C_FIELD_IMAGEBUTTON))
      {
        if (nonButtonLayout == null)
        {
          RelativeLayout.LayoutParams nonButtonLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);
          nonButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
          nonButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

          nonButtonLayout = new LinearLayout(context);
          nonButtonLayout.setLayoutParams(nonButtonLayoutParams);
          nonButtonLayout.setGravity(Gravity.CENTER_VERTICAL);

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

  private boolean isFieldWithType(MBComponent child, String type)
  {
    return child instanceof MBField && ((MBField) child).getType() != null && ((MBField) child).getType().equals(type);
  }

  private ViewGroup buildMatrixHeaderPanel(MBPanel panel)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();

    RelativeLayout headerPanelContainer = new RelativeLayout(context);
    headerPanelContainer.setTag(Constants.C_MATRIXHEADER_CONTAINER);
    headerPanelContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

    RelativeLayout.LayoutParams headerPanelParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
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
      headerPanel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
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
      headerLabel.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT));
      headerLabel.setTag(Constants.C_MATRIXTITLEROW);
      styleHandler.styleMatrixHeaderTitleRow(panel, headerLabel);

      buildChildrenForMatrixHeader(matrixTitles, headerLabel);

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
      LinearLayout headerRow = new LinearLayout(context);
      headerRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      headerRow.setOrientation(LinearLayout.HORIZONTAL);
      headerRow.setGravity(Gravity.CENTER_VERTICAL);

      styleHandler.styleMatrixHeaderLabelRow(headerRow);

      buildMatrixRowPanelChildren(matrixLabels, headerRow, true);
      headerPanel.addView(headerRow);
    }

    headerPanelContainer.addView(headerPanel);

    panel.attachView(headerPanelContainer);

    return headerPanelContainer;
  }

  private ViewGroup buildMatrixRowPanel(MBPanel panel)
  {
    _matrixRowNumber++;

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

    boolean isClickable = false;

    MBStyleHandler styleHandler = getStyleHandler();

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
      buildMatrixRowPanelLabels(panel, linearContainer, matrixRowLabels, currentId);
    }

    if (panel.getOutcomeName() != null)
    {
      isClickable = true;

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

    styleHandler.styleMatrixRowPanel(panel, rowPanel, isClickable, rowStyle, _matrixRowNumber);
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

  private void buildMatrixRowPanelChildren(ArrayList<MBComponent> matrixRowLabels, ViewGroup parent, boolean buildingHeaderPanelChildren)
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
        LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);
        childView.setLayoutParams(params);

        if (needsToProcessFirstLabel)
        {
          styleHandler.styleFirstMatrixHeaderRowChild(childView);
        }
        styleHandler.styleMatrixHeaderRowChild(childView);
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

  private ViewGroup buildEditableMatrixRowPanel(MBPanel panel)
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

    styleHandler.styleMatrixRowPanel(panel, borderWrapper, isClickable, rowStyle, _matrixRowNumber);
    return borderWrapper;
  }

  public ViewGroup buildClickableMatrixPanel(ViewGroup parent)
  {
    int matrixId = UniqueIntegerGenerator.getId();
    parent.setId(matrixId);

    // We create a container so we can add an leaf to show this section is clickable
    Context context = parent.getContext();
    FrameLayout container = new FrameLayout(context);
    container.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    container.addView(parent);

    // Add the leaf to the container
    ImageView leaf = new ImageView(context);

    // Indicator will be positioned on the bottom right of the parent
    leaf.setLayoutParams(new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT));
    leaf.setImageDrawable(MBResourceService.getInstance().getImageByID(Constants.C_LEAF));

    container.addView(leaf);

    return container;
  }

  public ViewGroup buildClickableMatrixPanel(ViewGroup parent, String style)
  {
    return buildClickableMatrixPanel(parent);
  }

}
