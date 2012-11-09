package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.view.View;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.view.MBField;

public class SublabelFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    String value = field.getValuesForDisplay();

    TextView label = buildTextViewWithValue(value);
    getStyleHandler().styleSubLabel(label);
    getStyleHandler().styleSubLabel(label, field.getStyle());

    return label;
  }

}
