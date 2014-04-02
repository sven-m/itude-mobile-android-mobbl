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
package com.itude.mobile.mobbl.core.controller;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBToolDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.exceptions.MBInvalidPathException;
import com.itude.mobile.mobbl.core.controller.MBApplicationController.ApplicationState;
import com.itude.mobile.mobbl.core.controller.MBDialogManager.MBDialogChangeListener;
import com.itude.mobile.mobbl.core.controller.exceptions.MBExpressionNotBooleanException;
import com.itude.mobile.mobbl.core.controller.helpers.MBActivityHelper;
import com.itude.mobile.mobbl.core.controller.util.DefaultHomeButtonHandler;
import com.itude.mobile.mobbl.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBElement;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBLocalizationService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.MBParseUtil;
import com.itude.mobile.mobbl.core.util.helper.MBSecurityHelper;
import com.itude.mobile.mobbl.core.util.threads.MBThreadHandler;
import com.itude.mobile.mobbl.core.view.MBAlert;
import com.itude.mobile.mobbl.core.view.MBPage;
import com.itude.mobile.mobbl.core.view.MBPage.OrientationPermission;
import com.itude.mobile.mobbl.core.view.builders.MBContentViewWrapper;
import com.itude.mobile.mobbl.core.view.components.tabbar.MBActionBarBuilder;

/**
 * Default view manager
 *
 */
public abstract class MBViewManager extends ActionBarActivity implements MBDialogChangeListener
{
  public enum MBActionBarInvalidationOption {
    SHOW_FIRST, RESET_HOME_DIALOG, NOTIFY_LISTENER
  }

  protected static MBViewManager _instance;

  private Dialog                 _currentAlert;
  private boolean                _singlePageMode;
  private boolean                _showDialogTitle = false;

  private boolean                _activityExists  = false;

  private int                    _defaultScreenOrientation;

  private MBDialogManager        _dialogManager;
  private MBShutdownHandler      _shutdownHandler = new MBDefaultShutdownHandler();

  private HomeButtonHandler      _homeButtonHandler;
  private ViewGroup              _container;

  ///////////////////// Android lifecycle methods

  @Override
  protected void onCreate(android.os.Bundle savedInstanceState)
  {
    onPreCreate();

    _actionBarBuilder = constructActionBarBuilder();

    _dialogManager = new MBDialogManager(this);

    _homeButtonHandler = new DefaultHomeButtonHandler();

    /*
     *  We store our default orientation. This will be used to determine how pages should be shown by default
     *  See setOrientation
     */
    _defaultScreenOrientation = getRequestedOrientation();

    super.onCreate(null);

    supportRequestWindowFeature(Window.FEATURE_PROGRESS);
    supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    // makes sure the action bar is initialized (otherwise, the setProgressBar.. doesn't work)
    if (getSupportActionBar() != null)
    {
      setSupportProgressBarIndeterminateVisibility(false);
    }

    getDialogManager().addDialogChangeListener(this);

    _instance = this;

    MBApplicationController.getInstance().startController();
  }

  public void prepareForApplicationStart()
  {
    _dialogManager.onCreate();
  }

  @Override
  public void setContentView(View view)
  {

    if (_container == null)
    {
      FrameLayout container = new FrameLayout(this);
      LayoutParams layout = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
      container.setLayoutParams(layout);

      MBContentViewWrapper wrapper = MBApplicationFactory.getInstance().createContentViewWrapper();
      super.setContentView(wrapper.buildContentView(this, container));

      _container = container;
    }

    _container.removeAllViews();
    _container.addView(view);
  }

  @Override
  protected void onResume()
  {
    if (!_activityExists)
    {
      _activityExists = true;
    }
    else
    {
      _dialogManager.onResume();
    }
    super.onResume();
  }

  @Override
  protected void onRestart()
  {
    super.onRestart();

    MBApplicationController.getInstance().startOutcomeHandler();
  }

  @Override
  protected void onStop()
  {
    MBThreadHandler.getInstance().stopAllRunningThreads();

    MBApplicationController.getInstance().stopOutcomeHandler();

    super.onStop();
  }

