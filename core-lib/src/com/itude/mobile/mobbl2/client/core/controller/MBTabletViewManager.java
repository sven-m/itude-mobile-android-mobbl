package com.itude.mobile.mobbl2.client.core.controller;

import java.util.List;

import android.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
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
        MenuItem menuItem = menu.add(Menu.NONE, def.getName().hashCode(), Menu.NONE, def.getTitle());
        menuItem.setIcon(MBResourceService.getInstance().getImageByID(def.getIcon()));
        // TODO do show as action in config
        int showAsActionFlag = MenuItem.SHOW_AS_ACTION_IF_ROOM;
        if ("REFRESH".equals(def.getType()))
        {
          _refreshId = def.getName().hashCode();
          showAsActionFlag = MenuItem.SHOW_AS_ACTION_ALWAYS;
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
          MBOutcome outcome = new MBOutcome();
          outcome.setOriginName(def.getName());
          outcome.setOutcomeName(def.getAction());

          MBApplicationController.getInstance().handleOutcome(outcome);
          return true;
        }
        return false;
      }
    }

    return super.onOptionsItemSelected(item);
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

  @Override
  final public void populateActionBar()
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
        tabBar.setTabPadding(0, 0, MBScreenUtilities.SIXTEEN, 0);

        for (String dialogName : getSortedDialogNames())
        {
          MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);

          //FIXME create something in the config
          if (getSortedDialogNames().indexOf(dialogName) == 0)
          {
            ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<CharSequence>(MBTabletViewManager.this,
                android.R.layout.simple_spinner_dropdown_item);
            arrayAdapter.add("Indices");
            arrayAdapter.add("AEX");
            arrayAdapter.add("AMX");
            arrayAdapter.add("Favorieten");

            MBSpinner spinner = new MBSpinner(MBTabletViewManager.this);
            spinner.setPadding(0, 0, MBScreenUtilities.SIXTEEN, 0);
            spinner.setAdapter(arrayAdapter);
            spinner.setText(dialogDefinition.getTitle());
            spinner.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));

            tabBar.addTab(new MBTab(MBTabletViewManager.this).setActiveView(spinner)
                .setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon())).setText(dialogDefinition.getTitle())
                .setTabId(dialogName.hashCode()).setListener(new MBTabListener(dialogName.hashCode())));
          }
          else
          {
            tabBar.addTab(new MBTab(MBTabletViewManager.this)
                .setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon())).setText(dialogDefinition.getTitle())
                .setListener(new MBTabListener(dialogName.hashCode())).setTabId(dialogName.hashCode()));
          }
        }
        actionBar.setCustomView(tabBar, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT, Gravity.LEFT));
      }
    });
  }

  @Override
  final public void invalidateActionBar()
  {
    runOnUiThread(new MBRunnable()
    {
      @Override
      public void runMethod()
      {
        invalidateOptionsMenu();
        // throw away current MBActionBar and create a new one
        getActionBar().setCustomView(null);

        populateActionBar();
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
