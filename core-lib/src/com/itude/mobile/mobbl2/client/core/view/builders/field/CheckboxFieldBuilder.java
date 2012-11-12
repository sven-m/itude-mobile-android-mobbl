package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class CheckboxFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    String value = field.getValue();
    String valueIfNil = field.getValueIfNil();
    boolean checked = false;

    if ((value != null && value.equalsIgnoreCase("TRUE")) || (valueIfNil != null && valueIfNil.equalsIgnoreCase("TRUE")))
    {
      checked = true;
    }

    Context context = MBApplicationController.getInstance().getBaseContext();

    final CheckBox checkBox = new CheckBox(context);
    checkBox.setId(UniqueIntegerGenerator.getId());
    checkBox.setChecked(checked);
    checkBox.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    checkBox.setOnCheckedChangeListener(field);
    checkBox.setOnKeyListener(field);

    getStyleHandler().styleCheckBox(checkBox);

    RelativeLayout container = new RelativeLayout(context);
    RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    container.setLayoutParams(rlParams);
    container.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        checkBox.toggle();
      }
    });

    if (field.getLabel() != null && field.getLabel().length() > 0)
    {
      RelativeLayout.LayoutParams cbParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      cbParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      checkBox.setLayoutParams(cbParams);

      RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
          RelativeLayout.LayoutParams.WRAP_CONTENT);
      labelParams.addRule(RelativeLayout.LEFT_OF, checkBox.getId());
      labelParams.addRule(RelativeLayout.CENTER_VERTICAL);

      TextView label = buildTextViewWithValue(field.getLabel());
      label.setLayoutParams(labelParams);
      getStyleHandler().styleLabel(label, field);

      container.addView(label);
    }

    container.addView(checkBox);

    return container;
  }

}