  @Override
  protected void onDestroy()
  {
    // Our application is closing so after this point our ApplicationState should return that the application is not started.
    MBApplicationController.getInstance().setApplicationState(ApplicationState.NOTSTARTED);

    _dialogManager.onDestroy();

    super.onDestroy();
  }

  @Override
  protected void onPause()
  {
    if (MBActivityHelper.isApplicationBroughtToBackground(this))
    {
      stopAlert();
      MBSecurityHelper.getInstance().logOutIfCheckNotSelected();
    }

    _dialogManager.onPause();

    super.onPause();
  }

  private void stopAlert()
  {
    Dialog currentAlert = getCurrentAlert();
    if (currentAlert != null) currentAlert.dismiss();
  }

  ///////////////////// 

  ///////////////////// Android method

  @Override
  protected void onNewIntent(Intent intent)
  {
    super.onNewIntent(intent);
    setIntent(intent);

    final MBApplicationController appController = MBApplicationController.getInstance();

    if (Intent.ACTION_SEARCH.equals(intent.getAction()) || Intent.ACTION_VIEW.equals(intent.getAction()))
    {
      appController.handleSearchRequest(intent);
    }

    final String outcomeName = intent.getStringExtra(Constants.C_INTENT_POST_INITIALOUTCOMES_OUTCOMENAME);
    if (StringUtil.isNotBlank(outcomeName))
    {

      runOnUiThread(new Runnable()
      {

        @Override
        public void run()
        {
          if (appController.getOutcomeHandler() == null)
          {
            appController.startOutcomeHandler();
          }
          appController.handleOutcome(new MBOutcome(outcomeName, null));
        }
      });
    }

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    if (getActiveDialog() != null) for (MBBasicViewController controller : getActiveDialog().getAllFragments())
    {
      controller.onActivityResult(requestCode, resultCode, data);
    }
  }

