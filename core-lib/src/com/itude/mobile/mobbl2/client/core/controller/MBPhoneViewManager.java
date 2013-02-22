package com.itude.mobile.mobbl2.client.core.controller;

import java.lang.reflect.Field;
import java.util.List;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBToolDefinition;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.components.tabbar.MBTab;
import com.itude.mobile.mobbl2.client.core.view.components.tabbar.MBTabBar;

@TargetApi(11)
public class MBPhoneViewManager extends MBNextGenViewManager
{
  // Android hooks
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    List<MBToolDefinition> tools = MBMetadataService.getInstance().getTools();

    for (MBToolDefinition def : tools)
    {
      if (isPreConditionValid(def))
      {
        String localizedTitle = MBLocalizationService.getInstance().getTextForKey(def.getTitle());
        MenuItem menuItem = menu.add(Menu.NONE, def.getName().hashCode(), tools.indexOf(def), localizedTitle);

        Drawable image = null;
        if (def.getIcon() != null)
        {
          image = MBResourceService.getInstance().getImageByID(def.getIcon());
          menuItem.setIcon(image);
        }

        menuItem.setShowAsAction(getMenuItemActionFlags(def));

        if ("SEARCH".equals(def.getType()))
        {
          final SearchView searchView = new SearchView(MBViewManager.getInstance().getApplicationContext());
          searchView.setTag(def);
          searchView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener()
          {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
              if (hasFocus)
              {
                Object tag = v.getTag();
                if (tag instanceof MBToolDefinition)
                {
                  MBToolDefinition toolDef = (MBToolDefinition) tag;
                  if (toolDef.getOutcomeName() != null)
                  {
                    handleOutcome(toolDef);
                  }
                }
              }
              else
              {
                searchView.setIconified(true);
              }
            }
          });

          if (image != null)
          {
            try
            {
              // change the iconified icon
              Field searchButtonField = searchView.getClass().getDeclaredField("mSearchButton");
              searchButtonField.setAccessible(true);
              ImageView searchButton = (ImageView) searchButtonField.get(searchView);
              searchButton.setImageDrawable(image);

              // change the searchview
              Field searchEditField = searchView.getClass().getDeclaredField("mSearchEditFrame");
              searchEditField.setAccessible(true);
              LinearLayout searchLayout = (LinearLayout) searchEditField.get(searchView);

              setSearchImage(image, searchLayout);

              LinearLayout searchPlate = (LinearLayout) searchLayout.getChildAt(1);
              MBViewBuilderFactory.getInstance().getStyleHandler().styleSearchPlate(searchPlate);

              LinearLayout submitArea = (LinearLayout) searchLayout.getChildAt(2);
              MBViewBuilderFactory.getInstance().getStyleHandler().styleSearchSubmitArea(submitArea);
            }
            catch (Exception e)
            {
              Log.e(Constants.APPLICATION_NAME, "error changing searchbutton icon", e);
            }
          }

          SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
          searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

          menuItem.setActionView(searchView);
        }
      }
    }
    return true;
  }

  private void setSearchImage(Drawable image, LinearLayout linearLayout)
  {
    ImageView searchViewIcon = null;

    for (int i = 0; i < linearLayout.getChildCount(); i++)
    {
      View view = linearLayout.getChildAt(i);
      if (view instanceof ImageView)
      {
        searchViewIcon = (ImageView) view;
        break;
      }
    }
    searchViewIcon.setImageDrawable(image);
  }

  // End of Android hooks
  @Override
  protected void onHomeSelected()
  {
    if (hasMenuItems())
    {
      getSlidingMenu().toggle(true);
    }
    else
    {
      MBDialogDefinition homeDialogDefinition = MBMetadataService.getInstance().getHomeDialogDefinition();

      MBTabBar tabBar = getTabBar();
      resetViewPreservingCurrentDialog();
      int firstDialog = homeDialogDefinition.getName().hashCode();
      MBTab selectedTab = tabBar.getSelectedTab();
      if (selectedTab == null || firstDialog != selectedTab.getTabId())
      {
        if (tabBar.findTabById(firstDialog) != null)
        {
          tabBar.selectTab(firstDialog, true);
        }
        else
        {
          activateDialogWithName(homeDialogDefinition.getName());
        }
      }
      else
      {
        activateDialogWithName(homeDialogDefinition.getName());
      }
    }
  }

  @Override
  public void invalidateActionBar(final boolean showFirst, final boolean notifyListener)
  {
    runOnUiThread(new MBThread()
    {
      @Override
      public void runMethod()
      {
        MBTabBar tabBar = getTabBar();
        int selectedTab = -1;
        if (tabBar != null)
        {
          selectedTab = tabBar.indexOfSelectedTab();
        }

        invalidateOptionsMenu();
        // throw away current MBActionBar and create a new one
        getActionBar().setCustomView(null);

        populateActionBar();

        tabBar = getTabBar();
        if (tabBar != null)
        {
          if (showFirst)
          {
            MBTab tab = tabBar.getTab(0);
            tabBar.selectTab(tab, notifyListener);
          }
          else if (selectedTab >= 0)
          {
            tabBar.selectTab(tabBar.getTab(selectedTab), notifyListener);
          }
        }
      }
    });
  }

  @Override
  public void setContentView(int id)
  {
    setContentView(getLayoutInflater().inflate(id, null));
  }

  @Override
  public void setContentView(View v)
  {
    setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
  }
}
