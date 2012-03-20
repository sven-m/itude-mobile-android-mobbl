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

  public void setSelectedElement(int selectedElement)
  {
    _selectedElement = selectedElement;
  }

  public int getSelectedElement()
  {
    return _selectedElement;
  }

}
