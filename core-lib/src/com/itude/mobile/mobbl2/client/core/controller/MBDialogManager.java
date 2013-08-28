package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.itude.mobile.android.util.ComparisonUtil;
import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBaseLifecycleListener;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;
import com.itude.mobile.mobbl2.client.core.util.threads.exception.MBInterruptedException;

public class MBDialogManager extends MBBaseLifecycleListener
{
  private final List<String>                    _sortedDialogNames;
  private final Map<String, MBDialogController> _controllerMap;
  private MBDialogController                    _menuController;
  private String                                _activeDialog;
  private final Activity                        _activity;

  public MBDialogManager(Activity activity)
  {
    _activity = activity;
    _sortedDialogNames = new ArrayList<String>();
    _controllerMap = new HashMap<String, MBDialogController>();
  }

  ///////////////// Lifecycle events ////////////

  @Override
  public void onCreate()
  {
    super.onCreate();

    build();
  }

  @Override
  public void onResume()
  {
    super.onResume();
    if (getActiveDialog() != null) getActiveDialog().activate();
  }

  @Override
  public void onPause()
  {
    super.onPause();

    // Fix for https://mobiledev.itude.com/jira/browse/BINCKRETAILSLA-541
    MBDialogController dc = getActiveDialog();
    dc.removeOnBackStackChangedListenerOfCurrentDialog();

  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();

    // signal the DialogControllers that we are closing down
    for (MBDialogController controller : _controllerMap.values())
      controller.shutdown();

  }

  public void onConfigurationChanged(final Configuration newConfig)
  {
    MBThread thread = new MBThread()
    {
      @Override
      public void runMethod() throws MBInterruptedException
      {
        for (MBDialogController dialog : getDialogs())
          dialog.handleOrientationChange(newConfig);
      }
    };

    _activity.runOnUiThread(thread);
  }

  /////////// Properties /////

  public MBDialogController getActiveDialog()
  {
    return getDialog(_activeDialog);
  }

  @Deprecated
  public Collection<MBDialogController> getDialogs()
  {
    return _controllerMap.values();
  }

  public MBDialogController getMenuDialog()
  {
    return _menuController;
  }

  public List<String> getSortedDialogNames()
  {
    return _sortedDialogNames;
  }

  public MBDialogController getDialog(String name)
  {
    return _controllerMap.get(name);
  }

  ///////////////

  public void reset()
  {
    clear();
    build();
  }

  ////////////// Actual management ////////

  private void clear()
  {
    _sortedDialogNames.clear();
    _controllerMap.clear();
    _menuController = null;
    _activeDialog = null;
  }

  private void build()
  {
    List<MBDialogDefinition> dialogs = MBMetadataService.getInstance().getDialogs();

    for (MBDialogDefinition dialog : dialogs)
    {
      if (dialog.isPreConditionValid()) createDialog(dialog);

    }
  }

  public boolean activateDialog(String dialogName)
  {
    if (ComparisonUtil.safeEquals(_activeDialog, dialogName)) return false;

    MBDialogController dialog = _controllerMap.get(dialogName);
    if (dialog == null) throw new MBException("No dialog " + dialogName + " found!");

    MBDialogController current = getActiveDialog();
    if (current != null) current.deactivate();

    _activeDialog = dialogName;
    dialog.activate();
    return true;
  }

  private void createDialog(MBDialogDefinition definition)
  {
    MBDialogController controller = MBApplicationFactory.getInstance().createDialogController();
    String name = definition.getName();
    controller.init(name, null);
    _controllerMap.put(name, controller);
    _sortedDialogNames.add(name);
    if (definition.isShowAsMenu()) _menuController = controller;
  }

  public void removeDialog(String dialogName)
  {
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
            _activity.runOnUiThread(new Runnable()
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

}
