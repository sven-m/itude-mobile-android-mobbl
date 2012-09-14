package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

/**
 * @deprecated
 *
 */
public class MBSegmentedControl extends RadioGroup
{
  public MBSegmentedControl(Context context)
  {
    super(context);
    setOrientation(RadioGroup.HORIZONTAL);
  }

  public void addItem(int id, String text)
  {
    addItem(id, getChildCount(), text);
  }

  public void addItem(int id, int index, String text)
  {
    LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
        RadioGroup.LayoutParams.WRAP_CONTENT, 1);
    MBSegmentedItem item = new MBSegmentedItem(getContext());

    item.setPadding(0, MBScreenUtilities.FIVE, 0, MBScreenUtilities.FIVE);
    item.setId(id);
    item.setText(text);

    MBViewBuilderFactory.getInstance().getStyleHandler().styleSegmentedItem(item);

    addView(item, index, layoutParams);
  }
}