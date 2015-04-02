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

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itude.mobile.android.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.util.MBParseUtil;
import com.itude.mobile.mobbl.core.view.MBField;

public class CheckboxFieldBuilder extends MBBaseFieldBuilder {

    @Override
    public View buildField(MBField field) {
        Boolean value = MBParseUtil.strictBooleanValue(field.getUntranslatedValue());
        boolean valueIfNil = MBParseUtil.booleanValue(field.getUntranslatedValueIfNil());
        boolean checked = false;

        if ((value != null && value) || (value == null && valueIfNil)) {
            checked = true;
        }

        Context context = MBApplicationController.getInstance().getBaseContext();

        final CheckBox checkBox = new CheckBox(context);
        checkBox.setId(UniqueIntegerGenerator.getId());
        checkBox.setChecked(checked);
        checkBox.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        checkBox.setOnCheckedChangeListener(field);
        checkBox.setOnKeyListener(field);

        getStyleHandler().styleCheckBox(checkBox);

        RelativeLayout container = new RelativeLayout(context);
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        container.setLayoutParams(rlParams);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.toggle();
            }
        });

        if (field.getLabel() != null && field.getLabel().length() > 0) {
            RelativeLayout.LayoutParams cbParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            cbParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            checkBox.setLayoutParams(cbParams);

            RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            labelParams.addRule(RelativeLayout.LEFT_OF, checkBox.getId());
            labelParams.addRule(RelativeLayout.CENTER_VERTICAL);

            TextView label = buildTextViewWithValue(field.getLabel());
            label.setLayoutParams(labelParams);
            getStyleHandler().styleLabel(label, field);

            container.addView(label);
        }

        container.addView(checkBox);

        return container;
    }

}
