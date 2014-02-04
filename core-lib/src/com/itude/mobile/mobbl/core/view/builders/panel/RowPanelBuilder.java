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
package com.itude.mobile.mobbl.core.view.builders.panel;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.itude.mobile.android.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.view.MBComponent;
import com.itude.mobile.mobbl.core.view.MBPanel;
import com.itude.mobile.mobbl.core.view.builders.MBPanelViewBuilder.BuildState;
import com.itude.mobile.mobbl.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl.core.view.builders.MBViewBuilderFactory;

public class RowPanelBuilder extends MBBasePanelBuilder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, BuildState buildState)
  {

    final Context context = MBApplicationController.getInstance().getBaseContext();
    HashMap<String, Object> childIds = new HashMap<String, Object>();

    RelativeLayout rowPanel = new RelativeLayout(context);
    rowPanel.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    rowPanel.setTag(childIds);

    // Content view
    buildChildrenForRowPanel(panel.getChildren(), rowPanel);

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
  private void buildChildrenForRowPanel(List<? extends MBComponent> children, ViewGroup parent)
  {
    final Context context = parent.getContext();
    final MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    int previousButton = -1;

    LinearLayout nonButtonLayout = null;
    boolean processingLabel = false;
    LinearLayout labelLayout = null;

    for (MBComponent child : children)
    {
      View childView = child.buildView();
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