  /***
   * Only sets a flag to invalidate the menu on first invocation. The menu is built in {@link #onPrepareOptionsMenu(Menu)}
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    return buildOptionsMenu(menu);
  }

  public void finishFromChild(MBDialogController childController)
  {
    MBDialogDefinition firstDialogDefinition = MBMetadataService.getInstance().getHomeDialogDefinition();
    final String firstDialog = firstDialogDefinition.getName();
    if (!childController.getName().equals(firstDialog))
    {
      getDialogManager().activateHome();
    }
    else
    {
      getShutdownHandler().onShutdown();
    }

  }

  ///// Event handling /////

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
    {
      // Take care of calling this method on earlier versions of
      // the platform where it doesn't exist.
      onBackPressed();

      return true;
    }
    else
    {
      // Pass this onKeyDown event trough to the underlying fragments
      MBDialogController activeDialog = getActiveDialog();
      if (activeDialog != null && !getActiveDialog().onKeyDown(keyCode, event))
      {
        return super.onKeyDown(keyCode, event);
      }
      else
      {
        return true;
      }
    }

  }

  @Override
  public void onBackPressed()
  {
    if (!getActiveDialog().onBackPressed())
    {
      super.onBackPressed();
    }
  }

  public boolean onMenuKeyDown(int keyCode, KeyEvent event, View callingView)
  {
    boolean onKeyDown = super.onKeyDown(keyCode, event);

    if (!onKeyDown)
    {
      hideSoftKeyBoard(callingView);
      this.openOptionsMenu();
      return true;
    }

    return false;
  }

  @Override
  public boolean onSearchRequested()
  {
    if (getActiveDialog() != null) return getActiveDialog().onSearchRequested();
    else return false;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev)
  {
    boolean handled = false;
    if (getActiveDialog() != null) handled = getActiveDialog().dispatchTouchEvent(ev);
    if (!handled) return super.dispatchTouchEvent(ev);
    return handled;
  }

  /////////////////////////////////////////////////////

  public void showAlert(MBAlert alert, boolean backStackEnabled)
  {
    alert.buildAlertDialog().show();
  }

  @SuppressLint("NewApi")
  public void invalidateOptionsMenu(boolean resetHomeDialog, final boolean selectHome)
  {
    super.supportInvalidateOptionsMenu();

    if (selectHome) getDialogManager().activateHome();

  }

  public Dialog getCurrentAlert()
  {
    return _currentAlert;
  }

  public void setCurrentAlert(Dialog currentAlert)
  {
    _currentAlert = currentAlert;
  }

  public boolean getSinglePageMode()
  {
    return _singlePageMode;
  }

  public void setSinglePageMode(boolean singlePageMode)
  {
    _singlePageMode = singlePageMode;
  }

  public void showPage(MBPage page, String displayMode, boolean addToBackStack)
  {

    Log.d(Constants.APPLICATION_NAME,
          "MBViewManager: showPage name=" + page.getPageName() + " pagestackName=" + page.getPageStackName() + " mode=" + displayMode
              + " type=" + page.getPageType() + " orientation=" + ((MBPageDefinition) page.getDefinition()).getOrientationPermissions()
              + " backStack=" + addToBackStack);

    if (page.getPageType() == MBPageDefinition.MBPageType.MBPageTypesErrorPage || "POPUP".equals(displayMode))
    {
      showAlertView(page);
    }
    else
    {
      addPageToPageStack(page, displayMode, addToBackStack);
    }
  }

  private void showAlertView(MBPage page)
  {

    if (getCurrentAlert() != null)
    {
      getCurrentAlert().dismiss();
    }

    String title = null;
    String message = null;

    MBDocument pageDoc = page.getDocument();
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    boolean buildDefault = true;
    if (pageDoc.getName().equals(MBConfigurationDefinition.DOC_SYSTEM_EXCEPTION))
    {
      title = pageDoc.getValueForPath(MBConfigurationDefinition.PATH_SYSTEM_EXCEPTION_NAME);
      message = pageDoc.getValueForPath(MBConfigurationDefinition.PATH_SYSTEM_EXCEPTION_DESCRIPTION);
    }
    else
    {
      title = page.getTitle();
      message = MBLocalizationService.getInstance().getTextForKey((String) pageDoc.getValueForPath("/message[0]/@text"));
      if (message == null)
      {
        message = MBLocalizationService.getInstance().getTextForKey((String) pageDoc.getValueForPath("/message[0]/@text()"));
      }

      try
      {
        MBElement buttons = pageDoc.getValueForPath("/buttons[0]");
        MBElement neutral = buttons.getValueForPath("/neutral[0]");
        MBElement negative = buttons.getValueForPath("/negative[0]");
        MBElement positive = buttons.getValueForPath("/positive[0]");

        builder.setMessage(message).setTitle(title).setCancelable(true);

        buildAlertDialogButtons(builder, neutral);
        buildAlertDialogButtons(builder, negative);
        buildAlertDialogButtons(builder, positive);

        buildDefault = false;
      }
      catch (MBInvalidPathException e)
      {
        Log.w(Constants.APPLICATION_NAME, "Popup document " + pageDoc.getName() + " has no buttons defined. Adding neutral button Ok");
      }
    }

    if (buildDefault)
    {
      builder.setMessage(message).setTitle(title).setCancelable(true).setNeutralButton("Ok", new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialog, int id)
        {
          dialog.cancel();
        }
      });
    }

    runOnUiThread(new Runnable()
    {

      @Override
      public void run()
      {
        Dialog dialog = builder.create();
        dialog.show();
        setCurrentAlert(dialog);
      }
    });

  }

  private void buildAlertDialogButtons(AlertDialog.Builder builder, final MBElement element)
  {
    String label = element.getValueForAttribute("label");
    if (StringUtil.isNotBlank(label))
    {
      if ("neutral".equals(element.getName()))
      {
        builder.setNeutralButton(MBLocalizationService.getInstance().getTextForKey(label), new OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            String outcome = element.getValueForAttribute("outcome");
            if (StringUtil.isBlank(outcome))
            {
              dialog.cancel();
            }
            else
            {
              MBApplicationController.getInstance().handleOutcome(new MBOutcome(outcome, null));
            }
          }
        });
      }
      else if ("positive".equals(element.getName()))
      {
        builder.setPositiveButton(MBLocalizationService.getInstance().getTextForKey(label), new OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            String outcome = element.getValueForAttribute("outcome");
            if (StringUtil.isBlank(outcome))
            {
              dialog.cancel();
            }
            else
            {
              MBApplicationController.getInstance().handleOutcome(new MBOutcome(outcome, null));
            }
          }
        });

      }
      else if ("negative".equals(element.getName()))
      {
        builder.setNegativeButton(MBLocalizationService.getInstance().getTextForKey(label), new OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            String outcome = element.getValueForAttribute("outcome");
            if (StringUtil.isBlank(outcome))
            {
              dialog.cancel();
            }
            else
            {
              MBApplicationController.getInstance().handleOutcome(new MBOutcome(outcome, null));
            }
          }
        });

      }

    }

  }

  private void addPageToPageStack(MBPage page, String displayMode, boolean addToBackStack)
  {
    //MBDialogDefinition topDefinition = MBMetadataService.getInstance().getTopDialogDefinitionForDialogName(page.getDialogName());
    MBPageStackController pageStack = _dialogManager.getPageStack(page.getPageStackName());
    MBDialogController dialogController = pageStack.getParent();
    dialogController.showPage(page, displayMode, page.getPageStackName() + page.getPageName(), page.getPageStackName(), addToBackStack);
  }

  @Override
  public void supportInvalidateOptionsMenu()
  {
    runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        ActivityCompat.invalidateOptionsMenu(MBViewManager.this);
      }
    });
  }

  public boolean activateDialogWithName(String dialogName)
  {
    MBOutcome outcome = new MBOutcome(dialogName, null);
    outcome.setOrigin(new MBOutcome.Origin().withDialog(dialogName));
    MBApplicationController.getInstance().handleOutcome(outcome);
    return false;
  }

  public void endDialog(String dialogName, boolean keepPosition)
  {
    if (keepPosition) _dialogManager.getDialog(dialogName).popAll();
    else _dialogManager.getDialog(dialogName).dismiss();
  }

  public void popPage(String dialogName)
  {
    _dialogManager.getDialog(dialogName).popView();
  }

  public void makeKeyAndVisible()
  {
  }

  public String getActiveDialogName()
  {
    return _dialogManager.getActiveDialog() != null ? _dialogManager.getActiveDialog().getName() : null;
  }

  public void resetView()
  {
  }

  public void resetViewPreservingCurrentDialog()
  {
    // Walk trough all dialogControllers
    for (MBDialogController dc : _dialogManager.getDialogs())
    {
      dc.clearAllViews();
    }

  }

  public static MBViewManager getInstance()
  {
    return _instance;
  }

  @Deprecated
  public List<String> getSortedDialogNames()
  {
    return _dialogManager.getSortedDialogNames();
  }

  public void removeDialog(String dialogName)
  {
    _dialogManager.removeDialog(dialogName);

  }

  public void hideSoftKeyBoard(View triggeringView)
  {
    InputMethodManager imm = (InputMethodManager) triggeringView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(triggeringView.getWindowToken(), 0);
  }

  public void showSoftKeyBoard(View triggeringView)
  {
    InputMethodManager imm = (InputMethodManager) triggeringView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
  }

  /**
   * Method can be used to manually request an orientation
   * @param orientation use {@link ActivityInfo} to set your requested orientation.
   */
  public void setOrientation(int orientation)
  {
    Log.d(Constants.APPLICATION_NAME, "MBViewManager.setOrientation: Changing to " + orientation);
    setRequestedOrientation(orientation);
  }

