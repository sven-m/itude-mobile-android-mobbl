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
package com.itude.mobile.mobbl.core.view.builders.field;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.services.MBLocalizationService;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.MBStringUtil;
import com.itude.mobile.mobbl.core.view.MBField;

public class InputFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {

    Context context = MBApplicationController.getInstance().getBaseContext();

    EditText inputField = new EditText(context);

    // Default inputfield should be single lined

    String hint = field.getHint();
    if (StringUtil.isNotBlank(hint))
    {
      getStyleHandler().styleHint(inputField);
      inputField.setHint(MBLocalizationService.getInstance().getTextForKey(hint));
      // http://code.google.com/p/android/issues/detail?id=7252
      inputField.setEllipsize(TruncateAt.END);
    }

    inputField.setSingleLine();
    inputField.setOnKeyListener(field);
    getStyleHandler().styleInputfieldBackgroundWithName(inputField, null);

    inputField.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
    String defaultValue = "";
    if (field.getPath() != null)
    {
      String fieldValue = field.getValue();
      if (fieldValue != null)
      {
        defaultValue = fieldValue;
      }
      else if (field.getValueIfNil() != null)
      {
        defaultValue = field.getValueIfNil();
      }
    }

    // Set type of value (so different keyboard will be shown)
    if (field.getDataType() != null && field.getDataType().equals(Constants.C_FIELD_DATATYPE_INT))
    {
      inputField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

      try
      {
        if (!StringUtil.isEmpty(defaultValue)) Integer.parseInt(defaultValue);
        inputField.setText(defaultValue);
      }
      catch (Exception e)
      {
        MBLog.w(Constants.APPLICATION_NAME, "Inputfield with type \"" + field.getDataType() + "\" cannot have the value \"" + defaultValue
                                            + "\"", e);
      }
    }
    else if (field.getDataType() != null
             && (field.getDataType().equals(Constants.C_FIELD_DATATYPE_DOUBLE) || field.getDataType()
                 .equals(Constants.C_FIELD_DATATYPE_FLOAT)))
    {
      inputField.setKeyListener(MBStringUtil.getCurrencyNumberKeyListener());

      try
      {
        if (!StringUtil.isEmpty(defaultValue)) Double.parseDouble(defaultValue);
        inputField.setText(defaultValue);
      }
      catch (Exception e)
      {
        MBLog.w(Constants.APPLICATION_NAME, "Inputfield with type \"" + field.getDataType() + "\" cannot have the value \"" + defaultValue
                                            + "\"", e);
      }

      // Depending on the localeCode-settings in the applicationProperties, we want to display a comma or a dot as decimal seperator for floats and doubles
      if (MBLocalizationService.getInstance().getLocaleCode() != null
          && (MBLocalizationService.getInstance().getLocaleCode().equals(Constants.C_LOCALE_CODE_DUTCH) || MBLocalizationService
              .getInstance().getLocaleCode().equals(Constants.C_LOCALE_CODE_ITALIAN)))
      {

        if (inputField.getText().toString().length() > 0)
        {
          String textFieldText = StringUtil.formatNumberWithOriginalNumberOfDecimals(inputField.getText().toString(), MBLocalizationService
              .getInstance().getLocale());
          inputField.setText(textFieldText);
        }

      }

    }
    else
    {
      inputField.setText(defaultValue);
    }

    // Add TextChangedListener to EditText so changes will be saved to the document
    inputField.addTextChangedListener(field);

    getStyleHandler().styleTextfield(inputField, field);

    if (field.getLabel() != null && field.getLabel().length() > 0)
    {
      inputField.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 50));

      LinearLayout labelLayout = new LinearLayout(context);
      labelLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
      labelLayout.setOrientation(LinearLayout.HORIZONTAL);

      TextView label = buildTextViewWithValue(field.getLabel());
      getStyleHandler().styleLabel(label, field);
      label.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 50));

      labelLayout.addView(label);
      labelLayout.addView(inputField);

      return labelLayout;
    }

    return inputField;
  }

}
