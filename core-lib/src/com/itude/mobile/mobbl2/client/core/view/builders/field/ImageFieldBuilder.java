package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class ImageFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    String source = field.getSource();
    String path = field.getPath();
    if (StringUtilities.isBlank(source) && StringUtilities.isBlank(path))
    {
      Log.w(Constants.APPLICATION_NAME, "Source or Path is null or empty for field");
      return null;
    }

    ImageView image = new ImageView(MBApplicationController.getInstance().getBaseContext());
    if (StringUtilities.isNotBlank(field.getOutcomeName()))
    {
      image.setOnClickListener(field);
    }

    Drawable drawable = null;
    if (StringUtilities.isNotBlank(source))
    {
      drawable = MBResourceService.getInstance().getImageByID(source);
    }
    else
    {
      drawable = MBResourceService.getInstance().getImageByURL(field.getFormattedValue());
    }
    image.setBackgroundDrawable(drawable);

    getStyleHandler().styleImage(image);
    getStyleHandler().styleImage(image, field.getStyle());

    return image;
 }

}