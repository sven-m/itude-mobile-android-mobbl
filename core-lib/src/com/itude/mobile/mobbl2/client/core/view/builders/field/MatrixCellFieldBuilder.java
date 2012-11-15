package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.view.MBField;

public class MatrixCellFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    String value = field.getValuesForDisplay();

    // Title TextView
    TextView label = buildTextViewWithValue(value);
    label.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
    label.setSingleLine();

    // default styling
    getStyleHandler().styleMatrixCell(field, label);
    return label;
  }

}
