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

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.view.MBComponentContainer;
import com.itude.mobile.mobbl.core.view.MBPanel;
import com.itude.mobile.mobbl.core.view.builders.MBPanelViewBuilder.BuildState;

public class ListPanelBuilder extends MBBasePanelBuilder
{

  @Override
  public ViewGroup buildPanel(MBPanel panel, BuildState buildState)
  {
    final Context context = MBApplicationController.getInstance().getBaseContext();
    LinearLayout result = new LinearLayout(context);
    result.setOrientation(LinearLayout.VERTICAL);

    if (panel.getTitle() != null)
    {
      TextView title = new TextView(context);
      title.setText(panel.getTitle());
      result.addView(title);
      getStyleHandler().styleBasicPanelHeaderText(title);
    }
    buildChildren(panel.getChildren(), result);

    // Only add padding if this list isn't a direct child of a section
    MBComponentContainer parent = panel.getParent();
    boolean notDirectChildOfSection = (!(parent != null && parent instanceof MBPanel && (((MBPanel) parent).getType()) != null && ((MBPanel) parent)
        .getType().equals(Constants.C_SECTION)));

    getStyleHandler().styleListPanel(result, panel.getStyle(), notDirectChildOfSection);

    return result;

  }

}
