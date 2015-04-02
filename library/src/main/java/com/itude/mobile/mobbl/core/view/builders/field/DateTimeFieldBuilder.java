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
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.itude.mobile.android.util.DateUtil;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBResourceService;
import com.itude.mobile.mobbl.core.view.MBDateField;
import com.itude.mobile.mobbl.core.view.MBField;
import com.itude.mobile.mobbl.core.view.builders.MBStyleHandler;

public abstract class DateTimeFieldBuilder extends MBBaseFieldBuilder {

    @Override
    public View buildField(MBField field) {
        final Context context = MBApplicationController.getInstance().getBaseContext();

        final MBStyleHandler styleHandler = getStyleHandler();

        final MBDocument doc = field.getDocument();
        final String path = field.getPath();

        final MBDateField df = new MBDateField();

        // Create our container which will fill the whole width
        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        container.setGravity(Gravity.CENTER_VERTICAL);

        // Add our label (if one exists)
        TextView label = buildTextViewWithValue(field.getLabel());
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 50));

        styleHandler.styleLabel(label, field);

        final TextView value = new TextView(context);
        value.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 50));
        value.setGravity(Gravity.CENTER_VERTICAL);

        // Find out if we have previously set a time
        String dateTimeString = doc.getValueForPath(path);
        String valueLabelText = "";

        String nillValue = field.getValueIfNil();
        if (StringUtil.isNotBlank(nillValue)) {
            valueLabelText = field.getValueIfNil();
        }

        if (StringUtil.isNotBlank(dateTimeString)) {
            df.setTime(dateTimeString);
            valueLabelText = DateUtil.dateToString(df.getCalender().getTime(), field.getFormatMask());
        }

        if (StringUtil.isNotBlank(valueLabelText)) {
            value.setText(valueLabelText);
        }

        styleHandler.styleDateOrTimeSelectorValue(value, field);

        String source = field.getSource();
        if (StringUtil.isNotBlank(source)) {
            Drawable drawable = MBResourceService.getInstance().getImageByID(source);
            value.setBackgroundDrawable(drawable);
        }

        value.setOnClickListener(getOnClickListener(field, df, value));
        container.addView(label);
        container.addView(value);

        return container;
    }

    protected abstract View.OnClickListener getOnClickListener(MBField field, MBDateField df, TextView value);

}
