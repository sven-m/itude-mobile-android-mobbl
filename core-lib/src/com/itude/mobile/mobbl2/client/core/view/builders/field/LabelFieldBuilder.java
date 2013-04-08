package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.view.MBField;

public class LabelFieldBuilder extends MBBaseFieldBuilder
{
  @Override
  public View buildField(MBField field)
  {

    String value = field.getValuesForDisplay();

    TextView label = buildTextViewWithValue(value);
    label.setSingleLine(true);
    label.setEllipsize(TruncateAt.END);
    getStyleHandler().styleLabel(label, field);

    return label;
  }
}
