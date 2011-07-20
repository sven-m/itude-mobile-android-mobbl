package com.itude.mobile.mobbl2.client.core.view.components;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * @author Coen Houtman
 *
 * Container class containing {@link MBTab} views. Typically, this view will be placed in the ActionBar.
 * It could be used anywhere though, in theory.
 * 
 * Basically, this is a {@link android.widget.LinearLayout} with a horizontal orientation. It maintains a list
 * of MBTab instances and is responsible for controlling tab bar-like functionality.
 */
public class MBTabBar extends LinearLayout
{
  private List<MBTab> _tabs             = null;
  private MBTab       _selectedTab      = null;

  private int         _tabPaddingLeft   = 0;
  private int         _tabPaddingTop    = 0;
  private int         _tabPaddingRight  = 0;
  private int         _tabPaddingBottom = 0;

  public MBTabBar(Context context)
  {
    super(context);

    setOrientation(HORIZONTAL);
    setMotionEventSplittingEnabled(false);

    _tabs = new ArrayList<MBTab>();
  }

  /**
   * @param tab
   * 
   * Adds an {@link MBTab} to this tab bar. If there is no tab selected yet, the provided tab will be selected.
   */
  public void addTab(MBTab tab)
  {
    tab.setTabBar(this);
    tab.setPadding(_tabPaddingLeft, _tabPaddingTop, _tabPaddingRight, _tabPaddingBottom);
    _tabs.add(tab);

    addView(tab);
  }

  public void setTabPadding(int left, int top, int right, int bottom)
  {
    _tabPaddingLeft = left;
    _tabPaddingTop = top;
    _tabPaddingRight = right;
    _tabPaddingBottom = bottom;
  }

  /**
   * @return the selected tab or null if there is no tab selected
   */
  public MBTab getSelectedTab()
  {
    return _selectedTab;
  }

  public MBTab findTabById(int tabId)
  {
    for (MBTab tab : _tabs)
    {
      if (tabId == tab.getTabId())
      {
        return tab;
      }
    }
    return null;
  }

  public MBTab getTab(int position)
  {
    return _tabs.get(position);
  }

  public int indexOfSelectedTab()
  {
    return indexOf(_selectedTab);
  }

  public int indexOf(MBTab tab)
  {
    return _tabs.indexOf(tab);
  }

  public void selectTab(MBTab tab)
  {
    if (tab != null && tab.equals(_selectedTab))
    {
      _selectedTab.reselect();
    }
    else
    {
      if (_selectedTab != null)
      {
        _selectedTab.unselect();
      }

      if (tab != null)
      {
        tab.doSelect();
      }
      _selectedTab = tab;
    }
  }
}
