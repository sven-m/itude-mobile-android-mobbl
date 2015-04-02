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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.itude.mobile.mobbl.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.services.MBLocalizationService;
import com.itude.mobile.mobbl.core.view.MBField;

public class DropdownListFieldBuilder extends MBBaseFieldBuilder {

    @Override
    public View buildField(MBField field) {
        Context context = MBApplicationController.getInstance().getViewManager();

        int selected = -1;

        Spinner dropdownList = new Spinner(context);
        dropdownList.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));

        getStyleHandler().styleSpinner(dropdownList);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    getStyleHandler().styleLabel(textView, null);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String fieldValue = field.getValue();
        if (field.getDomain() != null) {
            for (int i = 0; i < field.getDomain().getDomainValidators().size(); i++) {
                MBDomainValidatorDefinition domDef = field.getDomain().getDomainValidators().get(i);
                adapter.add(MBLocalizationService.getInstance().getTextForKey(domDef.getTitle()));

                String domDefValue = domDef.getValue();
                if ((fieldValue != null && fieldValue.equals(domDefValue))
                        || (fieldValue == null && field.getValueIfNil() != null && field.getValueIfNil().equals(domDefValue))) {
                    selected = i;
                }
            }
        }

        dropdownList.setAdapter(adapter);

        if (selected > -1) {
            dropdownList.setSelection(selected);
        }

        dropdownList.setOnItemSelectedListener(field);
        dropdownList.setOnKeyListener(field);

        if (field.getLabel() != null && field.getLabel().length() > 0) {
            dropdownList.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 50));

            LinearLayout labelLayout = new LinearLayout(context);
            labelLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            labelLayout.setOrientation(LinearLayout.HORIZONTAL);
            labelLayout.setGravity(Gravity.CENTER_VERTICAL);
            getStyleHandler().styleLabelContainer(labelLayout, field);

            TextView label = buildTextViewWithValue(field.getLabel());
            label.setText(field.getLabel());
            label.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 50));
            getStyleHandler().styleLabel(label, field);

            labelLayout.addView(label);
            labelLayout.addView(dropdownList);

            return labelLayout;
        }

        return dropdownList;
    }

}
