package com.itude.mobile.mobbl2.client.core.controller;

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
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.android.compatibility.ActivityCompatHoneycomb;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogGroupDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidPathException;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController.ApplicationState;
import com.itude.mobile.mobbl2.client.core.controller.helpers.MBActivityHelper;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.services.MBWindowChangeType.WindowChangeType;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.helper.MBSecurityHelper;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThreadHandler;
import com.itude.mobile.mobbl2.client.core.view.MBAlert;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.MBPage.OrientationPermission;
import com.itude.mobile.mobbl2.client.core.view.components.tabbar.MBTabBar;

public class MBViewManager extends FragmentActivity
{
  public enum MBViewState {
    MBViewStateFullScreen, MBViewStatePlain, MBViewStateTabbed, MBViewStateModal
  };

  public enum MBActionBarInvalidationOption {
    SHOW_FIRST, RESET_HOME_DIALOG, NOTIFY_LISTENER
  }

  protected static MBViewManager _instance;

  private Dialog                 _currentAlert;
  private boolean                _singlePageMode;
  private boolean                _showDialogTitle    = false;

  private boolean                _activityExists     = false;
  private boolean                _optionsMenuInvalid = false;

  private int                    _defaultScreenOrientation;

  private MBDialogManager        _dialogManager;

  ///////////////////// Android lifecycle methods

  protected void onPreCreate()
  {
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
  }

  @Override
  protected void onCreate(android.os.Bundle savedInstanceState)
  {
    onPreCreate();

    _dialogManager = new MBDialogManager(this);

    /*
     *  We store our default orientation. This will be used to determine how pages should be shown by default
     *  See setOrientation
     */
    _defaultScreenOrientation = getRequestedOrientation();

    // https://dev.itude.com/jira/browse/BINCKAPPS-1131
    super.onCreate(null);

    FrameLayout container = new FrameLayout(this);
    LayoutParams layout = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    setContentView(container, layout);
    _instance = this;

    MBApplicationController.getInstance().startController();
  }

