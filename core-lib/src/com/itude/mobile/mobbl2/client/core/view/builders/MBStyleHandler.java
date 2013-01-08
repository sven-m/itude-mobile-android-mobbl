package com.itude.mobile.mobbl2.client.core.view.builders;

import java.util.List;

import android.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.components.MBCirclePageIndicatorBar;
import com.itude.mobile.mobbl2.client.core.view.components.MBDrawablePageIndicatorBar;
import com.itude.mobile.mobbl2.client.core.view.components.MBHeader;
import com.itude.mobile.mobbl2.client.core.view.components.MBSegmentedControlBar;
import com.itude.mobile.mobbl2.client.core.view.components.MBSegmentedControlContainer;
import com.itude.mobile.mobbl2.client.core.view.components.MBSegmentedItem;
import com.itude.mobile.mobbl2.client.core.view.components.MBTab;

public class MBStyleHandler
{

  public void applyStyle(MBComponent component, View view)
  {
    if (component instanceof MBField) applyStyle(view, (MBField) component);
    else if (component instanceof MBPage) applyStyle(view, (MBPage) component);
    else if (component instanceof MBPanel) applyStyle(view, (MBPanel) component);
  }

  public void styleLabel(TextView view, MBField field)
  {
    view.setBackgroundColor(Color.TRANSPARENT);

    if (field != null) alignLabel(view, field.getAlignment());
  }

  public void styleDateOrTimeSelectorValue(TextView view, MBField field)
  {
    styleLabel(view, field);
  }

  public void styleSubLabel(TextView view)
  {
    view.setGravity(Gravity.BOTTOM | Gravity.LEFT);
  }

  public void styleSubLabel(TextView view, String style)
  {
  }

  public void styleMultilineLabel(View view, MBField field)
  {
  }

  public void styleTextfield(View view, MBField field)
  {
    if (view instanceof EditText)
    {
      EditText textField = (EditText) view;
      textField.setSelection(textField.getText().length());
      textField.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }

  }

  public void styleTextView(TextView view, MBField field)
  {
  }

  public void styleTextView(TextView textView, String name)
  {
  }

  public void styleButton(Button view, MBField field)
  {
    styleButtonWithName(view, field.getStyle());
  }

  /**
   * Important note regarding button states 
   * from http://developer.android.com/resources/tutorials/views/hello-formstuff.html
   * 
   * The order of the <item> elements is important. When this drawable is referenced, the <item>s are traversed in-order to determine 
   * which one is appropriate for the current button state. Because the "normal" image is last, it is only applied when the conditions 
   * android:state_pressed and android:state_focused have both evaluated false.
   * 
   * R.attr.state_enabled = "normal"
   *   
   * @param view
   * @param style
   */
  public void styleButtonWithName(Button view, String style)
  {
  }

  public void styleButtonHeight(Button view, String style)
  {
  }

  @Deprecated
  protected StateListDrawable getStatedButtonBackground(String stateNormal, String statePressed)
  {
    StateListDrawable buttonStates = new StateListDrawable();

    if (statePressed != null)
    {
      Drawable imageFocused = MBResourceService.getInstance().getImageByID(statePressed);
      buttonStates.addState(new int[]{R.attr.state_pressed}, imageFocused);
    }

    if (stateNormal != null)
    {
      Drawable imageEnabled = MBResourceService.getInstance().getImageByID(stateNormal);
      buttonStates.addState(new int[]{R.attr.state_enabled}, imageEnabled);
      buttonStates.addState(new int[]{-R.attr.state_selected}, imageEnabled);
    }

    return buttonStates;
  }

  @Deprecated
  protected StateListDrawable getStatedButtonBackground(String stateNormal, String statePressed, String stateDisabled)
  {
    StateListDrawable buttonStates = getStatedButtonBackground(stateNormal, statePressed);

    if (stateDisabled != null)
    {
      Drawable imageDisabled = MBResourceService.getInstance().getImageByID(stateDisabled);
      buttonStates.addState(new int[]{-R.attr.state_enabled}, imageDisabled);
    }

    return buttonStates;
  }

