package com.itude.mobile.mobbl2.client.core.view.components;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.view.MBComponent;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBSegmentedControlContainer extends LinearLayout
{

  private final ArrayList<Button> _contentTitles;
  private final ArrayList<View>   _contentItems;
  private int                     _selectedIndex;
  private final LinearLayout      _contentContainer;
  private final MBStyleHandler    _styleHandler;

  public MBSegmentedControlContainer(Context context, MBPanel segmentedControlPanel)
  {
    super(context);

    _styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    _contentTitles = new ArrayList<Button>();
    _contentItems = new ArrayList<View>();
    _selectedIndex = 0;

    int childCount = segmentedControlPanel.getChildren().size();
    for (int i = 0; i < childCount; i++)
    {
      MBPanel childPanel = (MBPanel) segmentedControlPanel.getChildren().get(i);

      // Add the button item to the list
      _contentTitles.add(createSegmentedControlItem(childPanel, context, i, childCount));

      // Determine if this child should be focused after processing all views and titles
      if (childPanel.isFocused())
      {
        _selectedIndex = i;
      }

      // Add all subchildren to a container that will be our content
      LinearLayout subChildContainer = new LinearLayout(context);
      subChildContainer.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      subChildContainer.setOrientation(LinearLayout.VERTICAL);
      for (MBComponent subChild : childPanel.getChildren())
      {
        View contentView = subChild.buildViewWithMaxBounds(null);
        subChildContainer.addView(contentView);
      }
      _contentItems.add(subChildContainer);

    }

    /*
     * We've created our container, now let's add our buttons (tabs)
     */
    addView(createSegmentedControlBar(segmentedControlPanel, context));

    /*
     * Items done. Time to create our content container
     */
    _contentContainer = new LinearLayout(context);
    _contentContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    _styleHandler.styleSegmentedControlContentContainer(_contentContainer, segmentedControlPanel);
    addView(_contentContainer);

    /*
     * Seems like everything is set up. Now let's trigger our focused tab
     */
    setFocusedItem(_selectedIndex);
  }

  public void setFocusedItem(int itemIndex)
  {
    // 1. Make the control bar change it's items states
    if (itemIndex != _selectedIndex)
    {
      _contentTitles.get(_selectedIndex).setSelected(false);
    }
    _contentTitles.get(itemIndex).setSelected(true);
    _selectedIndex = itemIndex;

    // 2. Update the content of our content container
    _contentContainer.removeAllViews();
    _contentContainer.addView(_contentItems.get(itemIndex));
  }

  private Button createSegmentedControlItem(MBPanel panel, Context context, final int itemIndex, int count)
  {
    Button item = new Button(context);
    item.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
    item.setText(MBLocalizationService.getInstance().getTextForKey(panel.getTitle()));
    item.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        setFocusedItem(itemIndex);
      }
    });

    // Let's do some styling
    if (itemIndex == 0)
    {
      // Style our first item
      _styleHandler.styleFirstSegmentedItem(item, panel);
    }
    else if (itemIndex == (count - 1))
    {
      // Style our last item
      _styleHandler.styleLastSegmentedItem(item, panel);
    }
    else
    {
      // Style our centered items
      _styleHandler.styleCenterSegmentedItem(item, panel);
    }

    return item;
  }

  private LinearLayout createSegmentedControlBar(MBPanel panel, Context context)
  {
    LinearLayout segmentedControlBar = new LinearLayout(context);
    segmentedControlBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

    // TODO add stylehandler method
    _styleHandler.styleSegmentedControlBar(segmentedControlBar, panel);

    int size = _contentTitles.size();
    for (int i = 0; i < size; i++)
    {
      Button item = _contentTitles.get(i);
      segmentedControlBar.addView(item);

    }

    return segmentedControlBar;
  }
}
