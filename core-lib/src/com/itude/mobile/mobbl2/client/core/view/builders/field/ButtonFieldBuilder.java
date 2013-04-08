package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class ButtonFieldBuilder extends MBBaseFieldBuilder
{
  @Override
  public View buildField(MBField field)
  {

    MarginLayoutParams buttonParams = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);

    //    buttonParams.setMargins(ScreenUtil.FIVE, 0, ScreenUtil.FIVE, 0);
    Button button = new Button(MBApplicationController.getInstance().getBaseContext());
    button.setLayoutParams(buttonParams);

    String defaultValue = field.getLabel();

    String path = field.getPath();
    if (StringUtil.isBlank(defaultValue) && StringUtil.isNotBlank(path))
    {
      String fieldValue = field.getValue();
      if (StringUtil.isNotBlank(fieldValue))
      {
        defaultValue = fieldValue;
      }
      else if (StringUtil.isNotBlank(field.getValueIfNil()))
      {
        defaultValue = field.getValueIfNil();
      }
    }
    button.setText(defaultValue);
    button.setOnClickListener(field);
    button.setOnKeyListener(field);

    String source = field.getSource();
    if (source != null)
    {
      Drawable drawable = MBResourceService.getInstance().getImageByID(source);
      button.setBackgroundDrawable(drawable);
    }
    else
    {
      getStyleHandler().styleButton(button, field);
    }
    return button;

  }
}
