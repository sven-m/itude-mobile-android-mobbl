package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import com.itude.mobile.mobbl2.client.core.view.MBField;

public class PasswordFieldBuilder extends InputFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    EditText inputField = (EditText) super.buildField(field);

    inputField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    inputField.setTransformationMethod(new PasswordTransformationMethod());

    return inputField;
  }

}
