package com.itude.mobile.mobbl2.client.core.view.components;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.controller.MBTabletViewManager;
import com.itude.mobile.mobbl2.client.core.util.Constants;

/**
 * @author Coen Houtman
 *
 * Container class containing {@link MBTab} views. Typically, this view will be placed in the ActionBar.
 * It could be used anywhere though, in theory.
 * 
 * Basically, this is a {@link android.widget.LinearLayout} with a horizontal orientation. It maintains a list
 * of MBTab instances and is responsible for controlling tab bar-like functionality.
 */
public class MBTabBar extends LinearLayout implements OnClickListener
{
  private List<MBTab> _tabs        = null;
  private MBTab       _selectedTab = null;

  public MBTabBar(Context context)
  {
    super(context);

    setOrientation(HORIZONTAL);

    _tabs = new ArrayList<MBTab>();
  }

  /**
   * @param tab
   * 
   * Adds an {@link MBTab} to this tab bar. If there is no tab selected yet, the provided tab will be selected.
   */
  public void addTab(MBTab tab)
  {
    tab.setOnClickListener(this);
    _tabs.add(tab);
    addView(tab);
    if (_selectedTab == null)
    {
      tab.setSelected(true);
      _selectedTab = tab;
    }
  }

  public MBTab getSelectedTab()
  {
    return _selectedTab;
  }

  public int indexOfSelectedTab()
  {
    return indexOf(_selectedTab);
  }

  public int indexOf(MBTab tab)
  {
    return _tabs.indexOf(tab);
  }

  /**
   * @param position
   * 
   * Select the tab at the specified position. If the position is out of range, the request is ignored.
   */
  public void selectTab(int position)
  {
    MBTab tab;
    try
    {
      tab = _tabs.get(position);
    }
    catch (Exception e)
    {
      Log.e(Constants.APPLICATION_NAME, "Unable to select tab with position: " + position, e);
      return;
    }

    _selectedTab.unselect();
    tab.select();
    _selectedTab = tab;
  }

  /* (non-Javadoc)
   * @see android.view.View.OnClickListener#onClick(android.view.View)
   */
  @Override
  public void onClick(View view)
  {
    MBTab tab = (MBTab) view;

    if (tab == _selectedTab)
    {
      tab.reselect();
    }
    else
    {
      tab.select();
      _selectedTab.unselect();
      _selectedTab = tab;
      MBTabletViewManager.getInstance().invalidateActionBar();
    }
  }

}
