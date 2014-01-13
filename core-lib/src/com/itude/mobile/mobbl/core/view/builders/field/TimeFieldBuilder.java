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

import android.app.TimePickerDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TimePicker;

import com.itude.mobile.android.util.DateUtil;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.view.MBDateField;
import com.itude.mobile.mobbl.core.view.MBField;

public class TimeFieldBuilder extends DateTimeFieldBuilder
{

  @Override
  protected View.OnClickListener getOnClickListener(final MBField field, final MBDateField df, final TextView value)
  {

    return new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener()
        {
          @Override
          public void onTimeSet(TimePicker view, int hourOfDay, int minute)
          {
            df.setTime(hourOfDay, minute);

            field.setValue(DateUtil.dateToString(df.getCalender().getTime()));

            // Update our label
            value.setText(DateUtil.dateToString(df.getCalender().getTime(), field.getFormatMask()));
          }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(MBViewManager.getInstance(), listener, df.getHourOfDay(), df.getMinute(),
            true);

        timePickerDialog.setTitle(field.getLabel());
        getStyleHandler().styleTimePickerDialog(timePickerDialog, field);

        timePickerDialog.show();

      }
    };
  }
}
