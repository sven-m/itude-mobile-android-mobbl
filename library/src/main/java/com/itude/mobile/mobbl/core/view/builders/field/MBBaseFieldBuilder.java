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

import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.widget.TextView;

import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.view.builders.MBFieldViewBuilder.Builder;
import com.itude.mobile.mobbl.core.view.builders.MBViewBuilder;

public abstract class MBBaseFieldBuilder extends MBViewBuilder implements Builder {
    protected static enum TextType {
        plain, html
    }

    protected TextView buildTextViewWithValue(String value) {
        return buildTextViewWithValue(value, TextType.plain);
    }

    protected TextView buildTextViewWithValue(String value, TextType type) {
        TextView label = new TextView(MBApplicationController.getInstance().getBaseContext());
        if (value == null) {
            // If the value is null we don't want it to be parsed as HTML since that will break the application
            label.setText("");
        } else {
            label.setEllipsize(TruncateAt.END);

            if (type == TextType.html) {
                label.setText(Html.fromHtml(value));
            } else {
                label.setText(value);
            }
        }

        getStyleHandler().styleLabel(label, null);

        return label;
    }

}
