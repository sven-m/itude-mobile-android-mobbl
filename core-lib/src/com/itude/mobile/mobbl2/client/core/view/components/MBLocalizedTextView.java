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
package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBLocalizedTextView extends TextView
{

  public MBLocalizedTextView(Context context)
  {
    super(context);
  }

  public MBLocalizedTextView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public MBLocalizedTextView(Context context, AttributeSet attrs, int defStyle)
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
    String mbText;
    try
    {
      mbText = MBLocalizationService.getInstance().getTextForKey(text.toString());
    }
    catch (MBException mbe)
    {
      // You are probably previewing the XML, and MOBBL isn't loaded at this point. 
      Log.w(Constants.APPLICATION_NAME, mbe.getMessage());
      mbText = text.toString();
    }
    return mbText;
  }

}
