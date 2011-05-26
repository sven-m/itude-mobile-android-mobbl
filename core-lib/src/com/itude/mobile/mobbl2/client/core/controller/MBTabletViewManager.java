package com.itude.mobile.mobbl2.client.core.controller;

import android.app.ActionBar;

/**
 * @author Coen Houtman
 *
 *  This ViewManager can be used to perform actions that cannot be done on pre-Honeycomb devices.
 *  For example the use of the ActionBar.
 */
public class MBTabletViewManager extends MBViewManager implements ActionBar.OnNavigationListener
{
  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId)
  {
    return false;
  }
}
