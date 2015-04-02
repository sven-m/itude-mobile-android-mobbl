/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl.core.view.builders;

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

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.android.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl.core.services.MBResourceService;
import com.itude.mobile.mobbl.core.util.MBConstants;
import com.itude.mobile.mobbl.core.util.MBScreenConstants;
import com.itude.mobile.mobbl.core.view.MBComponent;
import com.itude.mobile.mobbl.core.view.MBField;
import com.itude.mobile.mobbl.core.view.MBPage;
import com.itude.mobile.mobbl.core.view.MBPanel;
import com.itude.mobile.mobbl.core.view.components.MBSegmentedControlBar;
import com.itude.mobile.mobbl.core.view.components.MBSegmentedControlContainer;
import com.itude.mobile.mobbl.core.view.components.MBSegmentedItem;

import java.util.List;

public class MBStyleHandler {

    /**
     * Apply Style for default MBComponent's
     *
     * @param component MBComponent
     * @param view      View
     */
    public void applyStyle(MBComponent component, View view) {
        if (component instanceof MBField) applyStyle(view, (MBField) component);
        else if (component instanceof MBPage) applyStyle(view, (MBPage) component);
        else if (component instanceof MBPanel) applyStyle(view, (MBPanel) component);
    }

    /**
     * Style a Textview
     *
     * @param view  TextView
     * @param field MBField
     */
    public void styleLabel(TextView view, MBField field) {
        view.setBackgroundColor(Color.TRANSPARENT);

        if (field != null) alignLabel(view, field.getAlignment());
    }

    public void styleDateOrTimeSelectorValue(TextView view, MBField field) {
        styleLabel(view, field);
    }

    public void styleSubLabel(TextView view) {
        view.setGravity(Gravity.BOTTOM | Gravity.START);
    }

    public void styleSubLabel(TextView view, String style) {
    }

    public void styleTextfield(View view, MBField field) {
        if (view instanceof EditText) {
            EditText textField = (EditText) view;
            textField.setSelection(textField.getText().length());
            textField.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        }

    }

    public void styleTextView(TextView view, MBField field) {
    }

    public void styleButton(Button view, MBField field) {
        styleButtonWithName(view, field.getStyle());
    }

    /**
     * Important note regarding button states
     * from http://developer.android.com/resources/tutorials/views/hello-formstuff.html
     * <p/>
     * The order of the <item> elements is important. When this drawable is referenced, the <item>s are traversed in-order to determine
     * which one is appropriate for the current button state. Because the "normal" image is last, it is only applied when the conditions
     * android:state_pressed and android:state_focused have both evaluated false.
     * <p/>
     * R.attr.state_enabled = "normal"
     *
     * @param view
     * @param style
     */
    public void styleButtonWithName(Button view, String style) {
    }

    @Deprecated
    protected StateListDrawable getStatedButtonBackground(String stateNormal, String statePressed) {
        StateListDrawable buttonStates = new StateListDrawable();

        if (statePressed != null) {
            Drawable imageFocused = MBResourceService.getInstance().getImageByID(statePressed);
            buttonStates.addState(new int[]{android.R.attr.state_pressed}, imageFocused);
        }

        if (stateNormal != null) {
            Drawable imageEnabled = MBResourceService.getInstance().getImageByID(stateNormal);
            buttonStates.addState(new int[]{android.R.attr.state_enabled}, imageEnabled);
            buttonStates.addState(new int[]{-android.R.attr.state_selected}, imageEnabled);
        }

        return buttonStates;
    }

    public void styleInputfieldBackgroundWithName(EditText inputField, String style) {
    }

    public void styleMatrixHeaderTitle(TextView view) {
    }

    public void styleMatrixRowTitle(TextView view, MBField field) {
    }

    public void styleSectionHeader(LinearLayout header) {
    }

    public void styleSectionHeader(LinearLayout header, MBPanel sectionPanel) {
    }

    public void styleSectionHeaderText(TextView title) {
    }

    public void styleSectionHeaderText(TextView title, MBPanel sectionPanel) {
    }

    public void styleSectionContainer(LinearLayout view, boolean hasTitle) {
        view.setPadding(0, MBScreenConstants.SEVEN, 0, 0);
    }

    public void styleSectionContainer(LinearLayout view, boolean hasTitle, MBPanel panel) {
        view.setPadding(0, MBScreenConstants.SEVEN, 0, 0);
    }

    // The following methods are listed public so you can override them in a
    // subclass// You should not normally call them; use the generic method
    // above for that- (void) applyStyle:(Object *)view page:(MBPage *)page
    // viewState:(MBViewState)viewState;
    public void applyStyle(View view, MBPanel panel) {
    }

    public void applyStyle(View view, MBField field) {
    }

    public void applyInsetsForComponent(MBComponent component) {
    }

    public void alignLabel(TextView label, String alignment) {
        if (alignment != null) {
            // Align the label
            if (alignment.equals(MBConstants.C_ALIGNMENT_LEFT)) {
                label.setGravity(Gravity.START);
            } else if (alignment.equals(MBConstants.C_ALIGNMENT_CENTER)) {
                label.setGravity(Gravity.CENTER_HORIZONTAL);
            } else if (alignment.equals(MBConstants.C_ALIGNMENT_CENTER_VERTICAL)) {
                label.setGravity(Gravity.CENTER_VERTICAL);
            } else if (alignment.equals(MBConstants.C_ALIGNMENT_RIGHT)) {
                label.setGravity(Gravity.END);
            }
        }

    }

    public void styleMatrixHeader(LinearLayout view) {
    }

    public void styleBackground(View view) {
    }

    public void stylePageHeader(View view) {
    }

    public void stylePageHeaderTitle(TextView view) {
    }

