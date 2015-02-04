package com.itude.mobile.mobbl.core.view.bindings;

import android.widget.EditText;
import android.widget.TextView;

import com.itude.mobile.mobbl.core.view.MBField;

public class TextBinder extends SingleViewBinder<TextView, MBField>
{
  protected TextBinder(int id)
  {
    super(id);
  }

  public static TextBinder getInstance(int id)
  {
    return new TextBinder(id);
  }

  @Override
  protected void bindSingleView(TextView view, MBField component)
  {
    view.setText(component.getValuesForDisplay());
    if (view instanceof EditText) view.addTextChangedListener(component);
  }

}