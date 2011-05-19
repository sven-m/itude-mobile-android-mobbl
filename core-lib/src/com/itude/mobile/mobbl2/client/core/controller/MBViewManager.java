package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.util.MBActivityIndicator;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.controller.util.MBIndeterminateProgressIndicator;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResourceService;
import com.itude.mobile.mobbl2.client.core.services.MBWindowChangeType.WindowChangeType;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBPage;

public class MBViewManager extends ActivityGroup
{
  public enum MBViewState {
    MBViewStateFullScreen, MBViewStatePlain, MBViewStateTabbed, MBViewStateModal
  };

  private static MBViewManager _instance;

  private ArrayList<String>    _dialogControllers;
  private ArrayList<String>    _sortedDialogNames;
  private String               _activeDialogName;
  private Dialog               _currentAlert;
  private Object               _modalController;
  private boolean              _singlePageMode;

  private int                  _showActivityIndicatorQueue = 0;

  ///////////////////// Android lifecycle methods

  @Override
  protected void onCreate(android.os.Bundle savedInstanceState)
  {
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    super.onCreate(savedInstanceState);

    _dialogControllers = new ArrayList<String>();
    _sortedDialogNames = new ArrayList<String>();
    _instance = this;

    MBApplicationController.getInstance().startController();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    setupMenu(menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item)
  {
    if (!getLocalActivityManager().getCurrentActivity().onMenuItemSelected(featureId, item)) activateDialogWithID(item.getItemId());
    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  public void finishFromChild(Activity child)
  {
    if (child instanceof MBDialogController)
    {
      final MBDialogController childController = (MBDialogController) child;
      MBDialogDefinition firstDialogDefinition = MBMetadataService.getInstance().getFirstDialogDefinition();
      String firstDialog = firstDialogDefinition.getName();
      if (!childController.getName().equals(firstDialog))
      {
        activateDialogWithName(firstDialog);
        setTitle(firstDialogDefinition.getTitle());
      }
      else
      {
        String message = MBLocalizationService.getInstance().getTextForKey("close app message");
        String positive = MBLocalizationService.getInstance().getTextForKey("close app positive button");
        String negative = MBLocalizationService.getInstance().getTextForKey("close app negative button");
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton(positive, new OnClickListener()
        {

          public void onClick(DialogInterface dialog, int which)
          {
            finish();
          }
        }).setNegativeButton(negative, new OnClickListener()
        {

          public void onClick(DialogInterface dialog, int which)
          {
            dialog.dismiss();
          }
        }).show();

      }
    }
    else super.finishFromChild(child);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    return getLocalActivityManager().getCurrentActivity().onKeyDown(keyCode, event);
  }

  /////////////////////////////////////////////////////

  public void setupMenu(Menu menu)
  {
    for (String dialogName : getSortedDialogNames())
    {
      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      MenuItem menuItem = menu.add(Menu.NONE, dialogName.hashCode(), Menu.NONE, dialogDefinition.getTitle());
      menuItem.setIcon(MBResourceService.getInstance().getImageByID(dialogDefinition.getIcon()));
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
          getActiveDialog().popViewsUntil(1);
        }
      }
    }
  }

  public void setActiveDialogName(String activeDialogName)
  {
    _activeDialogName = activeDialogName;
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
    showPage(page, mode, true);
  }

  public void showPage(MBPage page, String displayMode, boolean shouldSelectDialog)
  {

    Log.d("MOBBL",
          "ViewManager: showPage name=" + page.getPageName() + " dialog=" + page.getDialogName() + " mode=" + displayMode + " type="
              + page.getPageType() + " orientation=" + ((MBPageDefinition) page.getDefinition()).getOrientationPermissions());

    if (page.getPageType() == MBPageDefinition.MBPageType.MBPageTypesErrorPage || "POPUP".equals(displayMode))
    {
      showAlertView(page);
    }
    else if ("MODAL".equals(displayMode) && _modalController == null)
    {
      addPageToDialog(page, displayMode, shouldSelectDialog);

      // TODO: support nested modal dialogs

      //_modalController = [[MBNavigationController alloc] initWithRootViewController:[page viewController]];
      // [[[MBViewBuilderFactory sharedInstance] styleHandler] styleNavigationBar:_modalController.navigationBar];
      //[_tabController presentModalViewController:_modalController animated:TRUE];
    }
    else if (_modalController != null)
    {
      //[_modalController pushViewController:[page viewController] animated:TRUE];
    }
    else
    {
      addPageToDialog(page, displayMode, shouldSelectDialog);
    }
  }

