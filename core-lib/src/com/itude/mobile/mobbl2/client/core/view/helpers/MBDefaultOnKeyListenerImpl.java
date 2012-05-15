package com.itude.mobile.mobbl2.client.core.view.helpers;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;

public class MBDefaultOnKeyListenerImpl implements OnKeyListener
{

  public boolean onKey(View v, int keyCode, KeyEvent event)
  {
    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
    {
      MBViewManager.getInstance().onKeyDown(keyCode, event);
      return true;
    }
    else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_MENU)
    {
      MBViewManager.getInstance().onMenuKeyDown(keyCode, event, v);
      return true;
    }

    return false;
  }

}
