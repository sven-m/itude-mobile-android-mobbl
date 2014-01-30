/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl.core.view.components.tabbar;

import java.util.EnumSet;
import java.util.List;

import android.R;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
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

import com.itude.mobile.android.util.ComparisonUtil;
import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.android.util.ScreenUtil;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDialogGroupDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDomainDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBToolDefinition;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.controller.MBOutcome;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.controller.MBViewManager.MBActionBarInvalidationOption;
import com.itude.mobile.mobbl.core.services.MBLocalizationService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.MBResourceService;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.ScreenConstants;
import com.itude.mobile.mobbl.core.util.threads.MBThread;
import com.itude.mobile.mobbl.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl.core.view.components.MBHeader;

public abstract class MBDefaultActionBarBuilder implements MBActionBarBuilder
{
  private final Context    _context;
  private MBToolDefinition _refreshToolDef = null;
  private ActionBar        _actionBar;
  private Menu             _menu;

  public MBDefaultActionBarBuilder(Context context)
  {
    _context = context;
  }

  @Override
  public void fillActionBar(ActionBar actionBar, Menu menu)
  {
    _actionBar = actionBar;
    _menu = menu;

    List<MBToolDefinition> tools = MBMetadataService.getInstance().getTools();

    for (MBToolDefinition def : tools)
    {
      if (def.isPreConditionValid())
      {
        String localizedTitle = MBLocalizationService.getInstance().getTextForKey(def.getTitle());
        MenuItem menuItem = menu.add(Menu.NONE, def.getName().hashCode(), tools.indexOf(def), localizedTitle);

        Drawable image = null;
        if (def.getIcon() != null)
        {
          image = MBResourceService.getInstance().getImageByID(def.getIcon());
          menuItem.setIcon(image);
        }

        MenuItemCompat.setShowAsAction(menuItem, getMenuItemActionFlags(def));

        if ("REFRESH".equals(def.getType()))
        {
          _refreshToolDef = def;
        }
        else if ("SEARCH".equals(def.getType()))
        {
          final SearchView searchView = new SearchView(_context);
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

          changeSearchImage(image, searchView);

          SearchManager searchManager = (SearchManager) _context.getSystemService(Context.SEARCH_SERVICE);

          searchView.setSearchableInfo(searchManager.getSearchableInfo(MBViewManager.getInstance().getComponentName()));

          MenuItemCompat.setActionView(menuItem, searchView);
        }
      }
    }
    if (actionBar != null)
    {
      populateActionBar(actionBar);
    }
  }

  protected void handleOutcome(MBToolDefinition def)
  {
    MBOutcome outcome = new MBOutcome();
    outcome.setOrigin(new MBOutcome.Origin().withAction(def.getName()));
    outcome.setOutcomeName(def.getOutcomeName());

    MBApplicationController.getInstance().handleOutcome(outcome);
  }

  protected void setSearchImage(Drawable image, LinearLayout linearLayout)
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

  protected final int getMenuItemActionFlags(MBToolDefinition def)
  {
    String visibility = def.getVisibility();
    if (StringUtil.isBlank(visibility))
    {
      Log.w(Constants.APPLICATION_NAME, "No visibility specified for tool " + def.getName() + ": using default show as action if room");
      return MenuItemCompat.SHOW_AS_ACTION_IF_ROOM;
    }

    int flags = 0;
    String[] split = visibility.split("\\|");
    for (String flagString : split)
    {
      int flag = getFlagForString(flagString);
      flags = flags | flag;
    }

    return flags;
  }

  private int getFlagForString(String flag)
  {
    int resultFlag = -1;
    if ("ALWAYS".equals(flag))
    {
      resultFlag = MenuItemCompat.SHOW_AS_ACTION_ALWAYS;
    }
    else if ("IFROOM".equals(flag))
    {
      resultFlag = MenuItemCompat.SHOW_AS_ACTION_IF_ROOM;
    }
    else if ("OVERFLOW".equals(flag))
    {
      resultFlag = MenuItemCompat.SHOW_AS_ACTION_NEVER;
    }
    else if ("SHOWTEXT".equals(flag))
    {
      resultFlag = MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT;
    }
    else
    {
      throw new IllegalArgumentException("Invalid flag: " + flag);
    }

    return resultFlag;
  }

