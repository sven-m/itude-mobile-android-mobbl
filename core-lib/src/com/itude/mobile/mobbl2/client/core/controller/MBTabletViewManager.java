package com.itude.mobile.mobbl2.client.core.controller;

import java.lang.reflect.Field;
import java.util.List;

import android.R;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBRunnable;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.components.MBSpinner;
import com.itude.mobile.mobbl2.client.core.view.components.MBSpinnerAdapter;
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
  private Menu _menu      = null;
  private int  _refreshId = -1;

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
        int index = tools.indexOf(def);
        MenuItem menuItem = menu.add(Menu.NONE, def.getName().hashCode(), index, def.getTitle());
        Drawable image = MBResourceService.getInstance().getImageByID(def.getIcon());
        menuItem.setIcon(image);

        // TODO do show as action in config
        int showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM;
        if ("REFRESH".equals(def.getType()))
        {
          _refreshId = def.getName().hashCode();
          showAsActionFlag = MenuItem.SHOW_AS_ACTION_ALWAYS;
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
                  handleOutcome(toolDef);
                }
              }
              else
              {
                searchView.setIconified(true);
              }
            }
          });

          if (def.getIcon() != null)
          {
            try
            {
              // change the iconified icon
              Field searchButtonField = searchView.getClass().getDeclaredField("mSearchButton");
              searchButtonField.setAccessible(true);
              ImageView searchButton = (ImageView) searchButtonField.get(searchView);
              searchButton.setImageDrawable(image);

              // change the searchview icon
              Field searchEditField = searchView.getClass().getDeclaredField("mSearchEditFrame");
              searchEditField.setAccessible(true);
              LinearLayout searchLayout = (LinearLayout) searchEditField.get(searchView);
              LinearLayout linearLayout = (LinearLayout) searchLayout.getChildAt(0);

              // find first image view, assuming this is the icon we need
              ImageView searchViewIcon = null;
              for (int i = 0; searchViewIcon == null && i < linearLayout.getChildCount(); i++)
              {
                View view = linearLayout.getChildAt(i);
                if (view instanceof ImageView)
                {
                  searchViewIcon = (ImageView) view;
                }
              }
              searchViewIcon.setImageDrawable(image);
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
        menuItem.setShowAsAction(showAsActionFlag);
      }
    }

    return true;
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
        if (def.getAction() != null)
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
    outcome.setOutcomeName(def.getAction());

    MBApplicationController.getInstance().handleOutcome(outcome);
  }

  // End of Android hooks

  private void onHomeSelected()
  {
    MBTabBar tabBar = getTabBar();
    if (tabBar != null)
    {
      resetViewPreservingCurrentDialog();
      int firstDialog = MBMetadataService.getInstance().getFirstDialogDefinition().getName().hashCode();
      MBTab tab = tabBar.findTabById(firstDialog);
      tab.select();
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
          tabBar.selectTab(null);
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
      MBTab tab = tabBar.findTabById(id);
      tab.select();
    }
  }

  private MBTabBar getTabBar()
  {
    try
    {
      return (MBTabBar) getActionBar().getCustomView();
    }
    catch (Exception e)
    {
      Log.w(Constants.APPLICATION_NAME, "Unable to retrieve the tab bar, returning null", e);
      return null;
    }
  }

  private void populateActionBar()
  {
    runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        final ActionBar actionBar = getActionBar();

        //fix the Home icon padding
        View homeIcon = findViewById(R.id.home);
        if (homeIcon != null)
        {
          MBViewBuilderFactory.getInstance().getStyleHandler().styleHomeIcon(homeIcon);
        }

        MBViewBuilderFactory.getInstance().getStyleHandler().styleActionBar(actionBar);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);

        MBTabBar tabBar = new MBTabBar(MBTabletViewManager.this);
        tabBar.setTabPadding(0, 0, MBScreenUtilities.SIXTEEN, 0);

        for (String dialogName : getSortedDialogNames())
        {
          MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);

          if (dialogDefinition.getDomain() != null)
          {
            MBDomainDefinition domainDef = MBMetadataService.getInstance().getDefinitionForDomainName(dialogDefinition.getDomain());

            final MBSpinnerAdapter spinnerAdapter = new MBSpinnerAdapter(MBTabletViewManager.this);

            for (MBDomainValidatorDefinition domDef : domainDef.getDomainValidators())
            {
              spinnerAdapter.add(domDef.getTitle());
            }

            final MBSpinner spinner = new MBSpinner(MBTabletViewManager.this);
            spinner.setId(dialogDefinition.getName().hashCode());
            spinner.setAdapter(spinnerAdapter);
            spinner.setPadding(0, 0, MBScreenUtilities.SIXTEEN, 0);
            spinner.setText(dialogDefinition.getTitle());
            spinner.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));
            spinner.setOnItemSelectedListener(new OnItemSelectedListener()
            {

              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
              {
                if (position == spinnerAdapter.getSelectedElement())
                {
                  return;
                }

                spinnerAdapter.setSelectedElement(position);
                MBDialogDefinition dialogDef = null;

                List<MBDialogDefinition> dialogs = MBMetadataService.getInstance().getDialogs();
                for (int i = 0; i < dialogs.size() && dialogDef == null; i++)
                {
                  MBDialogDefinition dialog = dialogs.get(i);
                  if (dialog.getName().hashCode() == parent.getId())
                  {
                    dialogDef = dialog;
                  }
                }

                if (dialogDef != null)
                {
                  MBDomainDefinition domainDef = MBMetadataService.getInstance().getDefinitionForDomainName(dialogDef.getDomain());
                  String value = domainDef.getDomainValidators().get(position).getValue();

                  if (value != null)
                  {
                    MBOutcome outcome = new MBOutcome(value, null);
                    outcome.setOriginName(dialogDef.getName());
                    MBApplicationController.getInstance().handleOutcome(outcome);
                  }

                }
              }

              @Override
              public void onNothingSelected(AdapterView<?> parent)
              {
              }

            });

            tabBar.addTab(new MBTab(MBTabletViewManager.this).setActiveView(spinner)
                .setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon())).setText(dialogDefinition.getTitle())
                .setTabId(dialogName.hashCode()).setListener(new MBTabListener(dialogName.hashCode())));
          }
          else
          {
            MBTab tab = new MBTab(MBTabletViewManager.this).setText(dialogDefinition.getTitle())
                .setListener(new MBTabListener(dialogName.hashCode())).setTabId(dialogName.hashCode());

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
    });
  }

  @Override
  public final void invalidateActionBar(final boolean selectFirstTab)
  {
    runOnUiThread(new MBRunnable()
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
            tabBar.selectTab(tab);
          }
          else if (selectedTab >= 0)
          {
            tabBar.selectTab(tabBar.getTab(selectedTab));
          }
        }
      }
    });
  }

  @Override
  public void showProgressIndicatorInTool()
  {
    if (_refreshId != -1 && _menu != null)
    {
      final MenuItem item = _menu.findItem(_refreshId);

      //FIXME move view building to somewhere in the view package
      final FrameLayout frameLayout = new FrameLayout(this);
      frameLayout.setLayoutParams(new FrameLayout.LayoutParams(MBScreenUtilities.convertDimensionPixelsToPixels(80),
          LayoutParams.WRAP_CONTENT, Gravity.CENTER));
      frameLayout.setPadding(0, 0, MBScreenUtilities.convertDimensionPixelsToPixels(20), 0);

      ProgressBar progressBar = new ProgressBar(this);
      int dp = MBScreenUtilities.convertDimensionPixelsToPixels(40);
      progressBar.setLayoutParams(new FrameLayout.LayoutParams(dp, dp, Gravity.CENTER));
      frameLayout.addView(progressBar);
      runOnUiThread(new MBRunnable()
      {
        @Override
        public void runMethod()
        {
          item.setActionView(frameLayout);
        }
      });
    }
  }

  @Override
  public void hideProgressIndicatorInTool()
  {
    if (_refreshId != -1 && _menu != null)
    {
      final MenuItem item = _menu.findItem(_refreshId);

      runOnUiThread(new MBRunnable()
      {
        @Override
        public void runMethod()
        {
          item.setActionView(null);
        }
      });
    }
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
}
