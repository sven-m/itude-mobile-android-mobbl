package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.widget.ArrayAdapter;

public class MBSpinnerAdapter extends ArrayAdapter<CharSequence>
{
  private int _selectedElement = 0;

  public MBSpinnerAdapter(Context context, int textViewResourceId)
  {
    super(context, textViewResourceId);
  }

  //
  //  @Override
  //  public View getView(int position, View convertView, ViewGroup parent)
  //  {
  //    if (position == _selectedElement)
  //    {
  //      view.setSelected(true);
  //    }
  //    View view = super.getView(position, convertView, parent);
  //
  //
  //    return view;
  //  }

  //
  //  @Override
  //  public View getDropDownView(int position, View convertView, ViewGroup parent)
  //  {
  //    // TODO Auto-generated method stub
  //    View view = super.getDropDownView(position, convertView, parent);
  //
  //    if (position == _selectedElement)
  //    {
  //      view.setSelected(true);
  //    }
  //
  //    return view;
  //
  //  }

  public void setSelectedElement(int selectedElement)
  {
    _selectedElement = selectedElement;
  }

  public int getSelectedElement()
  {
    return _selectedElement;
  }

}
