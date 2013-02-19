package com.itude.mobile.mobbl2.client.core.view.listeners;

import android.annotation.TargetApi;
import android.os.Build;

import com.itude.mobile.mobbl2.client.core.view.components.tabbar.MBTab;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public interface MBTabListenerI
{
  public void onTabSelected(MBTab tab);

  public void onTabUnselected(MBTab tab);

  public void onTabReselected(MBTab tab);
}
