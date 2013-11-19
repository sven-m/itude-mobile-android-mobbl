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
    returnView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
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
