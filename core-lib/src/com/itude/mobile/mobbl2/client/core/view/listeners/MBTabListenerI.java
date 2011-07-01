package com.itude.mobile.mobbl2.client.core.view.listeners;

import com.itude.mobile.mobbl2.client.core.view.components.MBTab;

public interface MBTabListenerI
{
  public void onTabSelected(MBTab tab);

  public void onTabUnselected(MBTab tab);

  public void onTabReselected(MBTab tab);
}