  private void populateActionBar(final ActionBar actionBar)
  {
    MBViewManager.getInstance().runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        MBTabBar tabBar = new MBTabBar(_context);

        for (String dialogName : MBViewManager.getInstance().getDialogManager().getSortedDialogNames())
        {
          MBDialogGroupDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);

          if (dialogDefinition.isPreConditionValid() && dialogDefinition.isShowAsTab())
          {
            if (dialogDefinition.getDomain() != null)
            {
              MBDomainDefinition domainDef = MBMetadataService.getInstance().getDefinitionForDomainName(dialogDefinition.getDomain());

              final MBTabSpinnerAdapter tabSpinnerAdapter = new MBTabSpinnerAdapter(_context, android.R.layout.simple_spinner_dropdown_item);

              for (MBDomainValidatorDefinition domDef : domainDef.getDomainValidators())
              {
                tabSpinnerAdapter.add(domDef.getTitle());
              }

              Drawable drawable = MBResourceService.getInstance().getImageByID("tab-spinner-leaf");

              MBTab tab = new MBTab(_context);
              tab.setAdapter(tabSpinnerAdapter);
              tab.setSelectedBackground(drawable);
              if (dialogDefinition.getIcon() != null)
              {
                tab.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));
              }
              setTabText(dialogDefinition, tab, tabBar);

              tab.setName(dialogName);
              tab.setListener(new MBTabListener(dialogName));

              tabBar.addTab(tab);

              if (ComparisonUtil.safeEquals(dialogName, MBViewManager.getInstance().getActiveDialogName())) tabBar.selectTab(tab, true);
            }
            else
            {
              MBTab tab = new MBTab(_context);
              setTabText(dialogDefinition, tab, tabBar);

              tab.setListener(new MBTabListener(dialogName));
              tab.setName(dialogName);

              if (dialogDefinition.getIcon() != null)
              {
                tab.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));
              }
              tabBar.addTab(tab);

              if (ComparisonUtil.safeEquals(dialogName, MBViewManager.getInstance().getActiveDialogName())) tabBar.selectTab(tab, true);
            }
          }
        }

        MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

        //fix the Home icon padding
        View homeIcon = null;
        if (DeviceUtil.getInstance().hasNativeActionBarSupport())
        {
          homeIcon = MBViewManager.getInstance().findViewById(R.id.home);
        }
        else
        {
          homeIcon = MBViewManager.getInstance().findViewById(android.support.v7.appcompat.R.id.home);
        }

        if (homeIcon != null)
        {
          styleHandler.styleHomeIcon(homeIcon);
          actionBar.setHomeButtonEnabled(true);
        }

        styleHandler.styleActionBar(actionBar);

        int actionBarDisplayOptions = ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO;
        actionBar.setDisplayOptions(actionBarDisplayOptions);

        final View customView;
        if (!tabBar.isEmpty())
        {
          customView = tabBar;
        }
        else
        {
          //          LinearLayout linearLayout = new LinearLayout(MBNextGenViewManager.this);
          //          linearLayout.setGravity(Gravity.CENTER_VERTICAL);
          //
          //          TextView textView = new TextView(MBNextGenViewManager.this);
          //          textView.setText(getTitle());
          //          linearLayout.addView(textView);

          MBHeader header = new MBHeader(_context);
          header.setTitleText((String) MBViewManager.getInstance().getTitle());

          styleHandler.styleActionBarHeader(header);
          styleHandler.styleActionBarHeaderTitle(header.getTitleView());

          customView = header;
        }

        actionBar.setCustomView(customView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT, Gravity.LEFT));
      }

      private void setTabText(MBDialogGroupDefinition dialogDefinition, MBTab tab, MBTabBar tabBar)
      {
        String title;

        if (_context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
          title = dialogDefinition.getTitlePortrait();
        }
        else
        {
          title = dialogDefinition.getTitle();
        }

        if (StringUtil.isNotBlank(title))
        {
          tab.setText(title);
          tabBar.setTabPadding(0, 0, ScreenConstants.SIXTEEN, 0);
        }
        else
        {
          tabBar.setTabPadding(0, 0, 0, 0);
        }
      }
    });
  }

  @Override
  public synchronized void showProgressIndicatorInTool()
  {
    MBToolDefinition refreshToolDef = getRefreshToolDef();
    Menu menu = _menu;

    if (refreshToolDef != null && menu != null)
    {
      final MenuItem item = menu.findItem(refreshToolDef.getName().hashCode());

      ImageView rotationImage = getRotationImage();

      float imageWidth = rotationImage.getDrawable().getIntrinsicWidth();
      int framePadding = (int) ((ScreenUtil.convertDimensionPixelsToPixels(_context, 80) - imageWidth) / 2);

      final FrameLayout frameLayout = new FrameLayout(_context);
      frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ScreenUtil.convertDimensionPixelsToPixels(_context, 80),
          LayoutParams.WRAP_CONTENT, Gravity.CENTER));
      frameLayout.setPadding(framePadding, 0, framePadding, 0);

      frameLayout.addView(rotationImage);

      MBViewManager.getInstance().runOnUiThread(new MBThread()
      {
        @Override
        public void runMethod()
        {
          //item.setIcon(null);
          MenuItemCompat.setActionView(item, frameLayout);
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
    ImageView rotationImage = new ImageView(_context);
    rotationImage.setImageDrawable(drawable);
    rotationImage.setAnimation(rotateAnimation);

    return rotationImage;
  }

  @Override
  public synchronized void hideProgressIndicatorInTool()
  {
    MBToolDefinition refreshToolDef = getRefreshToolDef();
    Menu menu = _menu;

    if (refreshToolDef != null && menu != null)
    {
      final MenuItem item = menu.findItem(refreshToolDef.getName().hashCode());

      MBViewManager.getInstance().runOnUiThread(new MBThread()
      {
        @Override
        public void runMethod()
        {
          MenuItemCompat.setActionView(item, null);
        }
      });
    }
  }

  @Override
  public void invalidateActionBar(EnumSet<MBActionBarInvalidationOption> flags)
  {
    if (_actionBar != null)
    {
      final boolean showFirst;
      final boolean notifyListener;
      final boolean resetHomeDialog;

      if (flags == null)
      {
        showFirst = false;
        notifyListener = false;
        resetHomeDialog = false;
      }
      else
      {
        showFirst = flags.contains(MBActionBarInvalidationOption.SHOW_FIRST);
        notifyListener = flags.contains(MBActionBarInvalidationOption.NOTIFY_LISTENER);
        resetHomeDialog = flags.contains(MBActionBarInvalidationOption.RESET_HOME_DIALOG);
      }

      MBViewManager.getInstance().runOnUiThread(new MBThread()
      {
        @Override
        public void runMethod()
        {
          MBTabBar tabBar = getTabBar();
          int selectedTab = -1;
          if (tabBar != null)
          {
            selectedTab = tabBar.indexOfSelectedTab();

            if (tabBar.getSelectedTab() != null)
            {
              tabBar.getSelectedTab().setSelected(false);
            }
          }
          MBViewManager.getInstance().invalidateOptionsMenu(resetHomeDialog, false);
          // throw away current MBActionBar and create a new one
          _actionBar.setCustomView(null);

          populateActionBar(_actionBar);

          tabBar = getTabBar();
          if (tabBar != null)
          {
            if (showFirst)
            {
              tabBar.selectTab(null, false);

              MBViewManager.getInstance().onHomeSelected();
            }
            else if (selectedTab >= 0)
            {
              tabBar.selectTab(tabBar.getTab(selectedTab), notifyListener);
            }
          }
        }
      });
    }
  }

  protected MBToolDefinition getRefreshToolDef()
  {
    return _refreshToolDef;
  }

  private MBTabBar getTabBar()
  {
    ActionBar actionBar = _actionBar;
    if (actionBar != null && actionBar.getCustomView() != null && actionBar.getCustomView() instanceof MBTabBar)
    {
      return (MBTabBar) actionBar.getCustomView();
    }
    return null;
  }

  @Override
  public void selectTabWithoutReselection(String dialogName)
  {
    MBTabBar tabBar = getTabBar();
    if (tabBar != null)
    {
      tabBar.selectTabWithoutReselection(dialogName);
    }
  }

  protected abstract void changeSearchImage(Drawable image, final SearchView searchView);

}
