package com.itude.mobile.mobbl2.client.core.controller;

import java.lang.reflect.Field;
import java.util.List;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.itude.mobile.android.util.ScreenUtil;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBToolDefinition;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.components.tabbar.MBTab;
import com.itude.mobile.mobbl2.client.core.view.components.tabbar.MBTabBar;

/**
 * @author Coen Houtman
 *
 *  This ViewManager can be used to perform actions that cannot be done on pre-Honeycomb devices.
 *  For example the use of the ActionBar.
 */
@TargetApi(11)
public class MBTabletViewManager extends MBNextGenViewManager
{
  private Menu             _menu           = null;
  private MBToolDefinition _refreshToolDef = null;

  @Override
  protected void onPreCreate()
  {
    // empty to override request window feature to hide Android's standard indeterminate progress indicator
  }

  // Android hooks
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    _menu = menu;

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

        if ("REFRESH".equals(def.getType()))
        {
          _refreshToolDef = def;
        }
        else if ("SEARCH".equals(def.getType()))
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

              LinearLayout searchPlate = (LinearLayout) searchLayout.getChildAt(0);
              MBViewBuilderFactory.getInstance().getStyleHandler().styleSearchPlate(searchPlate);

              // find first image view, assuming this is the icon we need
              setSearchImage(image, searchPlate);

              LinearLayout submitArea = (LinearLayout) searchLayout.getChildAt(1);
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
      MBTabBar tabBar = getTabBar();
      if (tabBar != null)
      {
        resetViewPreservingCurrentDialog();
        int firstDialog = MBMetadataService.getInstance().getHomeDialogDefinition().getName().hashCode();
        MBTab selectedTab = tabBar.getSelectedTab();
        if (selectedTab == null || firstDialog != selectedTab.getTabId())
        {
          tabBar.selectTab(firstDialog, true);
        }
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

          if (tabBar.getSelectedTab() != null) tabBar.getSelectedTab().setSelected(false);
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
  public synchronized void showProgressIndicatorInTool()
  {
    if (_refreshToolDef != null && _menu != null)
    {
      final MenuItem item = _menu.findItem(_refreshToolDef.getName().hashCode());

      ImageView rotationImage = getRotationImage();

      float imageWidth = rotationImage.getDrawable().getIntrinsicWidth();
      int framePadding = (int) ((ScreenUtil.convertDimensionPixelsToPixels(getBaseContext(), 80) - imageWidth) / 2);

      final FrameLayout frameLayout = new FrameLayout(this);
      frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ScreenUtil.convertDimensionPixelsToPixels(getBaseContext(), 80),
          LayoutParams.WRAP_CONTENT, Gravity.CENTER));
      frameLayout.setPadding(framePadding, 0, framePadding, 0);

      frameLayout.addView(rotationImage);

      runOnUiThread(new MBThread()
      {
        @Override
        public void runMethod()
        {
          //item.setIcon(null);
          item.setActionView(frameLayout);
          getRotationImage().getAnimation().startNow();
        }
      });
    }
  }

  private ImageView getRotationImage()
  {
    RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    rotateAnimation.setDuration(1000L);
    rotateAnimation.setRepeatMode(Animation.INFINITE);
    rotateAnimation.setRepeatCount(Animation.INFINITE);
    rotateAnimation.setFillEnabled(false);
    rotateAnimation.setInterpolator(new LinearInterpolator());

    Drawable drawable = MBResourceService.getInstance().getImageByID(_refreshToolDef.getIcon());
    ImageView rotationImage = new ImageView(this);
    rotationImage.setImageDrawable(drawable);
    rotationImage.setAnimation(rotateAnimation);

    return rotationImage;
  }

  @Override
  public synchronized void hideProgressIndicatorInTool()
  {
    if (_refreshToolDef != null && _menu != null)
    {
      final MenuItem item = _menu.findItem(_refreshToolDef.getName().hashCode());

      runOnUiThread(new MBThread()
      {
        @Override
        public void runMethod()
        {
          item.setActionView(null);
        }
      });
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    super.onConfigurationChanged(newConfig);

    // Android 3.2 (perhaps other versions, but not 3.0) destroys the styling of the home icon in the action bar
    // when an onConfigurationChanged is triggered. When the main thread is about here, the handling of
    // that onConfigurationChanged should be in the message queue. This places a new message at the end of
    // the queue that changes the home icon back, which is therefore handled after the onConfigurationChanged message
    // that ruins it.
    final View homeIcon = findViewById(android.R.id.home);
    if (homeIcon != null) new Handler().post(new Runnable()
    {

      @Override
      public void run()
      {
        MBViewBuilderFactory.getInstance().getStyleHandler().styleHomeIcon(homeIcon);
      }
    });

  }
}
