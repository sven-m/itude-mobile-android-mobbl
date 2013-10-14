/*
 * (C) Copyright ItudeMobile.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.imagecache.ImageUtil;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class ImageFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    String source = field.getSource();
    String path = field.getPath();
    if (StringUtil.isBlank(source) && StringUtil.isBlank(path))
    {
      Log.w(Constants.APPLICATION_NAME, "Source or Path is null or empty for field");
      return null;
    }

    ImageView image = new ImageView(MBApplicationController.getInstance().getBaseContext());
    if (StringUtil.isNotBlank(field.getOutcomeName()))
    {
      image.setOnClickListener(field);
    }

    Drawable drawable = null;
    if (StringUtil.isNotBlank(source))
    {
      drawable = MBResourceService.getInstance().getImageByID(source);
      image.setImageDrawable(drawable);
    }
    else
    {
      ImageUtil.loadImage(image, field.getValue());
    }

    getStyleHandler().styleImage(image);
    getStyleHandler().styleImage(image, field.getStyle());

    return image;
  }

}
