package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

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
import com.itude.mobile.mobbl2.client.core.util.CollectionUtilities;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBDevice;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.util.helper.MBSecurityHelper;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThreadHandler;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.components.MBTabBar;

public class MBViewManager extends FragmentActivity
{
  public enum MBViewState {
    MBViewStateFullScreen, MBViewStatePlain, MBViewStateTabbed, MBViewStateModal
  };

  protected static MBViewManager          _instance;

  private ArrayList<String>               _dialogControllers;
  private ArrayList<String>               _sortedDialogNames;
  private Map<String, MBDialogController> _controllerMap;
  private Dialog                          _currentAlert;
  private boolean                         _singlePageMode;
  private String                          _activeDialog;

  ///////////////////// Android lifecycle methods

  protected void onPreCreate()
  {
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
  }

  @Override
  protected void onCreate(android.os.Bundle savedInstanceState)
  {
    onPreCreate();

    // https://dev.itude.com/jira/browse/BINCKAPPS-1131
    super.onCreate(null);

    FrameLayout container = new FrameLayout(this);
    LayoutParams layout = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    setContentView(container, layout);

    _dialogControllers = new ArrayList<String>();
    _sortedDialogNames = new ArrayList<String>();
    _controllerMap = new HashMap<String, MBDialogController>();
    _instance = this;

    MBApplicationController.getInstance().startController();
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
    if (StringUtilities.isNotBlank(outcomeName))
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
  public boolean onCreateOptionsMenu(Menu menu)
  {
    if (MBDevice.isTablet() || MBDevice.getInstance().isPhoneV14())
    {
      return false;
    }

    for (String dialogName : getSortedDialogNames())
    {
      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      MenuItem menuItem = menu.add(Menu.NONE, dialogName.hashCode(), Menu.NONE,
                                   MBLocalizationService.getInstance().getTextForKey(dialogDefinition.getTitle()));
      menuItem.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));
      MenuCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    return true;
  }

