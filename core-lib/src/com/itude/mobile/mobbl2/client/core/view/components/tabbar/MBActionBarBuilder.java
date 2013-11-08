package com.itude.mobile.mobbl2.client.core.view.components.tabbar;

import java.util.EnumSet;

import android.support.v7.app.ActionBar;
import android.view.Menu;

import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBActionBarInvalidationOption;

public interface MBActionBarBuilder
{
  public void fillActionBar(ActionBar actionBar, Menu menu);

  public void showProgressIndicatorInTool();

  public void hideProgressIndicatorInTool();
  
  public void invalidateActionBar(EnumSet<MBActionBarInvalidationOption> flags);

  public void selectTabWithoutReselection(String dialogName);


}
