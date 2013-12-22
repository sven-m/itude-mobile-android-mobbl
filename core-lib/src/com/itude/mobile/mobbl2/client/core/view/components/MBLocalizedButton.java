/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
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
package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;

public class MBLocalizedButton extends Button
{

  public MBLocalizedButton(Context context)
  {
    super(context);
  }

  public MBLocalizedButton(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public MBLocalizedButton(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
  }

  @Override
  public void setText(CharSequence text, BufferType type)
  {
    super.setText(getValue(text.toString()), type);
  }

  protected String getValue(String text)
  {
    if (isInEditMode())
    {
      // We're previewing the XML file. Therefore, there is no MOBBL configuration loaded: just show the key
      return text;
    }
    else
    {
      return MBLocalizationService.getInstance().getTextForKey(text.toString());
    }
  }

}
