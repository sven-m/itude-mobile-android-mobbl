package com.itude.mobile.mobbl2.client.core.view.components;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBSegmentedControlBar extends LinearLayout
{

  private final MBStyleHandler    _styleHandler;
  private int                     _selectedIndex = 0;

  private MBOnSelectedListener    _onSelectedListener;
  private MBOnClickListener       _onClickListener;

  private final ArrayList<Button> _itemButtons;

  public MBSegmentedControlBar(Context context, List<String> titles, String style)
  {
    super(context);

    _itemButtons = new ArrayList<Button>();

    _styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();
    _styleHandler.styleSegmentedControlBar(this, style);

    /*
     * Let's process our titles
     */
    processTitles(context, titles, style);

  }

  private void processTitles(Context context, List<String> titles, String style)
  {
    int childCount = titles.size();
    for (int i = 0; i < childCount; i++)
    {

      String title = titles.get(i);

      // Add the button item to the list
      Button button = createSegmentedControlItem(context, title, style, i, childCount);
      _itemButtons.add(button);
      addView(button);
    }

  }

  private Button createSegmentedControlItem(Context context, String title, String style, final int itemIndex, final int count)
  {
    Button item = new Button(context);
    item.setText(title);
    item.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        if (getMBOnSelectedListener() != null && _selectedIndex != itemIndex)
        {
          getMBOnSelectedListener().onSelected(itemIndex, count);
        }
        if (getMBOnClickListener() != null)
        {
          _onClickListener.onClick(itemIndex, count);
        }

        setFocusedItem(itemIndex);

      }
    });

    // Let's do some styling
    if (itemIndex == 0)
    {
      // Style our first item
      _styleHandler.styleFirstSegmentedItem(item, style);
    }
    else if (itemIndex == (count - 1))
    {
      // Style our last item
      _styleHandler.styleLastSegmentedItem(item, style);
    }
    else
    {
      // Style our centered items
      _styleHandler.styleCenterSegmentedItem(item, style);
    }

    return item;
  }

  public void setFocusedItem(int itemIndex)
  {
    /*
     * Make the control bar change it's items states
     */
    if (itemIndex != _selectedIndex)
    {
      _itemButtons.get(_selectedIndex).setSelected(false);
    }
    _itemButtons.get(itemIndex).setSelected(true);
    _selectedIndex = itemIndex;
  }

  public int getSelectedIndex()
  {
    return _selectedIndex;
  }

  public void setSelectedIndex(int selectedIndex)
  {
    _selectedIndex = selectedIndex;
  }

  public MBOnSelectedListener getMBOnSelectedListener()
  {
    return _onSelectedListener;
  }

  public void setMBOnSelectedListener(MBOnSelectedListener onSelectedListener)
  {
    _onSelectedListener = onSelectedListener;
  }

  public MBOnClickListener getMBOnClickListener()
  {
    return _onClickListener;
  }

  public void setMBOnClickListener(MBOnClickListener onClickListener)
  {
    _onClickListener = onClickListener;
  }

  public interface MBOnSelectedListener
  {
    void onSelected(int selectedIndex, int itemCount);
  }

  public interface MBOnClickListener
  {
    void onClick(int clickedIndex, int itemCount);
  }

}
