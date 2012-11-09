package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.view.builders.MBFieldViewBuilder.Builder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilder;

public abstract class MBBaseFieldBuilder extends MBViewBuilder implements Builder
{
  protected static enum TextType {
    plain, html
  }

  protected TextView buildTextViewWithValue(String value)
  {
    return buildTextViewWithValue(value, TextType.plain);
  }

  protected TextView buildTextViewWithValue(String value, TextType type)
  {
    TextView label = new TextView(MBApplicationController.getInstance().getBaseContext());
    if (value == null)
    {
      // If the value is null we don't want it to be parsed as HTML since that will break the application
      label.setText("");
    }
    else
    {
      label.setEllipsize(TruncateAt.END);

      if (type == TextType.html)
      {
        label.setText(Html.fromHtml(value));
      }
      else
      {
        label.setText(value);
      }
    }

    getStyleHandler().styleLabel(label, null);

    return label;
  }

}
