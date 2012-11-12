package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class TextFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    String value = field.getValuesForDisplay();

    // Title TextView
    TextView returnView = buildTextViewWithValue(value, TextType.html);
    returnView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    returnView.setEllipsize(null);

    if (field.getAlignment() != null)
    {
      if (field.getAlignment().equals(Constants.C_ALIGNMENT_RIGHT))
      {
        returnView.setGravity(Gravity.RIGHT);
      }
      else if (field.getAlignment().equals(Constants.C_ALIGNMENT_LEFT))
      {
        returnView.setGravity(Gravity.LEFT);
      }
      else if (field.getAlignment().equals(Constants.C_ALIGNMENT_CENTER_VERTICAL))
      {
        returnView.setGravity(Gravity.CENTER_VERTICAL);
      }
      else if (field.getAlignment().equals(Constants.C_ALIGNMENT_CENTER))
      {
        returnView.setGravity(Gravity.CENTER);
      }
    }

    getStyleHandler().styleTextView(returnView, field);

    return returnView;
  }

}
