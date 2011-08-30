package com.itude.mobile.mobbl2.client.core.view.builders;

import android.R;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.components.MBPageIndicatorBar;
import com.itude.mobile.mobbl2.client.core.view.components.MBSegmentedItem;

public class MBStyleHandler
{

  public void applyStyle(MBComponent component, View view, MBViewManager.MBViewState viewState)
  {
    if (component instanceof MBField) applyStyle(view, (MBField) component, viewState);
    else if (component instanceof MBPage) applyStyle(view, (MBPage) component, viewState);
    else if (component instanceof MBPanel) applyStyle(view, (MBPanel) component, viewState);
  }

  public void styleLabel(TextView view, MBField field)
  {
    view.setBackgroundColor(Color.TRANSPARENT);

    if (field != null) alignLabel(view, field.getAlignment());
  }

  public void styleSubLabel(TextView view)
  {
    view.setGravity(Gravity.BOTTOM | Gravity.LEFT);
  }

  public void styleMultilineLabel(View view, MBField field)
  {
  }

  public void styleTextfield(View view, MBField field)
  {
    if (view instanceof EditText)
    {
      EditText textField = (EditText) view;
      textField.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }

  }

  public void styleTextView(TextView view, MBField field)
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
    if (Constants.C_EDITABLEMATRIX_DELETEBUTTON.equals(style))
    {
      view.setBackgroundDrawable(getStatedButtonBackground("button-delete-normal", "button-delete-pressed", "button-delete-disabled"));
    }
    else if (Constants.C_EDITABLEMATRIX_UPBUTTON.equals(style))
    {
      view.setBackgroundDrawable(getStatedButtonBackground("button-arrow-up-normal", "button-arrow-up-pressed", "button-arrow-up-disabled"));
    }
    else if (Constants.C_EDITABLEMATRIX_DOWNBUTTON.equals(style))
    {
      view.setBackgroundDrawable(getStatedButtonBackground("button-arrow-down-normal", "button-arrow-down-pressed", "button-arrow-down-disabled"));
    }
    else if (Constants.C_EDITABLEMATRIX_DRAGBUTTON.equals(style))
    {
      view.setBackgroundDrawable(MBResourceService.getInstance().getImageByID("button-drag"));
    }
  }
  
  protected StateListDrawable getStatedButtonBackground(String stateNormal, String statePressed)
  {
    StateListDrawable buttonStates = new StateListDrawable();
    Drawable imageEnabled = MBResourceService.getInstance().getImageByID(stateNormal);
    Drawable imageFocused = MBResourceService.getInstance().getImageByID(statePressed);

    if (statePressed != null) buttonStates.addState(new int[]{R.attr.state_pressed}, imageFocused);
    if (stateNormal != null) buttonStates.addState(new int[]{R.attr.state_enabled}, imageEnabled);

    return buttonStates;
  }

  protected StateListDrawable getStatedButtonBackground(String stateNormal, String statePressed, String stateDisabled)
  {
    StateListDrawable buttonStates = new StateListDrawable();
    Drawable imageEnabled = MBResourceService.getInstance().getImageByID(stateNormal);
    Drawable imageFocused = MBResourceService.getInstance().getImageByID(statePressed);
    Drawable imageDisabled = MBResourceService.getInstance().getImageByID(stateDisabled);

    if (statePressed != null) buttonStates.addState(new int[]{R.attr.state_pressed}, imageFocused);
    if (stateNormal != null) buttonStates.addState(new int[]{R.attr.state_enabled}, imageEnabled);
    if (stateDisabled != null) buttonStates.addState(new int[]{-R.attr.state_enabled}, imageDisabled);

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

  public void styleInputfieldBackgroundWithName(View inputField, String style)
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

  public void styleNavigationBar(View bar)
  {
  }

  public void styleTabBarController(View tabBarController)
  {
  }

  public void styleSectionHeader(LinearLayout header)
  {
  }

  public void styleSectionHeaderText(TextView title)
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
  public void applyStyle(View view, MBPanel panel, MBViewManager.MBViewState viewState)
  {
  }

  public void applyStyle(View view, MBField field, MBViewManager.MBViewState viewState)
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

  public void styleBasicPanelHeader(View result)
  {
  }

  public void styleDivider(LinearLayout view)
  {
    view.setBackgroundColor(Color.DKGRAY);
  }

  public void styleMatrixRow(MBPanel panel, LinearLayout row)
  {
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

  public void styleRow(LinearLayout view)
  {
  }

  public void styleRowItemLeft(RelativeLayout.LayoutParams params)
  {
  }

  public void styleRowItemRight(RelativeLayout.LayoutParams params)
  {
  }

  public void styleRowButton(RelativeLayout.LayoutParams params)
  {
  }

  public void styleClickableRow(ViewGroup view)
  {
  }

  public void styleMatrixContainer(LinearLayout view)
  {
    view.setPadding(0, MBScreenUtilities.SEVEN, 0, 0);
  }

  public void styleListPanelContainer(View view)
  {
    view.setPadding(0, MBScreenUtilities.SEVEN, 0, 0);
  }

  public void styleMainScrollbarView(View scrollableView)
  {
    scrollableView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
    scrollableView.setPadding(MBScreenUtilities.SEVEN, 0, MBScreenUtilities.SEVEN, MBScreenUtilities.SEVEN);
  }

  public void styleSegmentedControl(View segmentedControl)
  {
    segmentedControl.setPadding(MBScreenUtilities.SEVEN, MBScreenUtilities.FIVE, MBScreenUtilities.SEVEN, MBScreenUtilities.NINE);
  }

  public void styleSegmentedItem(MBSegmentedItem item)
  {
  }

  public void stylePageIndicatorBar(MBPageIndicatorBar indicatorBar)
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

  public void styleActionBar(ActionBar actionBar)
  {
  }

  public void styleTabItem(View view)
  {
  }

  public void styleDropdownItem(View view)
  {
  }
}