  private void showAlertView(MBPage page)
  {

    if (getCurrentAlert() != null) getCurrentAlert().dismiss();

    String title = null;
    String message = null;

    if (page.getDocument().getName().equals(MBConfigurationDefinition.DOC_SYSTEM_EXCEPTION))
    {
      title = page.getDocument().getValueForPath(MBConfigurationDefinition.PATH_SYSTEM_EXCEPTION_NAME);
      message = page.getDocument().getValueForPath(MBConfigurationDefinition.PATH_SYSTEM_EXCEPTION_DESCRIPTION);
    }
    else
    {
      title = page.getTitle();
      message = MBLocalizationService.getInstance().getTextForKey((String) page.getDocument().getValueForPath("/message[0]/@text"));
      if (message == null) message = MBLocalizationService.getInstance().getTextForKey((String) page.getDocument()
                                                                                           .getValueForPath("/message[0]/@text()"));
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message).setTitle(title).setCancelable(true).setNeutralButton("Ok", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface dialog, int id)
      {
        dialog.cancel();
      }
    });
    Dialog dialog = builder.create();
    dialog.show();
    setCurrentAlert(dialog);

  }

  private void addPageToDialog(MBPage page, String displayMode, boolean shouldSelectDialog)
  {

    MBDialogController dialogController = getDialogWithName(page.getDialogName());
    if (dialogController == null || dialogController.getTemporary())
    {
      activateDialogWithPage(page);
    }
    else
    {
      dialogController.showPage(page, displayMode, page.getDialogName() + page.getPageName(), page.getDialogName());
    }

    if (shouldSelectDialog) activateDialogWithName(page.getDialogName());
  }

  public void activateDialogWithPage(MBPage page)
  {
    if (page != null)
    {
      _dialogControllers.add(page.getDialogName());
      Intent intent = new Intent(MBApplicationController.getInstance().getApplicationContext(), MBDialogController.class);
      String dialogName = page.getDialogName();

      if (getViewController(dialogName, null) != getViewController(getActiveDialogName(), null))
      {
        MBApplicationController.getInstance().changedWindow(getViewController(getActiveDialogName(), null), WindowChangeType.LEAVING);
      }

      if (dialogName == null)
      {
        dialogName = getActiveDialogName();
      }

      intent.putExtra("dialogName", dialogName);
      //
      String id = page.getDialogName() + page.getPageName();
      MBApplicationController.getInstance().setPage(id, page);
      intent.putExtra("outcomeID", id);
      //
      Log.d("MOBBL", "MBViewManager.activateDialogWithPage: dialogName=" + dialogName + " and id=" + id);
      Window window = getLocalActivityManager().startActivity(dialogName, intent);
      View view = window.getDecorView();
      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      setTitle(dialogDefinition.getTitle());
      setContentView(view);

      if (getViewController(dialogName, id) != null)
      {
        MBApplicationController.getInstance().changedWindow(getViewController(dialogName, id), WindowChangeType.ACTIVATE);
      }

    }
  }

  private void updateDisplay()
  {
    // TODO Auto-generated method stub

  }

  private MBDialogController getDialogWithName(String dialogName)
  {
    return (MBDialogController) getLocalActivityManager().getActivity(dialogName);
  }

  public void activateDialogWithName(String dialogName)
  {
    Log.d("MOBBL", "MBViewManager.activateDialogWithName: dialogName=" + dialogName);

    if (dialogName != null)
    {
      if (!_sortedDialogNames.contains(dialogName)) _sortedDialogNames.add(dialogName);

      MBDialogController dialogController = getDialogWithName(dialogName);
      // skip if the DialogController is already activated or not created yet.
      if (dialogController != null && dialogController != this.getLocalActivityManager().getCurrentActivity())
      {
        String previousDialogName = ((MBDialogController) getLocalActivityManager().getCurrentActivity()).getName();

        if (getViewController(dialogName, null) != getViewController(previousDialogName, null))
        {
          MBApplicationController.getInstance().changedWindow(getViewController(previousDialogName, null), WindowChangeType.LEAVING);

          Intent dialogIntent = new Intent(MBApplicationController.getInstance().getApplicationContext(), MBDialogController.class);
          dialogIntent.putExtra("dialogName", dialogName);
          Window window = this.getLocalActivityManager().startActivity(dialogName, dialogIntent);
          final View view = window.getDecorView();
          runOnUiThread(new Runnable()
          {

            public void run()
            {
              setContentView(view);
            }
          });

          if (getViewController(dialogName, null) != null)
          {
            MBApplicationController.getInstance().changedWindow(getViewController(dialogName, null), WindowChangeType.ACTIVATE);
          }
        }

      }
    }
  }

  public void endDialog(String dialogName, boolean keepPosition)
  {
  }

  public void popPage(String dialogName)
  {
    getDialogWithName(dialogName).popView();
  }

  public void showIndeterminateProgressIndicator()
  {
    MBIndeterminateProgressIndicator.show(this);
  }

  public void hideIndeterminateProgressIndicator()
  {
    MBIndeterminateProgressIndicator.dismiss(this);
  }

  public void showActivityIndicator()
  {
    if (!MBActivityIndicator.isActive()) MBActivityIndicator.show(this);
  }

  public void hideActivityIndicator()
  {
    if (_showActivityIndicatorQueue > 0) _showActivityIndicatorQueue--;
    else if (MBActivityIndicator.isActive() && _showActivityIndicatorQueue == 0) MBActivityIndicator.dismiss(MBViewManager.this);
  }

  public void postShowActivityIndicator()
  {
    if (MBActivityIndicator.isActive()) _showActivityIndicatorQueue++;
    showActivityIndicator();
  }

  public void makeKeyAndVisible()
  {
  }

  public String getActiveDialogName()
  {
    if (getCurrentActivity() == null)
    {
      return null;
    }

    return ((MBDialogController) getCurrentActivity()).getName();
  }

  public MBDialogController getActiveDialog()
  {
    if (getCurrentActivity() == null)
    {
      return null;
    }

    return (MBDialogController) getCurrentActivity();
  }

  public MBBasicViewController getActiveViewController()
  {
    if (getActiveDialog() == null)
    {
      return null;
    }

//    return getActiveDialog().getCurrentActivity();
    // TODO reimplement refresh events
    return null;
  }

  public void resetView()
  {
  }

  public void resetViewPreservingCurrentDialog()
  {
    // Walk trough all dialogControllers
    for (int i = 0; i < _dialogControllers.size(); i++)
    {
      // Pop all controller apart from first one
      MBDialogController dc = (MBDialogController) getLocalActivityManager().getActivity(_dialogControllers.get(i));
      if (dc != null)
      {
        dc.popViewsUntil(1);
      }

    }
  }

  public void endModalDialog(String modalPageID)
  {
    MBDialogController dc = (MBDialogController) getLocalActivityManager().getCurrentActivity();
    dc.endModalPage(modalPageID);
  }

  public void endModalDialog()
  {
//    removeChild();
  }

  public MBViewState getCurrentViewState()
  {
    // Currently fullscreen is not implemented
    if (_modalController != null) return MBViewState.MBViewStateModal;
    if (_dialogControllers.size() > 1) return MBViewState.MBViewStateTabbed;
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

  @Override
  public boolean onSearchRequested()
  {
    return getLocalActivityManager().getCurrentActivity().onSearchRequested();
  }

  public void clearDialog(String dialogName)
  {
    MBDialogController controller = getDialogWithName(dialogName);
    if (controller != null) controller.clearAllViews();
  }

  public void hideSoftKeyBoard(View triggeringView)
  {
    InputMethodManager imm = (InputMethodManager) triggeringView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(triggeringView.getWindowToken(), 0);
  }

 /* public List<? extends MBBasicViewController> getAllViewControllers()
  {
    ArrayList<MBBasicViewController> result = new ArrayList<MBBasicViewController>();

    for (int i = 0; i < getSortedDialogNames().size(); i++)
    {
      MBDialogController dc = getDialogWithName(getSortedDialogNames().get(i));

      if (dc != null)
      {
        for (int j = 0; j < dc.getSortedPageNames().size(); j++)
        {
          MBBasicViewController bvc = (MBBasicViewController) dc.getLocalActivityManager().getActivity(dc.getSortedPageNames().get(j));

          if (bvc != null)
          {
            result.add(bvc);
          }
        }
      }

    }

    return result;
  }*/

  public MBBasicViewController getViewController(String dialogName, String viewID)
  {
 /*   MBDialogController dc = getDialogWithName(dialogName);
    if (dc != null)
    {
      if (viewID == null)
      {
        return dc.getCurrentActivity();
      }

      return (MBBasicViewController) dc.getLocalActivityManager().getActivity(viewID);
    }*/

    return null;
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

    getActiveViewController().handleOrientationChange(newConfig);

    super.onConfigurationChanged(newConfig);
  }
  
  public int getOrientation()
  {
    return ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
  }
}