  public void setOrientation(MBPage page)
  {
    if (page == null)
    {
      Log.w(Constants.APPLICATION_NAME, "Can't set orientation without an MBPage");
      return;
    }

    MBPage.OrientationPermission orientationPermissions = page.getOrientationPermissions();

    /*
     *  If no orientation permissions have been set on a Page level we want to use the permission that is defined in the the AndroidManifest.xml (if any)
     */
    if (orientationPermissions == MBPage.OrientationPermission.UNDEFINED)
    {
      if (_defaultScreenOrientation != getRequestedOrientation())
      {
        setRequestedOrientation(_defaultScreenOrientation);
      }

    }
    else if (orientationPermissions == MBPage.OrientationPermission.ANY)
    {
      if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR)
      {
        Log.d(Constants.APPLICATION_NAME, "MBViewManager.setOrientation: Changing to SENSOR");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
      }
    }
    else if (orientationPermissions == OrientationPermission.PORTRAIT)
    {
      Log.d(Constants.APPLICATION_NAME, "MBViewManager.setOrientation: Changing to PORTRAIT");
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    else if (orientationPermissions == OrientationPermission.LANDSCAPE)
    {
      Log.d(Constants.APPLICATION_NAME, "MBViewManager.setOrientation: Changing to LANDSCAPE");
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

  }

  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    Log.d(Constants.APPLICATION_NAME, "MBViewManager.onConfigurationChanged");

    invalidateActionBar();

    super.onConfigurationChanged(newConfig);

    _dialogManager.onConfigurationChanged(newConfig);
  }

