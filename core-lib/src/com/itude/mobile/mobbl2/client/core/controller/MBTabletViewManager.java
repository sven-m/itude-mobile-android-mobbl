package com.itude.mobile.mobbl2.client.core.controller;

import android.app.ActionBar;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.controller.util.MBTabListener;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.view.components.MBSpinner;
import com.itude.mobile.mobbl2.client.core.view.components.MBTab;
import com.itude.mobile.mobbl2.client.core.view.components.MBTabBar;

/**
 * @author Coen Houtman
 *
 *  This ViewManager can be used to perform actions that cannot be done on pre-Honeycomb devices.
 *  For example the use of the ActionBar.
 */
public class MBTabletViewManager extends MBViewManager
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
  public void populateActionBar(final int select)
  {
    runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        final ActionBar actionBar = getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        MBTabBar tabBar = new MBTabBar(MBTabletViewManager.this);

        for (String dialogName : getSortedDialogNames())
        {
          MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);

          //FIXME create something in the config
          if (select == 0 && getSortedDialogNames().indexOf(dialogName) == 0)
          {
            ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<CharSequence>(MBTabletViewManager.this,
                android.R.layout.simple_spinner_dropdown_item);
            arrayAdapter.add("Indices");
            arrayAdapter.add("AEX");
            arrayAdapter.add("AMX");
            arrayAdapter.add("Favorieten");

            TextView prompt = new TextView(MBTabletViewManager.this);
            prompt.setText(dialogDefinition.getTitle());

            MBSpinner spinner = new MBSpinner(MBTabletViewManager.this);
            spinner.setAdapter(arrayAdapter);
            spinner.setPromptView(prompt);

            tabBar.addTab(new MBTab(MBTabletViewManager.this).setView(spinner));
          }
          else
          {
            tabBar.addTab(new MBTab(MBTabletViewManager.this)
                .setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon())).setText(dialogDefinition.getTitle())
                .setListener(new MBTabListener(dialogName.hashCode())));
          }
        }
        tabBar.selectTab(select);
        actionBar.setCustomView(tabBar, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT, Gravity.LEFT));
      }
    });
  }

  @Override
  public void invalidateActionBar()
  {
    MBTabBar tabBar = (MBTabBar) getActionBar().getCustomView();
    int index = tabBar.indexOfSelectedTab();
    
    // throw away current MBActionBar and create a new one
    getActionBar().setCustomView(null);

    populateActionBar(index);
  }
}