  @Deprecated
  protected StateListDrawable getStatedButtonBackground(String stateNormal, String statePressed, String stateDisabled, String stateSelected)
  {
    // Fix for issue: http://dev.itude.com/jira/browse/BINCKAPPS-995
    // The order in which the states are being added is very important! 
    // See http://developer.android.com/guide/topics/resources/drawable-resource.html#StateList for more info

    StateListDrawable buttonStates = new StateListDrawable();
    Drawable imageDisabled = MBResourceService.getInstance().getImageByID(stateDisabled);
    Drawable imageSelected = MBResourceService.getInstance().getImageByID(stateSelected);
    Drawable imageNormal = MBResourceService.getInstance().getImageByID(stateNormal);
    Drawable imagePressed = MBResourceService.getInstance().getImageByID(statePressed);

    if (stateSelected != null) buttonStates.addState(new int[]{R.attr.state_selected}, imageSelected);
    if (statePressed != null) buttonStates.addState(new int[]{R.attr.state_pressed}, imagePressed);
    if (stateDisabled != null) buttonStates.addState(new int[]{-R.attr.state_enabled}, imageDisabled);
    if (stateNormal != null) buttonStates.addState(new int[]{R.attr.state_enabled}, imageNormal);

    return buttonStates;
  }

  protected StateListDrawable getStatedEditTextBackground(String stateNormal, String stateActive, String stateDisabled)
  {
    StateListDrawable buttonStates = new StateListDrawable();
    Drawable imageNormal = MBResourceService.getInstance().getImageByID(stateNormal);
    Drawable imageActive = MBResourceService.getInstance().getImageByID(stateActive);
    Drawable imageDisabled = MBResourceService.getInstance().getImageByID(stateDisabled);

    if (stateActive != null) buttonStates.addState(new int[]{R.attr.state_focused}, imageActive);
    if (stateNormal != null) buttonStates.addState(new int[]{R.attr.state_enabled}, imageNormal);
    if (stateDisabled != null) buttonStates.addState(new int[]{-R.attr.state_enabled}, imageDisabled);

    return buttonStates;
  }

  public void styleImageButtonWithName(ImageButton view, String style)
  {
  }

  public void styleInputfieldBackgroundWithName(EditText inputField, String style)
  {
  }

  public void styleEditableMatrixModeButton(Button view)
  {
  }

  public void styleMatrixHeaderTitle(TextView view)
  {
  }

  public void styleMatrixRowTitle(TextView view, MBField field)
  {
  }

  public void styleTabBarController(View tabBarController)
  {
  }

  public void styleSectionHeader(LinearLayout header)
  {
  }

  public void styleSectionHeader(LinearLayout header, MBPanel sectionPanel)
  {
  }

  public void styleSectionHeaderText(TextView title)
  {
  }

  public void styleSectionHeaderText(TextView title, MBPanel sectionPanel)
  {
  }

  /**
   * Style the fore and- background color of the view, based on the given delta
   * 
   * @param view 
   * @param value foreground style based on positive, negative or zero value
   * @param delta background style based on positive, negative or zero difference
   */
  public void styleChangedValue(TextView view, double value, double delta)
  {
    if (delta < 0)
    {
      view.setTextColor(Color.WHITE);
      view.setBackgroundColor(Color.RED);
    }
    else if (delta > 0)
    {
      view.setTextColor(Color.WHITE);
      view.setBackgroundColor(Color.GREEN);
    }
  }

  public void styleSectionContainer(LinearLayout view, boolean hasTitle)
  {
    view.setPadding(0, MBScreenUtilities.SEVEN, 0, 0);
  }

  public void styleSectionContainer(LinearLayout view, boolean hasTitle, MBPanel panel)
  {
    view.setPadding(0, MBScreenUtilities.SEVEN, 0, 0);
  }

  public Object sizeForTextField(MBField field, Object bounds)
  {
    return null;
  }

  public Object sizeForLabel(MBField field, Object bounds)
  {
    return null;
  }

  // The following methods are listed public so you can override them in a
  // subclass// You should not normally call them; use the generic method
  // above for that- (void) applyStyle:(Object *)view page:(MBPage *)page
  // viewState:(MBViewState)viewState;
  public void applyStyle(View view, MBPanel panel)
  {
  }

  public void applyStyle(View view, MBField field)
  {
  }

  public void applyInsetsForComponent(MBComponent component)
  {
  }

  public void alignLabel(TextView label, String alignment)
  {
    if (alignment != null)
    {
      // Align the label
      if (alignment.equals(Constants.C_ALIGNMENT_LEFT))
      {
        label.setGravity(Gravity.LEFT);
      }
      else if (alignment.equals(Constants.C_ALIGNMENT_CENTER))
      {
        label.setGravity(Gravity.CENTER_HORIZONTAL);
      }
      else if (alignment.equals(Constants.C_ALIGNMENT_CENTER_VERTICAL))
      {
        label.setGravity(Gravity.CENTER_VERTICAL);
      }
      else if (alignment.equals(Constants.C_ALIGNMENT_RIGHT))
      {
        label.setGravity(Gravity.RIGHT);
      }
    }

  }

  public void styleMatrixHeader(LinearLayout view)
  {
  }

