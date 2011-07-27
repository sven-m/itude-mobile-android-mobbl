package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBArrayAdapter extends ArrayAdapter<CharSequence>
{

  private int _selectedElement = 0;

  public MBArrayAdapter(Context context, int textViewResourceId)
  {
    super(context, textViewResourceId);
  }

  public View getDropDownView(int position, View convertView, android.view.ViewGroup parent)
  {
    View v = super.getDropDownView(position, convertView, parent);
    if (getSelectedElement() == position) 
    {
      MBViewBuilderFactory.getInstance().getStyleHandler().applyDropDownView(v);
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