    public void styleBasicPanelHeaderText(TextView view) {
    }

    public void styleBasicPanelHeader(LinearLayout result, String style) {
    }

    public void styleDivider(View view) {
        view.setBackgroundColor(Color.DKGRAY);
    }

    public void styleMatrixRow(MBPanel panel, LinearLayout row) {
        alignMatrixRow(panel, row);
    }

    public void alignMatrixRow(MBPanel panel, LinearLayout row) {
        List<MBField> fields = panel.getChildrenOfKindWithType(MBField.class, MBConstants.C_FIELD_MATRIXCELL, MBConstants.C_FIELD_MATRIXCELL);
        for (int idx = 0; idx < fields.size(); ++idx) {
            MBField field = fields.get(idx);
            if (idx > 0 && field.getAlignment() == null && field.getAttachedView() instanceof TextView)
                ((TextView) field.getAttachedView())
                        .setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    public void styleMatrixRowPanel(MBPanel panel, RelativeLayout row, boolean isClickable, String style, int rowNumber) {
        if (isClickable) {
            styleClickableRow(row, panel.getStyle());
        }
    }

    public void styleMatrixCell(MBField field, TextView label) {
        if (field.getAlignment() != null && field.getAlignment().equals("LEFT")) {
            label.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        } else if (field.getAlignment() != null && field.getAlignment().equals("RIGHT")) {
            label.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        } else {
            label.setGravity(Gravity.CENTER_VERTICAL);
        }
    }

    public void styleMatrixHeaderTitleRow(MBPanel panel, ViewGroup headerRow) {
    }

    public void styleMatrixHeaderLabelRow(ViewGroup headerRow) {
    }

    public void styleRowAlignment(LinearLayout rowPanel) {
    }

    public void styleRow(RelativeLayout rowPanel, String style) {
    }

    public void styleRowButton(View button, RelativeLayout.LayoutParams params) {
    }

    public void styleClickableRow(RelativeLayout view, String style) {
        view.setMinimumHeight(MBScreenConstants.FIFTY);
    }

    public void styleFragmentPadding(View fragment, int whichFragment) {
    }

    public void styleMatrixContainer(MBPanel matrixPanel, LinearLayout view) {
        view.setPadding(0, MBScreenConstants.SEVEN, 0, 0);
    }

    public void styleMainScrollbarView(MBPage page, View scrollableView) {
        scrollableView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
    }

    public void styleSegmentedItem(MBSegmentedItem item) {
    }

    public Drawable getDrawablePageIndicatorDrawable() {
        return null;
    }

    public void styleDrawablePageIndicatorBarActiveIndicatorView(View view) {
    }

    public void styleCheckBox(CheckBox checkBox) {
    }

    public void styleSpinner(Spinner spinner) {
    }

    public void styleCloseButtonDialog(Button button) {
    }

    public void styleWebView(WebView webView, MBField field) {
    }

    public void styleRowButtonAligment(MBComponent child, RelativeLayout.LayoutParams childParams) {
        if (child instanceof MBField) {
            MBField field = (MBField) child;

            String alignment = field.getAlignment();
            if (StringUtil.isNotBlank(alignment)) {
                if (alignment.equals(MBConstants.C_ALIGNMENT_RIGHT)) {
                    childParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    childParams.setMargins(0, 0, MBScreenConstants.FIVE, MBScreenConstants.FIVE);
                } else if (alignment.equals(MBConstants.C_ALIGNMENT_LEFT)) {
                    childParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    childParams.setMargins(MBScreenConstants.FIVE, 0, 0, MBScreenConstants.FIVE);
                } else {
                    childParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                }
            } else {
                childParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
        } else {
            childParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }

    }

    public void styleHint(EditText inputField) {
    }

    /**
     * @param image {@link ImageView}
     * @deprecated Please use styleImage(ImageView image, String style)
     */
    @Deprecated
    public void styleImage(ImageView image) {
    }

    public void styleImage(ImageView image, String style) {
    }

    public void styleListPanel(LinearLayout view, String style, boolean notDirectChildOfSection) {
        if (notDirectChildOfSection) {
            view.setPadding(0, MBScreenConstants.SEVEN, 0, MBScreenConstants.SEVEN);
        }

    }

    public void styleMatrixHeaderRowChild(View childView, MBField field, boolean needsToProcessFirstLabel) {
    }

    public void styleMatrixRowDescription(TextView label, MBField field) {
    }

    public void styleTimePickerDialog(TimePickerDialog timePickerDialog, MBField field) {
    }

    public void styleDatePickerDialog(DatePickerDialog datePickerDialog, MBField field) {
    }

    public void styleFirstSegmentedItem(Button item, String style) {
        item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
    }

    public void styleLastSegmentedItem(Button item, String style) {
        item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
    }

    public void styleCenterSegmentedItem(Button item, String style) {
        item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));

    }

    public void styleSegmentedControlBar(MBSegmentedControlBar segmentedControlBar, String style) {
        segmentedControlBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void styleSegmentedControlContainer(MBSegmentedControlContainer scc, MBPanel panel) {
    }

    public void styleSegmentedControlContentContainer(ViewGroup contentContainer, MBPanel segmentedControlPanel) {
    }

    public void styleSegmentedControlLayoutStructure(MBSegmentedControlBar controlBar, View contentContainer) {
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
    public void styleDatePickerDialog(DatePickerDialog datePickerDialog, TextView view, MBField field) {
    }

    public void styleLabelContainer(LinearLayout labelLayout, MBField field) {
    }

    public void styleImageButton(ImageButton button, MBField field) {
    }

    public void styleDialogCloseButton(Button closeButton) {
    }

    public void styleDialogCloseButtonWrapper(View wrapper) {
    }

}