  public void styleBackground(View view)
  {
  }

  public void stylePageHeader(View view)
  {
  }

  public void stylePageHeaderTitle(TextView view)
  {
  }

  public void styleBasicPanelHeaderText(TextView view)
  {
  }

  public void styleBasicPanelHeader(LinearLayout result, String style)
  {
  }

  public void styleDivider(View view)
  {
    view.setBackgroundColor(Color.DKGRAY);
  }

  public void styleMatrixRow(MBPanel panel, LinearLayout row)
  {
    alignMatrixRow(panel, row);
  }

  public void alignMatrixRow(MBPanel panel, LinearLayout row)
  {
    List<MBField> fields = panel.getChildrenOfKindWithType(MBField.class, Constants.C_FIELD_MATRIXCELL, Constants.C_FIELD_MATRIXCELL);
    for (int idx = 0; idx < fields.size(); ++idx)
    {
      MBField field = fields.get(idx);
      if (idx > 0 && field.getAlignment() == null && field.getAttachedView() instanceof TextView) ((TextView) field.getAttachedView())
          .setGravity(Gravity.CENTER_HORIZONTAL);
    }
  }

  public void styleMatrixRowPanel(MBPanel panel, RelativeLayout row, boolean isClickable, String style, int rowNumber)
  {
    if (isClickable)
    {
      styleClickableRow(row, panel.getStyle());
    }
  }

  public void styleMatrixCell(MBField field, TextView label)
  {
    if (field.getAlignment() != null && field.getAlignment().equals("LEFT"))
    {
      label.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }
    else if (field.getAlignment() != null && field.getAlignment().equals("RIGHT"))
    {
      label.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    }
    else
    {
      label.setGravity(Gravity.CENTER_VERTICAL);
    }
  }

  public void styleMatrixHeaderRow(ViewGroup headerRow)
  {
  }

  public void styleMatrixHeaderTitleRow(ViewGroup headerRow)
  {
  }

  public void styleMatrixHeaderTitleRow(MBPanel panel, ViewGroup headerRow)
  {
  }

  public void styleMatrixHeaderLabelRow(ViewGroup headerRow)
  {
  }

  public void styleRowAlignment(LinearLayout rowPanel)
  {
  }

  public void styleRow(RelativeLayout rowPanel, String style)
  {
  }

  public void styleRow(ViewGroup rowPanel, String style)
  {
  }

  public void styleRowButton(View button, RelativeLayout.LayoutParams params)
  {
  }

  public void styleClickableRow(RelativeLayout view, String style)
  {
    view.setMinimumHeight(MBScreenUtilities.FIFTY);

    RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    arrowParams.addRule(RelativeLayout.CENTER_VERTICAL);

    ImageView arrow = new ImageView(view.getContext());
    arrow.setLayoutParams(arrowParams);
    arrow.setMinimumWidth(MBScreenUtilities.FORTY);
    arrow.setImageDrawable(MBResourceService.getInstance().getImageByID(Constants.C_ARROW));

    if (StringUtilities.isNotBlank(style))
    {
      if (Constants.C_STYLE_WRAP_ROW.equals(style))
      {
        int siblings = view.getChildCount();
        if (siblings > 0)
        {
          View latestChild = view.getChildAt(siblings - 1);
          latestChild.setPadding(0, 0, MBScreenUtilities.TWENTYEIGHT, 0);
        }
      }
    }

    view.addView(arrow);
  }

  public void styleFragmentPadding(View fragment, int whichFragment)
  {
  }

  public void styleMatrixContainer(MBPanel matrixPanel, LinearLayout view)
  {
    view.setPadding(0, MBScreenUtilities.SEVEN, 0, 0);
  }

  public void styleMainScrollbarView(MBPage page, View scrollableView)
  {
    scrollableView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
  }

  public void styleMainScrollbarView(MBPage page, View scrollableView, String name)
  {

  }

  public void styleSegmentedControl(View segmentedControl)
  {
    segmentedControl.setPadding(0, MBScreenUtilities.SEVEN, 0, 0);
  }

  public void styleSegmentedItem(MBSegmentedItem item)
  {
  }

  public void styleDrawablePageIndicatorBar(MBDrawablePageIndicatorBar indicatorBar)
  {
  }

  public Drawable getDrawablePageIndicatorDrawable()
  {
    return null;
  }

  public void styleDrawablePageIndicatorBarActiveIndicatorView(View view)
  {
  }

  public void styleDrawablePageIndicatorBarInactiveIndicatorView(View view)
  {
  }

  public void styleCirclePageIndicatorBar(MBCirclePageIndicatorBar indicatorBar)
  {
  }