  public List<MBBasicViewController> getAllFragments()
  {
    List<MBBasicViewController> list = new ArrayList<MBBasicViewController>();
    // Walk trough all dialogControllers
    for (MBDialogController dc : _dialogManager.getDialogs())
    {

      //TODO Duplicaten er nog eens uit halen.
      if (dc != null && !dc.getAllFragments().isEmpty()) list.addAll(dc.getAllFragments());
    }

    return list;
  }

  @Override
  public void setTitle(CharSequence title)
  {
    CharSequence titleToSet = null;
    if (isShowDialogTitle())
    {
      titleToSet = title;
    }
    super.setTitle(titleToSet);
  }

  ////// Dialog management ////////

  public MBDialogController getActiveDialog()
  {
    return _dialogManager.getActiveDialog();
  }

  public boolean isShowDialogTitle()
  {
    return _showDialogTitle;
  }

  public void setShowDialogTitle(boolean showDialogTitle)
  {
    _showDialogTitle = showDialogTitle;
  }

  // Tablet specific methods. Some methods are implemented also to run on smartphone.
  // Others are for tablet only.

  public void invalidateActionBar()
  {
    invalidateActionBar(null);
  }

  /***
   * 
   * @param showFirst
   * 
   * @deprecated please use {@link #invalidateActionBar(EnumSet)}
   */
  @Deprecated
  public void invalidateActionBar(boolean showFirst)
  {
    EnumSet<MBActionBarInvalidationOption> options = EnumSet.noneOf(MBActionBarInvalidationOption.class);
    if (showFirst)
    {
      options.add(MBActionBarInvalidationOption.SHOW_FIRST);
    }

    invalidateActionBar(options);
  }

  /**
   * @param showFirst
   * @param notifyListener
   * 
   * @deprecated please use {@link #invalidateActionBar(EnumSet)}
   */
  @Deprecated
  public void invalidateActionBar(boolean showFirst, boolean notifyListener)
  {
    EnumSet<MBActionBarInvalidationOption> options = EnumSet.noneOf(MBActionBarInvalidationOption.class);
    if (showFirst)
    {
      options.add(MBActionBarInvalidationOption.SHOW_FIRST);
    }

    if (notifyListener)
    {
      options.add(MBActionBarInvalidationOption.NOTIFY_LISTENER);
    }

    invalidateActionBar(options);
  }

  /**
   * @param showFirst
   * @param notifyListener
   * @param resetHomeDialog
   * 
   * @deprecated please use {@link #invalidateActionBar(EnumSet)}
   */
  @Deprecated
  public void invalidateActionBar(boolean showFirst, boolean notifyListener, final boolean resetHomeDialog)
  {
    EnumSet<MBActionBarInvalidationOption> options = EnumSet.noneOf(MBActionBarInvalidationOption.class);
    if (showFirst)
    {
      options.add(MBActionBarInvalidationOption.SHOW_FIRST);
    }

    if (notifyListener)
    {
      options.add(MBActionBarInvalidationOption.NOTIFY_LISTENER);
    }

    if (resetHomeDialog)
    {
      options.add(MBActionBarInvalidationOption.RESET_HOME_DIALOG);
    }

    invalidateActionBar(options);
  }

