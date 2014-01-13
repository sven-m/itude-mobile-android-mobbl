/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl.core.view.components.tabbar;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewGroupCompat;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl.core.view.listeners.MBTabListenerI;

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
    ViewGroupCompat.setMotionEventSplittingEnabled(this, false);

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

  public MBTab findTab(String name)
  {
    for (MBTab tab : _tabs)
    {
      if (name.equals(tab.getName()))
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

  public boolean isEmpty()
  {
    return _tabs.isEmpty();
  }

  public void selectTabWithoutReselection(String name)
  {
    MBTab tab = findTab(name);
    if (tab != null && tab.equals(_selectedTab)) return;
    else selectTab(tab, false);
  }

  public void selectTab(MBTab tab, boolean notifyListener)
  {
    if (tab != null && tab.equals(_selectedTab))
    {
      if (notifyListener)
      {
        _selectedTab.reselect();
      }
      else
      {
        MBTabListenerI listener = _selectedTab.getListener();
        _selectedTab.setListener(null);
        _selectedTab.reselect();
        _selectedTab.setListener(listener);
      }
    }
    else
    {
      if (_selectedTab != null)
      {
        _selectedTab.unselect();
      }

      _selectedTab = tab;

      if (tab != null)
      {
        if (notifyListener)
        {
          tab.select();
        }
        else
        {
          MBTabListenerI listener = tab.getListener();
          tab.setListener(null);
          tab.select();
          tab.setListener(listener);
        }
      }

    }
  }
}