  public void styleDiffableValue(TextView label, double delta)
  {
  }

  public void styleSelectedItem(View view)
  {
  }

  public void styleCheckBox(CheckBox checkBox)
  {
  }

  public void styleSpinner(Spinner spinner)
  {
  }

  public void styleTabDropdownItem(View view)
  {
  }

  public void styleDropdownItem(View view)
  {
  }

  public void styleCloseButtonDialog(Button button)
  {
  }

  // Style methods for tablet
  public void styleHomeIcon(View homeIcon)
  {
  }

  @SuppressLint("NewApi")
  public void styleActionBar(ActionBar actionBar)
  {
  }

  public void styleTab(MBTab mbTab)
  {

  }

  public void styleTabText(TextView view)
  {
    view.setTextSize(18);
  }

  public void styleTabSpinnerText(TextView view)
  {
  }

  public void styleWebView(WebView webView, MBField field)
  {
  }

  public void styleRowButtonAligment(MBComponent child, RelativeLayout.LayoutParams childParams)
  {
    if (child instanceof MBField)
    {
      MBField field = (MBField) child;

      String alignment = field.getAlignment();
      if (StringUtilities.isNotBlank(alignment))
      {
        if (alignment.equals(Constants.C_ALIGNMENT_RIGHT))
        {
          childParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
          childParams.setMargins(0, 0, MBScreenUtilities.FIVE, MBScreenUtilities.FIVE);
        }
        else if (alignment.equals(Constants.C_ALIGNMENT_LEFT))
        {
          childParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
          childParams.setMargins(MBScreenUtilities.FIVE, 0, 0, MBScreenUtilities.FIVE);
        }
        else
        {
          childParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
      }
      else
      {
        childParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      }
    }
    else
    {
      childParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    }

  }

  public void styleSearchPlate(LinearLayout searchLayout)
  {
  }

  public void styleSearchSubmitArea(LinearLayout submitArea)
  {
  }

  public void styleHint(EditText inputField)
  {
  }

  /***
   * @deprecated Please use styleImage(ImageView image, String style)
   * @param image {@link ImageView}
   */
  @Deprecated
  public void styleImage(ImageView image)
  {
  }

  public void styleImage(ImageView image, String style)
  {
  }

  public void styleListPanel(LinearLayout view, String style, boolean notDirectChildOfSection)
  {
    if (notDirectChildOfSection)
    {
      view.setPadding(0, MBScreenUtilities.SEVEN, 0, MBScreenUtilities.SEVEN);
    }

  }

  public void styleMatrixHeaderRowChild(View childView, MBField field, boolean needsToProcessFirstLabel)
  {
  }

  public void styleMatrixRowDescription(TextView label, MBField field)
  {
  }

  public void styleTimePickerDialog(TimePickerDialog timePickerDialog, MBField field)
  {
  }

  public void styleDatePickerDialog(DatePickerDialog datePickerDialog, MBField field)
  {
  }

  public void styleFirstSegmentedItem(Button item, String style)
  {
    item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
  }

  public void styleLastSegmentedItem(Button item, String style)
  {
    item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
  }

  public void styleCenterSegmentedItem(Button item, String style)
  {
    item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));

  }

  public void styleSegmentedControlBar(MBSegmentedControlBar segmentedControlBar, String style)
  {
    segmentedControlBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
  }

  public void styleSegmentedControlContainer(MBSegmentedControlContainer scc, MBPanel panel)
  {
  }

  public void styleSegmentedControlContentContainer(ViewGroup contentContainer, MBPanel segmentedControlPanel)
  {
  }

  public void styleSegmentedControlLayoutStructure(MBSegmentedControlBar controlBar, View contentContainer)
  {
    controlBar.setId(UniqueIntegerGenerator.getId());

    /*
     * Default we want the controlbar to be above the contentcontainer and the contentcontainer to fill up the height of the screen
     */
    RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    containerParams.addRule(RelativeLayout.BELOW, controlBar.getId());
    containerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

    contentContainer.setLayoutParams(containerParams);
  }

  // TODO, moet samen gevoegd worden
  public void styleDatePickerDialog(DatePickerDialog datePickerDialog, TextView view, MBField field)
  {
  }

  public void styleLabelContainer(LinearLayout labelLayout, MBField field)
  {
  }

  public void styleImageButton(ImageButton button, MBField field)
  {
  }

  public void styleDialogCloseButton(Button closeButton)
  {
  }

  public void styleDialogCloseButtonWrapper(View wrapper)
  {
  }

  public void styleDialogHeader(MBHeader dialogHeader)
  {
  }

}
