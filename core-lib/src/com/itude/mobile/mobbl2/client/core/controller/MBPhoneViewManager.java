package com.itude.mobile.mobbl2.client.core.controller;

import java.lang.reflect.Field;
import java.util.List;

import android.R;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDomainValidatorDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBToolDefinition;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBExpressionNotBooleanException;
import com.itude.mobile.mobbl2.client.core.controller.util.MBTabListener;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;
import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.components.MBSpinnerAdapter;
import com.itude.mobile.mobbl2.client.core.view.components.MBTab;
import com.itude.mobile.mobbl2.client.core.view.components.MBTabBar;

public class MBPhoneViewManager extends MBViewManager
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

  private int getMenuItemActionFlags(MBToolDefinition def)
  {
    String visibility = def.getVisibility();
    if (StringUtilities.isBlank(visibility))
    {
      Log.w(Constants.APPLICATION_NAME, "No visibility specified for tool " + def.getName() + ": using default show as action if room");
      return MenuItem.SHOW_AS_ACTION_IF_ROOM;
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
      resultFlag = MenuItem.SHOW_AS_ACTION_ALWAYS;
    }
    else if ("IFROOM".equals(flag))
    {
      resultFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM;
    }
    else if ("OVERFLOW".equals(flag))
    {
      resultFlag = MenuItem.SHOW_AS_ACTION_NEVER;
    }
    else if ("SHOWTEXT".equals(flag))
    {
      resultFlag = MenuItem.SHOW_AS_ACTION_WITH_TEXT;
    }
    else
    {
      throw new IllegalArgumentException("Invalid flag: " + flag);
    }

    return resultFlag;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    if (item.getItemId() == android.R.id.home)
    {
      onHomeSelected();
      return true;
    }

    for (MBToolDefinition def : MBMetadataService.getInstance().getTools())
    {
      if (item.getItemId() == def.getName().hashCode())
      {
        if (def.getOutcomeName() != null)
        {
          handleOutcome(def);
          return true;
        }
        return false;
      }
    }

    return super.onOptionsItemSelected(item);
  }

  private void handleOutcome(MBToolDefinition def)
  {
    MBOutcome outcome = new MBOutcome();
    outcome.setOriginName(def.getName());
    outcome.setOutcomeName(def.getOutcomeName());

    MBApplicationController.getInstance().handleOutcome(outcome);
  }

  // End of Android hooks

  private void onHomeSelected()
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

  @Override
  public void activateDialogWithName(String dialogName)
  {
    super.activateDialogWithName(dialogName);

    if (dialogName != null)
    {
      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      if (dialogDefinition.getParent() != null)
      {
        dialogName = dialogDefinition.getParent();
        dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      }

      if (!"TRUE".equals(dialogDefinition.getAddToNavbar()))
      {
        MBTabBar tabBar = getTabBar();
        if (tabBar != null)
        {
          tabBar.selectTab(null, true);
        }
      }

    }
  }

  @Override
  public void selectTab(int id)
  {
    MBTabBar tabBar = getTabBar();
    if (tabBar != null)
    {
      tabBar.selectTab(id, true);
    }
  }

  @Override
  public MBTabBar getTabBar()
  {
    try
    {
      ActionBar actionBar = getActionBar();
      if (actionBar != null)
      {
        return (MBTabBar) actionBar.getCustomView();
      }
    }
    catch (Exception e)
    {
      Log.w(Constants.APPLICATION_NAME, "Unable to retrieve the tab bar, returning null", e);
    }
    return null;
  }

  private void populateActionBar()
  {
    runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        final ActionBar actionBar = getActionBar();

        MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

        //fix the Home icon padding
        View homeIcon = findViewById(R.id.home);
        if (homeIcon != null)
        {
          styleHandler.styleHomeIcon(homeIcon);
        }

        styleHandler.styleActionBar(actionBar);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);

        MBTabBar tabBar = new MBTabBar(MBPhoneViewManager.this);

        for (String dialogName : getSortedDialogNames())
        {
          MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);

          if (dialogDefinition.getDomain() != null)
          {
            MBDomainDefinition domainDef = MBMetadataService.getInstance().getDefinitionForDomainName(dialogDefinition.getDomain());

            final MBSpinnerAdapter spinnerAdapter = new MBSpinnerAdapter(MBPhoneViewManager.this, R.layout.simple_spinner_dropdown_item);

            for (MBDomainValidatorDefinition domDef : domainDef.getDomainValidators())
            {
              spinnerAdapter.add(domDef.getTitle());
            }

            Drawable drawable = MBResourceService.getInstance().getImageByID("tab-spinner-leaf");

            MBTab tab = new MBTab(MBPhoneViewManager.this);
            tab.setAdapter(spinnerAdapter);
            tab.setSelectedBackground(drawable);
            if (dialogDefinition.getIcon() != null)
            {
              tab.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));
            }
            setTabText(dialogDefinition, tab, tabBar);

            tab.setTabId(dialogName.hashCode());
            tab.setListener(new MBTabListener(dialogName.hashCode()));

            tabBar.addTab(tab);
          }
          else
          {
            MBTab tab = new MBTab(MBPhoneViewManager.this);
            setTabText(dialogDefinition, tab, tabBar);

            tab.setListener(new MBTabListener(dialogName.hashCode()));
            tab.setTabId(dialogName.hashCode());

            if (dialogDefinition.getIcon() != null)
            {
              tab.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));
            }
            tabBar.addTab(tab);
          }
        }
        actionBar.setCustomView(tabBar, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT, Gravity.LEFT));
      }

      private void setTabText(MBDialogDefinition dialogDefinition, MBTab tab, MBTabBar tabBar)
      {
        String title;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
          title = dialogDefinition.getTitlePortrait();
        }
        else
        {
          title = dialogDefinition.getTitle();
        }

        if (StringUtilities.isNotBlank(title))
        {
          tab.setText(title);
          tabBar.setTabPadding(0, 0, MBScreenUtilities.SIXTEEN, 0);
        }
        else
        {
          tabBar.setTabPadding(0, 0, 0, 0);
        }
      }
    });
  }

  private void refreshActionBar()
  {
    invalidateActionBar(false, false);
  }

  @Override
  public void invalidateActionBar(boolean selectFirstTab)
  {
    invalidateActionBar(selectFirstTab, true);
  }

  public void invalidateActionBar(final boolean selectFirstTab, final boolean notifyListener)
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
          if (selectFirstTab)
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

  private boolean isPreConditionValid(MBToolDefinition def)
  {
    if (def.getPreCondition() == null)
    {
      return true;
    }

    MBDocument doc = MBDataManagerService.getInstance().loadDocument(MBConfigurationDefinition.DOC_SYSTEM_EMPTY);

    String result = doc.evaluateExpression(def.getPreCondition());
    if ("1".equals(result) || "YES".equalsIgnoreCase(result) || "TRUE".equalsIgnoreCase(result))
    {
      return true;
    }
    if ("0".equals(result) || "NO".equalsIgnoreCase(result) || "FALSE".equalsIgnoreCase(result))
    {
      return false;
    }
    String msg = "Expression of tool with name=" + def.getName() + " precondition=" + def.getPreCondition() + " is not boolean (result="
                 + result + ")";
    throw new MBExpressionNotBooleanException(msg);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    refreshActionBar();

    super.onConfigurationChanged(newConfig);

  }
}