  public void prepareForApplicationStart()
  {
    _dialogManager.onCreate();
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

  /***
   * Only sets a flag to invalidate the menu on first invocation. The menu is built in {@link #onPrepareOptionsMenu(Menu)}
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    _optionsMenuInvalid = true;
    return false;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    boolean displayMenu = true;

    if (_optionsMenuInvalid)
    {
      menu.clear();

      displayMenu = buildOptionsMenu(menu);

      _optionsMenuInvalid = false;
    }

    return displayMenu;
  }

  protected boolean buildOptionsMenu(Menu menu)
  {
    for (String dialogName : getSortedDialogNames())
    {
      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      if (dialogDefinition.isPreConditionValid() && dialogDefinition.isShowAsTab())
      {
        MenuItem menuItem = menu.add(Menu.NONE, dialogName.hashCode(), Menu.NONE,
                                     MBLocalizationService.getInstance().getTextForKey(dialogDefinition.getTitle()));
        menuItem.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));
        MenuCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);
      }
    }

    return true;
  }

  public void finishFromChild(MBDialogController childController)
  {
    MBDialogDefinition firstDialogDefinition = MBMetadataService.getInstance().getHomeDialogDefinition();
    final String firstDialog = firstDialogDefinition.getName();
    if (!childController.getName().equals(firstDialog))
    {
      if (_dialogManager.getDialog(firstDialogDefinition.getName()) == null)
      {
        createDialogWithID(firstDialogDefinition);
      }
      else
      {
        activateDialogWithName(firstDialog);
      }
      setTitle(MBLocalizationService.getInstance().getTextForKey(firstDialogDefinition.getTitle()));
    }
    else
    {
      String message = MBLocalizationService.getInstance().getTextForKey("close app message");
      String positive = MBLocalizationService.getInstance().getTextForKey("close app positive button");
      String negative = MBLocalizationService.getInstance().getTextForKey("close app negative button");
      new AlertDialog.Builder(this).setMessage(message).setPositiveButton(positive, new OnClickListener()
      {

        @Override
        public void onClick(DialogInterface dialog, int which)
        {
          MBSecurityHelper.getInstance().logOutIfCheckNotSelected();
          finish();
        }
      }).setNegativeButton(negative, new OnClickListener()
      {

        @Override
        public void onClick(DialogInterface dialog, int which)
        {
          dialog.dismiss();
        }
      }).show();

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
      if (!getActiveDialog().onKeyDown(keyCode, event)) return super.onKeyDown(keyCode, event);
      else return true;
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

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item)
  {
    boolean handled = false;
    if (getActiveDialog() != null) handled = getActiveDialog().onMenuItemSelected(featureId, item);
    if (!handled && !super.onMenuItemSelected(featureId, item))
    {
      activateOrCreateDialogWithID(item.getItemId());

    }
    return true;
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

  // Activate a dialog based on the hashed Name
  public void activateOrCreateDialogWithID(int itemId)
  {
    for (MBDialogDefinition dialogDefinition : MBMetadataService.getInstance().getDialogs())
    {
      if (itemId == dialogDefinition.getName().hashCode())
      {
        if (!getActiveDialog().getName().equals(dialogDefinition.getName()))
        {
          boolean activated = activateDialogWithName(dialogDefinition.getName());
          if (!activated)
          {
            if (dialogDefinition.isGroup())
            {
              MBDialogGroupDefinition dialogGroupDefinition = (MBDialogGroupDefinition) dialogDefinition;
              for (MBDialogDefinition childDef : dialogGroupDefinition.getChildren())
              {
                createDialogWithID(childDef);
              }

            }
            else
            {
              createDialogWithID(dialogDefinition);
            }
          }
        }
        else
        {
          getActiveDialog().clearAllViews();
        }
      }
    }
  }

  protected void createDialogWithID(MBDialogDefinition dialogDefinition)
  {
    if (StringUtil.isNotBlank(dialogDefinition.getAction()))
    {
      MBOutcome oc = new MBOutcome();
      oc.setOutcomeName(dialogDefinition.getAction());
      oc.setDialogName(dialogDefinition.getName());
      oc.setNoBackgroundProcessing(true);
      oc.setTransferDocument(false);
      oc.setDisplayMode(Constants.C_DISPLAY_MODE_REPLACE);

      MBApplicationController.getInstance().getOutcomeHandler().handleOutcomeSynchronously(oc, false);
    }
  }

  // Activate a dialog based on the hashed Name
  public void activateDialogWithID(int itemId)
  {
    for (MBDialogDefinition dialogDefinition : MBMetadataService.getInstance().getDialogs())
    {
      if (itemId == dialogDefinition.getName().hashCode())
      {
        if (!getActiveDialog().getName().equals(dialogDefinition.getName()))
        {
          activateDialogWithName(dialogDefinition.getName());
        }
        else
        {
          getActiveDialog().clearAllViews();
        }
      }
    }
  }

  @SuppressLint("NewApi")
  public void invalidateOptionsMenu(boolean resetHomeDialog, final boolean selectHome)
  {
    if (DeviceUtil.getInstance().isPhoneV14() || DeviceUtil.isTablet())
    {
      super.invalidateOptionsMenu();
    }
    else
    {
      _optionsMenuInvalid = true;
    }

    //_dialogManager.reset();
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

  public void showPage(MBPage page, String displayMode, boolean shouldSelectDialog, boolean addToBackStack)
  {

    Log.d(Constants.APPLICATION_NAME,
          "MBViewManager: showPage name=" + page.getPageName() + " dialog=" + page.getDialogName() + " mode=" + displayMode + " type="
              + page.getPageType() + " orientation=" + ((MBPageDefinition) page.getDefinition()).getOrientationPermissions()
              + " backStack=" + addToBackStack);

    if (page.getPageType() == MBPageDefinition.MBPageType.MBPageTypesErrorPage || "POPUP".equals(displayMode))
    {
      showAlertView(page);
    }
    else
    {
      addPageToDialog(page, displayMode, shouldSelectDialog, addToBackStack);
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

  private void addPageToDialog(MBPage page, String displayMode, boolean shouldSelectDialog, boolean addToBackStack)
  {
    MBDialogDefinition topDefinition = MBMetadataService.getInstance().getTopDialogDefinitionForDialogName(page.getDialogName());
    MBDialogController dialogController = _dialogManager.getDialog(topDefinition.getName());
    if (dialogController == null || dialogController.getTemporary())
    {
      activateDialogWithPage(page);
    }
    else
    {
      dialogController.showPage(page, displayMode, page.getDialogName() + page.getPageName(), page.getDialogName(), addToBackStack);
    }

    if (shouldSelectDialog) activateDialogWithName(topDefinition.getName());
  }

  public void activateDialogWithPage(MBPage page)
  {
    if (page != null)
    {
      String dialogName = MBMetadataService.getInstance().getTopDialogDefinitionForDialogName(page.getDialogName()).getName();
      Log.d(Constants.APPLICATION_NAME, "MBViewManager.activateDialogWithPage: dialogName=" + dialogName);

      if (dialogName != null) _dialogManager.activateDialog(dialogName);

      MBDialogController dialog = _dialogManager.getActiveDialog();

      //
      String id = page.getDialogName() + page.getPageName();
      MBApplicationController.getInstance().setPage(id, page);

      // TODO: move to dialogManager 
      setTitle(MBLocalizationService.getInstance().getTextForKey("Blerp" /*dialog.getTitle()*/));
      //setContentView(view);

      MBBasicViewController vc = _dialogManager.getActiveDialog().findFragment(id);

      if (vc != null)
      {
        MBApplicationController.getInstance().changedWindow(vc, WindowChangeType.ACTIVATE);
      }

    }
  }

  @Override
  public void supportInvalidateOptionsMenu()
  {
    ActivityCompatHoneycomb.invalidateOptionsMenu(this);
  }

  public boolean activateDialogWithName(String dialogName)
  {
    return _dialogManager.activateDialog(dialogName);
  }

  public void endDialog(String dialogName, boolean keepPosition)
  {
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

  public void endModalDialog(String modalPageID)
  {
    getActiveDialog().endModalPage(modalPageID);
  }

  public void endModalDialog()
  {
    endModalDialog(MBApplicationController.getInstance().getModalPageID());
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

  public MBDialogController getMenuDialog()
  {
    return _dialogManager.getMenuDialog();
  }

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

  public void invalidateActionBar(EnumSet<MBActionBarInvalidationOption> flags)
  {
    //    throw new UnsupportedOperationException("This method is not supported on smartphone");
  }

  public void showProgressIndicatorInTool()
  {
    throw new UnsupportedOperationException("This method is not supported on smartphone");
  }

  public void hideProgressIndicatorInTool()
  {
    throw new UnsupportedOperationException("This method is not supported on smartphone");
  }

  public MBTabBar getTabBar()
  {
    throw new UnsupportedOperationException("This method is not supported on smartphone");
  }

  public void hideSearchView()
  {
    throw new UnsupportedOperationException("This method is not supported on smartphone");
  }

  public void buildSlidingMenu()
  {
    //    throw new UnsupportedOperationException("This method is not supported on smartphone");
  }

  protected boolean needsSlidingMenu()
  {
    return getMenuDialog() != null;
  }

  public void reset()
  {
    MBOutcome initialOutcome = new MBOutcome();
    initialOutcome.setOriginName("Controller");
    initialOutcome.setOutcomeName("init");
    initialOutcome.setDialogName(getActiveDialogName());
    initialOutcome.setNoBackgroundProcessing(true);
    MBApplicationController.getInstance().handleOutcome(initialOutcome);
  }

}
