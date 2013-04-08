package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.view.View;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.view.MBField;


public class MatrixDescriptionFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    String value = field.getValuesForDisplay();

    // Title TextView
    TextView label = buildTextViewWithValue(value);

    getStyleHandler().styleMatrixRowDescription(label, field);

    return label;
  }

}
