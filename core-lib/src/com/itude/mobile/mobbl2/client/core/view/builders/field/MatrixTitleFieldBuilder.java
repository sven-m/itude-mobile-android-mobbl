package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.view.View;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBField;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
public class MatrixTitleFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    String value = field.getValuesForDisplay();

    // Title TextView
    TextView label = buildTextViewWithValue(value);

    // Decide which styling to apply
    if (((MBPanel) field.getParent()).getType().equals(Constants.C_MATRIXHEADER))
    {
      getStyleHandler().styleMatrixHeaderTitle(label);
    }
    else if (((MBPanel) field.getParent()).getType().equals(Constants.C_MATRIXROW))
    {
      getStyleHandler().styleMatrixRowTitle(label, field);
    }

    // Make sure only 1 line is visible when creating the title
    label.setSingleLine(true);

    return label;
  }

}
