package com.itude.mobile.mobbl2.client.core.view.components.tabbar;

import android.annotation.TargetApi;
import android.os.Build;

import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.view.listeners.MBTabListenerI;

/**
 * @author Coen Houtman
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MBTabListener implements MBTabListenerI
{
  private final String _dialog;

  public MBTabListener(String dialogName)
  {
    _dialog = dialogName;
  }

  @Override
  public void onTabReselected(MBTab tab)
  {
    MBViewManager.getInstance().activateDialogWithName(_dialog);
  }

  @Override
  public void onTabSelected(MBTab tab)
  {
    MBViewManager.getInstance().activateDialogWithName(_dialog);
  }

  @Override
  public void onTabUnselected(MBTab tab)
  {
  }
}
