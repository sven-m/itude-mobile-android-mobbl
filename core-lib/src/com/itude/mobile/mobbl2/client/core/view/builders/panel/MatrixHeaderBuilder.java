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
package com.itude.mobile.mobbl2.client.core.view.builders.panel;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder.BuildState;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;

public class MatrixHeaderBuilder extends MBBasePanelBuilder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, BuildState buildState)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();

    RelativeLayout headerPanelContainer = buildContainer(context);

    LinearLayout headerPanel = buildHeader(context);

    ArrayList<MBComponent> matrixLabels = new ArrayList<MBComponent>();
    ArrayList<MBComponent> matrixTitles = new ArrayList<MBComponent>();

    groupChildren(panel, matrixLabels, matrixTitles);

    final MBStyleHandler styleHandler = getStyleHandler();
    if (matrixTitles.isEmpty() && matrixLabels.isEmpty())
    {
      // use the stylehandler for the divider to let the header
      // act as a top divider
      headerPanel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
      styleHandler.styleDivider(headerPanel);
      return headerPanel;

    }
    styleHandler.styleMatrixHeader(headerPanel);

    buildHeader(panel, headerPanel, matrixTitles);

    buildLabels(panel, headerPanel, matrixLabels);

    headerPanelContainer.addView(headerPanel);

    panel.attachView(headerPanelContainer);

    return headerPanelContainer;
  }

  private LinearLayout buildHeader(Context context)
  {
    RelativeLayout.LayoutParams headerPanelParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    headerPanelParams.addRule(RelativeLayout.CENTER_VERTICAL);
    LinearLayout headerPanel = new LinearLayout(context);
    headerPanel.setTag(Constants.C_MATRIXHEADER);
    headerPanel.setLayoutParams(headerPanelParams);
    headerPanel.setOrientation(LinearLayout.VERTICAL);
    return headerPanel;
  }

  private RelativeLayout buildContainer(Context context)
  {
    RelativeLayout headerPanelContainer = new RelativeLayout(context);
    headerPanelContainer.setTag(Constants.C_MATRIXHEADER_CONTAINER);
    headerPanelContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    return headerPanelContainer;
  }

  private void buildLabels(MBPanel panel, LinearLayout headerPanel, ArrayList<MBComponent> matrixLabels)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();

    // Row with labels
    if (!matrixLabels.isEmpty())
    {
      LinearLayout headerRow = new LinearLayout(context);
      headerRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
      headerRow.setOrientation(LinearLayout.HORIZONTAL);
      headerRow.setGravity(Gravity.CENTER_VERTICAL);

      getStyleHandler().styleMatrixHeaderLabelRow(headerRow);

      buildChildren(matrixLabels, headerRow, new BuildChildrenCallback()
      {

        @Override
        public void onConstructChild(MBComponent child, View view)
        {
          LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
          view.setLayoutParams(params);
          getStyleHandler().styleMatrixHeaderRowChild(view, (MBField) child, false);
        }
      });

      getStyleHandler().alignMatrixRow(panel, headerRow);
      headerPanel.addView(headerRow);
    }
  }

  private void buildHeader(MBPanel panel, LinearLayout headerPanel, ArrayList<MBComponent> matrixTitles)
  {
    Context context = MBApplicationController.getInstance().getBaseContext();
    //Header
    if (!matrixTitles.isEmpty())
    {
      RelativeLayout headerLabel = new RelativeLayout(context);
      headerLabel.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT));
      headerLabel.setTag(Constants.C_MATRIXTITLEROW);
      getStyleHandler().styleMatrixHeaderTitleRow(panel, headerLabel);

      buildChildren(matrixTitles, headerLabel, new BuildChildrenCallback()
      {

        @Override
        public void onConstructChild(MBComponent child, View view)
        {

          RelativeLayout.LayoutParams childViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
              LayoutParams.WRAP_CONTENT);
          childViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
          view.setLayoutParams(childViewParams);

        }
      });

      headerPanel.addView(headerLabel);
    }
  }

  private void groupChildren(MBPanel panel, ArrayList<MBComponent> matrixLabels, ArrayList<MBComponent> matrixTitles)
  {
    List<MBComponent> children = panel.getChildren();
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
  }

}
