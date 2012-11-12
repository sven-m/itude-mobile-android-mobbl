package com.itude.mobile.mobbl2.client.core.view.builders.field;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.view.MBField;

public class DropdownListFieldBuilder extends MBBaseFieldBuilder
{

  @Override
  public View buildField(MBField field)
  {
    Context context = MBApplicationController.getInstance().getViewManager();

    int selected = -1;

    Spinner dropdownList = new Spinner(context);
    dropdownList.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));

    getStyleHandler().styleSpinner(dropdownList);

    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item)
    {

      @Override
      public View getView(int position, View convertView, ViewGroup parent)
      {
        View view = super.getView(position, convertView, parent);
        if (view instanceof TextView)
        {
          TextView textView = (TextView) view;
          getStyleHandler().styleLabel(textView, null);
        }
        return view;
      }
    };

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    String fieldValue = field.getValue();
    if (field.getDomain() != null)
    {
      for (int i = 0; i < field.getDomain().getDomainValidators().size(); i++)
      {
        MBDomainValidatorDefinition domDef = field.getDomain().getDomainValidators().get(i);
        adapter.add(MBLocalizationService.getInstance().getTextForKey(domDef.getTitle()));

        String domDefValue = domDef.getValue();
        if ((fieldValue != null && fieldValue.equals(domDefValue))
            || (fieldValue == null && field.getValueIfNil() != null && field.getValueIfNil().equals(domDefValue)))
        {
          selected = i;
        }
      }
    }

    dropdownList.setAdapter(adapter);

    if (selected > -1)
    {
      dropdownList.setSelection(selected);
    }

    dropdownList.setOnItemSelectedListener(field);
    dropdownList.setOnKeyListener(field);

    if (field.getLabel() != null && field.getLabel().length() > 0)
    {
      dropdownList.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 50));

      LinearLayout labelLayout = new LinearLayout(context);
      labelLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      labelLayout.setOrientation(LinearLayout.HORIZONTAL);
      labelLayout.setGravity(Gravity.CENTER_VERTICAL);
      getStyleHandler().styleLabelContainer(labelLayout, field);

      TextView label = buildTextViewWithValue(field.getLabel());
      label.setText(field.getLabel());
      label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 50));
      getStyleHandler().styleLabel(label, field);

      labelLayout.addView(label);
      labelLayout.addView(dropdownList);

      return labelLayout;
    }

    return dropdownList;
  }

}
