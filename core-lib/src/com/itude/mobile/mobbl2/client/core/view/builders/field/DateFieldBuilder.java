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
