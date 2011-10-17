package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

public class MBSpinnerAdapter extends ArrayAdapter<CharSequence>
{

  private int _selectedElement = 0;

  public MBSpinnerAdapter(Context context)
  {
    super(context, android.R.layout.simple_spinner_item);
    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  }

  public View getDropDownView(int position, View convertView, android.view.ViewGroup parent)
  {
    View v = super.getDropDownView(position, convertView, parent);

    if (getSelectedElement() == position)
    {
      //      MBViewBuilderFactory.getInstance().getStyleHandler().styleSelectedItem(v);
      // probably useful for setting the state and give a statelistdrawable in the styles.xml
      v.setSelected(true);
    }
    return v;
  }

  public void setSelectedElement(int selectedElement)
  {
    _selectedElement = selectedElement;
  }

  public int getSelectedElement()
  {
    return _selectedElement;
  }

}
