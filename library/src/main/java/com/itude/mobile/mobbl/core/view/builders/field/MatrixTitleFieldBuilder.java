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
package com.itude.mobile.mobbl.core.view.builders.field;

import android.view.View;
import android.widget.TextView;

import com.itude.mobile.mobbl.core.util.MBConstants;
import com.itude.mobile.mobbl.core.view.MBField;
import com.itude.mobile.mobbl.core.view.MBPanel;

public class MatrixTitleFieldBuilder extends MBBaseFieldBuilder {

    @Override
    public View buildField(MBField field) {
        String value = field.getValuesForDisplay();

        // Title TextView
        TextView label = buildTextViewWithValue(value);

        // Decide which styling to apply
        if (((MBPanel) field.getParent()).getType().equals(MBConstants.C_MATRIXHEADER)) {
            getStyleHandler().styleMatrixHeaderTitle(label);
        } else if (((MBPanel) field.getParent()).getType().equals(MBConstants.C_MATRIXROW)) {
            getStyleHandler().styleMatrixRowTitle(label, field);
        }

        // Make sure only 1 line is visible when creating the title
        label.setSingleLine(true);

        return label;
    }

}