  public void finishFromChild(MBDialogController childController)
  {
    MBDialogDefinition firstDialogDefinition = MBMetadataService.getInstance().getHomeDialogDefinition();
    final String firstDialog = firstDialogDefinition.getName();
    if (!childController.getName().equals(firstDialog))
    {
      if (MBDevice.getInstance().isPhone() || MBDevice.getInstance().isPhoneV14())
      {
        activateDialogWithName(firstDialog);
      }
      else if (MBDevice.getInstance().isTablet())
      {
        runOnUiThread(new Runnable()
        {
          @Override
          public void run()
          {
            selectTab(firstDialog.hashCode());
          }
        });
      }
      setTitle(firstDialogDefinition.getTitle());
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

    return super.onKeyDown(keyCode, event);
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
    if (StringUtilities.isNotBlank(dialogDefinition.getAction()))
    {
      MBOutcome oc = new MBOutcome();
      oc.setOutcomeName(dialogDefinition.getAction());
      oc.setDialogName(dialogDefinition.getName());
      oc.setNoBackgroundProcessing(true);
      oc.setTransferDocument(false);
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

  public void showPage(MBPage page, String mode)
  {
    showPage(page, mode, true, true);
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
    if (StringUtilities.isNotBlank(label))
    {
      if ("neutral".equals(element.getName()))
      {
        builder.setNeutralButton(MBLocalizationService.getInstance().getTextForKey(label), new OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            String outcome = element.getValueForAttribute("outcome");
            if (StringUtilities.isBlank(outcome))
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
            if (StringUtilities.isBlank(outcome))
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
            if (StringUtilities.isBlank(outcome))
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
    MBDialogController dialogController = getDialog(topDefinition.getName());
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

      _dialogControllers.add(dialogName);

      if (!CollectionUtilities.isEqualCollection(getViewControllers(dialogName), getViewControllers(getActiveDialogName())))
      {
        MBDialogController dialogController = getDialog(getActiveDialogName());
        // skip if the DialogController is already activated or not created yet.
        if (dialogController != null && dialogController != getActiveDialog())
        {
          // Some Android smartphone devices don't onPause an Activity when expected. 
          // This is a workaround to make sure that all activities handle their stuff when leaving.
          dialogController.handleAllOnLeavingWindow();
        }
      }

      if (dialogName == null)
      {
        dialogName = getActiveDialogName();
      }

      //
      String id = page.getDialogName() + page.getPageName();
      MBApplicationController.getInstance().setPage(id, page);

      //
      MBDialogController dc = startDialog(dialogName, id);
      //View view = window.getDecorView();
      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      setTitle(dialogDefinition.getTitle());
      //setContentView(view);

      MBBasicViewController vc = findViewController(dialogName, id);

      if (vc != null)
      {
        MBApplicationController.getInstance().changedWindow(vc, WindowChangeType.ACTIVATE);
      }

    }
  }

  private MBDialogController startDialog(String dialogName, String outcomeId)
  {
    MBDialogController controller = _controllerMap.get(dialogName);
    if (controller == null)
    {
      controller = MBApplicationFactory.getInstance().createDialogController();
      controller.init(dialogName, outcomeId);
      _controllerMap.put(dialogName, controller);
    }

    if (_activeDialog == null)
    {
      controller.activate();
      _activeDialog = dialogName;
    }

    return controller;
  }

  public void supportInvalidateOptionsMenu()
  {
    runOnUiThread(new Runnable()
    {

      @Override
      public void run()
      {
        MBViewManager.super.invalidateOptionsMenu();
      }
    });
  }

  private MBDialogController activateDialog(String dialogName)
  {
    MBDialogController controller = startDialog(dialogName, null);

    if (getActiveDialog() != null) getActiveDialog().deactivate();
    controller.activate();
    _activeDialog = dialogName;
    return controller;

  }

  public boolean activateDialogWithName(String dialogName)
  {

    boolean activated = false;
    Log.d(Constants.APPLICATION_NAME, "MBViewManager.activateDialogWithName: dialogName=" + dialogName);

    if (dialogName != null)
    {

      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      if (dialogDefinition.getParent() != null)
      {
        dialogName = dialogDefinition.getParent();
        dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      }

      addSortedDialogName(dialogName, dialogDefinition);

      MBDialogController dialogController = getDialog(dialogName);
      // skip if the DialogController is already activated or not created yet.
      if (dialogController != null && dialogController != getActiveDialog())
      {
        activated = true;
        String previousDialogName = getActiveDialogName();

        if (!CollectionUtilities.isEqualCollection(getViewControllers(dialogName), getViewControllers(previousDialogName)))
        {
          MBDialogController previousDialogController = getDialog(previousDialogName);
          if (previousDialogController != null)
          {
            // Some Android smartphone devices don't onPause an Activity when expected. 
            // This is a workaround to make sure that all activities handle their stuff when leaving.
            previousDialogController.handleAllOnLeavingWindow();
          }
        }

        MBDialogController dc = activateDialog(dialogName);

        if (getViewControllers(dialogName).size() > 0)
        {
          dialogController.handleAllOnWindowActivated();
        }

        if (MBDevice.getInstance().isTablet())
        {
          MBTabBar tabBar = getTabBar();
          if (tabBar != null)
          {
            tabBar.selectTab(dialogName.hashCode(), false);
          }
        }
      }
    }

    return activated;
  }

  public void endDialog(String dialogName, boolean keepPosition)
  {
  }

  public void popPage(String dialogName)
  {
    getDialog(dialogName).popView();
  }

  public void makeKeyAndVisible()
  {
  }

  public String getActiveDialogName()
  {
    return _activeDialog;
  }

  public void resetView()
  {
  }

  public void resetViewPreservingCurrentDialog()
  {
    // Walk trough all dialogControllers
    for (MBDialogController dc : getDialogs())
      dc.clearAllViews();

  }

  public void endModalDialog(String modalPageID)
  {

    getActiveDialog().endModalPage(modalPageID);
  }

  public void endModalDialog()
  {
    endModalDialog(MBApplicationController.getInstance().getModalPageID());
  }

  public MBViewState getCurrentViewState()
  {
    if (_dialogControllers.size() > 1)
    {
      return MBViewState.MBViewStateTabbed;
    }
    return MBViewState.MBViewStatePlain;
  }

  public static MBViewManager getInstance()
  {
    return _instance;
  }

  public void setSortedDialogNames(ArrayList<String> sortedDialogNames)
  {
    _sortedDialogNames = sortedDialogNames;
  }

  public ArrayList<String> getSortedDialogNames()
  {
    return _sortedDialogNames;
  }

  public void addSortedDialogName(String dialogName, MBDialogDefinition dialogDefinition)
  {
    if ("TRUE".equals(dialogDefinition.getAddToNavbar()) && !_sortedDialogNames.contains(dialogName))
    {
      _sortedDialogNames.add(dialogName);
    }
  }

  public void addSortedDialogName(String dialogName)
  {
    MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
    if (dialogDefinition.getParent() != null)
    {
      dialogName = dialogDefinition.getParent();
      dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
    }

    if ("TRUE".equals(dialogDefinition.getAddToNavbar()) && !_sortedDialogNames.contains(dialogName))
    {
      _sortedDialogNames.add(dialogName);
    }
  }

  /*  @Override
    public boolean onSearchRequested()
    {
      return getCurrentDialog().onSearchRequested();
    }
  */
  /**
   * @param dialogName dialogName
   */
  public void removeDialog(String dialogName)
  {
    clearDialogFromStack(dialogName);
    MBDialogController activeDialog = getActiveDialog();
    if (activeDialog != null)
    {
      MBBasicViewController fragment = activeDialog.findFragment(dialogName);
      if (fragment != null)
      {
        View root = fragment.getView();
        if (root != null)
        {
          ViewParent parent = root.getParent();
          if (parent instanceof FrameLayout)
          {
            final FrameLayout fragmentContainer = (FrameLayout) parent;
            runOnUiThread(new Runnable()
            {
              @Override
              public void run()
              {
                fragmentContainer.removeAllViews();
              }
            });
          }
        }
      }
    }
  }

  /**
   * @param dialogName dialogName
   */
  public void clearDialogFromStack(String dialogName)
  {
    MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
    if (dialogDefinition.getParent() != null)
    {
      dialogName = dialogDefinition.getParent();
    }
    MBDialogController controller = getDialog(dialogName);
    if (controller != null) controller.clearAllViews();
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

  public List<MBBasicViewController> getViewControllers(String dialogName)
  {
    Log.d(Constants.APPLICATION_NAME, "MBViewManager.getViewControllers: dialogName=" + dialogName);

    List<MBBasicViewController> lijst = new ArrayList<MBBasicViewController>();

    if (dialogName != null)
    {
      MBDialogController dc = getDialog(dialogName);
      if (dc != null)
      {
        List<MBBasicViewController> fragments = dc.getAllFragments();
        if (!fragments.isEmpty()) lijst.addAll(fragments);
      }
    }
    return lijst;

  }

  public MBBasicViewController findViewController(String dialogName, String viewID)
  {
    MBBasicViewController controller = null;
    Log.d(Constants.APPLICATION_NAME, "MBViewManager.findViewController: dialogName=" + dialogName + "' viewId=" + viewID);
    if (dialogName != null && viewID != null)
    {
      MBDialogController dc = getDialog(dialogName);
      if (dc != null)
      {
        controller = dc.findFragment(viewID);
      }
    }
    return controller;
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

    if (page.isAllowedAnyOrientation())
    {
      if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR)
      {
        Log.d(Constants.APPLICATION_NAME, "MBViewManager.setOrientation: Changing to SENSOR");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
      }
    }
    else if (page.isAllowedPortraitOrientation())
    {
      Log.d(Constants.APPLICATION_NAME, "MBViewManager.setOrientation: Changing to PORTRAIT");
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    else if (page.isAllowedLandscapeOrientation())
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

    final Configuration config = new Configuration(newConfig);
    Runnable r = new Runnable()
    {
      @Override
      public void run()
      {
        // Only handle orientationchanges when orientation changed, obviously
        // Also, tell all Dialogs
        for (MBDialogController dc : getDialogs())
        {
          dc.handleOrientationChange(config);
        }
      }
    };
    new Handler().post(r);
  }

  public List<MBBasicViewController> getAllFragments()
  {
    List<MBBasicViewController> list = new ArrayList<MBBasicViewController>();
    // Walk trough all dialogControllers
    for (MBDialogController dc : getDialogs())
    {

      //TODO Duplicaten er nog eens uit halen.
      if (dc != null && !dc.getAllFragments().isEmpty()) list.addAll(dc.getAllFragments());
    }

    return list;
  }

  ////// Dialog management ////////

  private Collection<MBDialogController> getDialogs()
  {
    return _controllerMap.values();
  }

  private MBDialogController getDialog(String name)
  {
    return _controllerMap.get(name);
  }

  public MBDialogController getActiveDialog()
  {
    return getDialog(getActiveDialogName());
  }

 
  // Tablet specific methods. Some methods are implemented also to run on smartphone.
  // Others are for tablet only.

  public void invalidateActionBar(boolean selectFirstTab)
  {
    //    throw new UnsupportedOperationException("This method is not supported on smartphone");
  }

  public void invalidateActionBar(boolean selectFirstTab, boolean notifyListener)
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

  public void selectTab(int hashCode)
  {
    throw new UnsupportedOperationException("This method is not supported on smartphone");
  }

  public void hideSearchView()
  {
    throw new UnsupportedOperationException("This method is not supported on smartphone");
  }

}
