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

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.view.MBPanel;
import com.itude.mobile.mobbl.core.view.builders.MBPanelViewBuilder.BuildState;

public class MatrixPanelBuilder extends MBBasePanelBuilder {

    @Override
    public ViewGroup buildPanel(MBPanel panel, BuildState buildState) {
        buildState.resetMatrixRow();

        LinearLayout result = new LinearLayout(MBApplicationController.getInstance().getBaseContext());
        result.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        result.setOrientation(LinearLayout.VERTICAL);
        buildChildren(panel.getChildren(), result);

        getStyleHandler().styleMatrixContainer(panel, result);

        if (StringUtil.isNotEmpty(panel.getOutcomeName())) {
            result.setOnClickListener(panel);
        }

        return result;

    }
}
