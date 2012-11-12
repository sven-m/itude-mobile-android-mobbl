package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.DateUtilities;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.view.MBDateField;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;

public abstract class DateTimeFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    final Context context = MBApplicationController.getInstance().getBaseContext();

    final MBStyleHandler styleHandler = getStyleHandler();

    final MBDocument doc = field.getDocument();
    final String path = field.getPath();

    final MBDateField df = new MBDateField();

    // Create our container which will fill the whole width
    LinearLayout container = new LinearLayout(context);
    container.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    container.setGravity(Gravity.CENTER_VERTICAL);

    // Add our label (if one exists)
    TextView label = buildTextViewWithValue(field.getLabel());
    label.setGravity(Gravity.CENTER_VERTICAL);
    label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 50));

    styleHandler.styleLabel(label, field);

    final TextView value = new TextView(context);
    value.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 50));
    value.setGravity(Gravity.CENTER_VERTICAL);

    // Find out if we have previously set a time
    String dateTimeString = doc.getValueForPath(path);
    String valueLabelText = "";

    String nillValue = field.getValueIfNil();
    if (StringUtilities.isNotBlank(nillValue))
    {
      valueLabelText = field.getValueIfNil();
    }

    if (StringUtilities.isNotBlank(dateTimeString))
    {
      df.setTime(dateTimeString);
      valueLabelText = DateUtilities.dateToString(df.getCalender().getTime(), field.getFormatMask());
    }

    if (StringUtilities.isNotBlank(valueLabelText))
    {
      value.setText(valueLabelText);
    }

    styleHandler.styleDateOrTimeSelectorValue(value, field);

    String source = field.getSource();
    if (StringUtilities.isNotBlank(source))
    {
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
