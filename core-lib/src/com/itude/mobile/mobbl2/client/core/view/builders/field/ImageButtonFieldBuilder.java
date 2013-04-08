package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class ImageButtonFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    String source = field.getSource();
    if (StringUtil.isBlank(source))
    {
      Log.w(Constants.APPLICATION_NAME, "Source is null or empty for field");
      return null;
    }

    ImageButton button = new ImageButton(MBApplicationController.getInstance().getBaseContext());
    button.setOnClickListener(field);
    button.setOnKeyListener(field);

    Drawable drawable = MBResourceService.getInstance().getImageByID(source);
    button.setBackgroundDrawable(drawable);

    getStyleHandler().styleImageButton(button, field);

    return button;
  }

}
