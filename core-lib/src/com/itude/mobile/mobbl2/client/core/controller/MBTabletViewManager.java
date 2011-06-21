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
  //  @Override
  //  public boolean onCreateOptionsMenu(Menu menu)
  //  {
  //    for (String dialogName : getSortedDialogNames())
  //    {
  //      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
  //      MenuItem menuItem = menu.add(Menu.NONE, dialogName.hashCode(), Menu.NONE, dialogDefinition.getTitle());
  //      menuItem.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));
  //      MenuCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);
  //      if (dialogName.contains("search"))
  //      {
  //        Log.d("coen", "setting action view");
  //        menuItem.setActionView(new SearchView(this));
  //        menuItem.
  //      }
  //    }
  //
  //    return true;
  //  }

  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId)
  {
    return false;
  }
}