  public void hideSearchView()
  {
    throw new UnsupportedOperationException("This method is not supported on smartphone");
  }

  public void reset()
  {
    _dialogManager.reset();
    MBApplicationController.getInstance().fireInitialOutcomes();
  }

  public MBDialogManager getDialogManager()
  {
    return _dialogManager;
  }

  public MBShutdownHandler getShutdownHandler()
  {
    return _shutdownHandler;
  }

  public void setShutdownHandler(MBShutdownHandler shutdownHandler)
  {
    _shutdownHandler = shutdownHandler;
  }

  /////// STUFF MERGED IN FROM MBNextGenViewManager ////////////

  private Menu               _menu = null;

  private MBActionBarBuilder _actionBarBuilder;

  protected void onPreCreate()
  {
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
  }

  private MBActionBarBuilder constructActionBarBuilder()
  {
    Class<? extends MBActionBarBuilder> customBuilder = MBApplicationFactory.getInstance().getActionBarBuilder();
    try
    {

      if (customBuilder == null) return getDefaultActionBar();
      Constructor<? extends MBActionBarBuilder> constructor = customBuilder.getConstructor(Context.class);
      return constructor.newInstance(this);
    }
    catch (Exception e)
    {
      throw new MBException("Error instantiating " + customBuilder.getName() + " with constructor " + customBuilder.getSimpleName()
                            + "(Context)..", e);
    }
  }

  protected abstract MBActionBarBuilder getDefaultActionBar();

  protected boolean buildOptionsMenu(Menu menu)
  {
    _menu = menu;

    _actionBarBuilder.fillActionBar(getSupportActionBar(), menu);

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

  public void onHomeSelected()
  {
    if (_homeButtonHandler != null)
    {
      _homeButtonHandler.handleButton();
    }
  }

  protected void handleOutcome(MBToolDefinition def)
  {
    MBOutcome outcome = new MBOutcome();
    outcome.setOrigin(new MBOutcome.Origin().withAction(def.getName()));
    outcome.setOutcomeName(def.getOutcomeName());

    MBApplicationController.getInstance().handleOutcome(outcome);
  }

  @Override
  public void onDialogSelected(String dialogName)
  {
    if (dialogName != null)
    {
      _actionBarBuilder.selectTabWithoutReselection(dialogName);

    }

  }

  public void showProgressIndicatorInTool()
  {
    _actionBarBuilder.showProgressIndicatorInTool();
  }

  public void hideProgressIndicatorInTool()
  {
    _actionBarBuilder.hideProgressIndicatorInTool();
  }

  /***
   * @deprecated please use {@link com.itude.mobile.mobbl.core.configuration.MBConditionalDefinition#isPreConditionValid()
   * 
   * @param def
   * @return
   */
  @Deprecated
  protected final boolean isPreConditionValid(MBToolDefinition def)
  {
    if (def.getPreCondition() == null)
    {
      return true;
    }

    MBDocument doc = MBDataManagerService.getInstance().loadDocument(MBConfigurationDefinition.DOC_SYSTEM_EMPTY);

    String result = doc.evaluateExpression(def.getPreCondition());
    Boolean bool = MBParseUtil.strictBooleanValue(result);
    if (bool != null) return bool;
    String msg = "Expression of tool with name=" + def.getName() + " precondition=" + def.getPreCondition() + " is not boolean (result="
                 + result + ")";
    throw new MBExpressionNotBooleanException(msg);
  }

  protected Menu getMenu()
  {
    return _menu;
  }

  public void invalidateActionBar(EnumSet<MBActionBarInvalidationOption> flags)
  {
    _actionBarBuilder.invalidateActionBar(flags);
  }

  public void setHomeButtonHandler(HomeButtonHandler handler)
  {
    _homeButtonHandler = handler;
  }

  public void resetDefaultHomeButtonHandler()
  {
    _homeButtonHandler = new DefaultHomeButtonHandler();
  }

  public static interface HomeButtonHandler
  {
    public void handleButton();
  }
}
