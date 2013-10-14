/*
 * (C) Copyright ItudeMobile.
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
package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.itude.mobile.android.util.DateUtil;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.view.MBDateField;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class DateFieldBuilder extends DateTimeFieldBuilder
{

  @Override
  protected View.OnClickListener getOnClickListener(final MBField field, final MBDateField df, final TextView value)
  {
    return new View.OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener()
        {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
          {
            df.setDate(year, monthOfYear, dayOfMonth);

            field.setValue(DateUtil.dateToString(df.getCalender().getTime()));

            // Update our label
            value.setText(DateUtil.dateToString(df.getCalender().getTime(), field.getFormatMask()));

          }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(MBViewManager.getInstance(), listener, df.getYear(), df.getMonth(),
            df.getDay());
        datePickerDialog.setTitle(field.getLabel());
        getStyleHandler().styleDatePickerDialog(datePickerDialog, field);
        getStyleHandler().styleDatePickerDialog(datePickerDialog, value, field);

        datePickerDialog.show();

      }
    };

  }

}
