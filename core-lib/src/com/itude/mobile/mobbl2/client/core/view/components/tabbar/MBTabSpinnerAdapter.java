package com.itude.mobile.mobbl2.client.core.view.components.tabbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.widget.ArrayAdapter;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MBTabSpinnerAdapter extends ArrayAdapter<CharSequence>
{
  private int _selectedElement = 0;

  public MBTabSpinnerAdapter(Context context, int textViewResourceId)
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
