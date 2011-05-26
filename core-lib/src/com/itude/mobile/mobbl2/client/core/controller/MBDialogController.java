package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogGroupDefinition;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.MBDevice;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.dialogbuilders.MBDialogBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.dialogbuilders.MBSingleDialogBuilder;
import com.itude.mobile.mobbl2.client.core.view.dialogbuilders.MBSplitDialogBuilder;

public class MBDialogController extends FragmentActivity
{
  private String                     _name;
  private String                     _iconName;
  private String                     _dialogMode;
  private boolean                    _usesNavbar;
  private Object                     _rootController;
  private Object                     _navigationController;
  private int                        _activityIndicatorCount;
  private boolean                    _temporary;
  private final Stack<View>          _viewStack       = new Stack<View>();
  private final Stack<String>        _pageIdStack     = new Stack<String>();
  private final List<Integer>        _sortedDialogIds = new ArrayList<Integer>();
  private final Map<String, Integer> _dialogIds       = new HashMap<String, Integer>();

  // Android lifecycle methods

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    controllerInit();
    viewInit();
  }

  private void controllerInit()
  {
    //    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    Intent intent = getIntent();
    String dialogName = intent.getStringExtra("dialogName");
    if (dialogName != null)
    {
      setName(dialogName);
      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      setIconName(dialogDefinition.getIcon());
      setDialogMode(dialogDefinition.getMode());
      setTitle(dialogDefinition.getTitle());
      if (dialogDefinition instanceof MBDialogGroupDefinition)
      {
        List<MBDialogDefinition> children = ((MBDialogGroupDefinition) dialogDefinition).getChildren();
        for (MBDialogDefinition dialogDef : children)
        {
          int id = UniqueIntegerGenerator.getId();
          addDialogChild(dialogDef.getName(), id);
        }
      }
      else
      {
        addDialogChild(_name, UniqueIntegerGenerator.getId());
      }
      _usesNavbar = ("STACK".equals(dialogDefinition.getMode()));
    }
    else
    {
      Log.w("MOBBL", "MBDialogController.onCreate: unable to find dialogName");
    }
  }

  /**
   * Store the id to be used as a reference to the view
   * 
   * @param name
   * @param id
   */
  private void addDialogChild(String name, int id)
  {
    _dialogIds.put(name, id);
    _sortedDialogIds.add(id);
  }

  private void viewInit()
  {
    RelativeLayout mainContainer = null;

    // handle as a single dialog
    if (_dialogIds.size() == 1)
    {
      MBSingleDialogBuilder builder = MBDialogBuilderFactory.getInstance().getSingleDialogBuilder();
      builder.setSortedDialogIds(_sortedDialogIds);
      mainContainer = (RelativeLayout) builder.build();
    }
    // handle as a group of dialogs
    else if (_dialogIds.size() > 1)
    {
      MBSplitDialogBuilder splitDialogBuilder = MBDialogBuilderFactory.getInstance().getSplitDialogBuilder();
      splitDialogBuilder.setSortedDialogIds(_sortedDialogIds);
      mainContainer = (RelativeLayout) splitDialogBuilder.build();
    }

    setContentView(mainContainer);

    String outcomeID = getIntent().getStringExtra("outcomeID");
    if (outcomeID != null)
    {
      Log.d("MOBBL", "MBDialogController.onCreate: found outcomeID=" + outcomeID);
      MBPage page = MBApplicationController.getInstance().getPage(outcomeID);
      showPage(page, null, outcomeID, page.getDialogName(), false);
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus)
  {
    if (hasFocus) getParent().setTitle(getTitle());
    super.onWindowFocusChanged(hasFocus);
  }

  ////////////////////////////

  /**
   * 
   */
  public void clearAllViews()
  {
    FragmentManager fragmentManager = getSupportFragmentManager();

    if (fragmentManager.getBackStackEntryCount() > 0) fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).getId(),
                                                                                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
  }

  public void popView()
  {
    getSupportFragmentManager().popBackStack();
  }

  public void popViewsUntil(int untilWhichView)
  {

    /*while (_pageIdStack.size() > untilWhichView)
    {
      String pageNameToDestroy = _pageIdStack.peek();
      Log.d("MOBBL", "MBDialogController.popAllViewsUntil popping pageName=" + pageNameToDestroy);

      MBApplicationController.getInstance().changedWindow((MBBasicViewController) getLocalActivityManager()
                                                              .getActivity(_pageIdStack.peek()), WindowChangeType.LEAVING);

      _pageIdStack.pop();
      _viewStack.pop();

      destroyActivity(pageNameToDestroy);
    }

    // Make sure the first view will be used in the dialog
    runOnUiThread(new Runnable()
    {

      public void run()
      {
        setContentView(_viewStack.peek());
        MBApplicationController.getInstance().changedWindow((MBBasicViewController) getLocalActivityManager().getActivity(_pageIdStack
                                                                                                                              .peek()),
                                                            WindowChangeType.ACTIVATE);
      }
    });*/

    // TODO implement using FragmentManager

  }

  public void endModalPage(String pageName)
  {
    getSupportFragmentManager().popBackStackImmediate(pageName, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    // Make sure no unnecessary views are being popped
    MBApplicationController.getInstance().clearModalPageID();
  }

  public String getName()
  {
    return _name;
  }

  public void setName(String name)
  {
    _name = name;
  }

  public String getIconName()
  {
    return _iconName;
  }

  public void setIconName(String iconName)
  {
    _iconName = iconName;
  }

  public String getDialogMode()
  {
    return _dialogMode;
  }

  public void setDialogMode(String dialogMode)
  {
    _dialogMode = dialogMode;
  }

  public Object getRootController()
  {
    return _rootController;
  }

  public void setRootController(Object rootController)
  {
    _rootController = rootController;
  }

  public boolean getTemporary()
  {
    return _temporary;
  }

  public void setTemporary(boolean temporary)
  {
    _temporary = temporary;
  }

  public void showPage(MBPage page, String displayMode, String id, String dialogName, boolean addToBackStack)
  {
    MBApplicationController.getInstance().setPage(id, page);

    if ("POP".equals(displayMode))
    {
      popView();
    }

    MBBasicViewController fragment = new MBBasicViewController();
    Bundle args = new Bundle();
    args.putString("id", id);
    fragment.setArguments(args);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    if (addToBackStack) transaction.addToBackStack(id);

    if (!MBDevice.getInstance().isPhone() && "MODAL".equals(displayMode)) transaction.add(fragment, id);
    else transaction.replace(_dialogIds.get(dialogName), fragment);

    // commitAllowingStateLoss makes sure that the transaction is being commit,
    // even when the target activity is stopped. For now, this comes with the price,
    // that the page being displayed will lose its state after a configuration change (e.g. an orientation change) 
    transaction.commitAllowingStateLoss();
  }

  public void popPageAnimated(boolean animated)
  {
  }

  /*@Override
  public MBBasicViewController getCurrentActivity()
  {
    return getCurrentActivity(true);
  }*/

  /*public MBBasicViewController getCurrentActivity(boolean getTopOfStackIfCurrentActivityNotAvailable)
  {
    if (getLocalActivityManager().getCurrentActivity() == null && !getTopOfStackIfCurrentActivityNotAvailable)
    {
      return null;
    }

    MBBasicViewController currentActivity = (MBBasicViewController) getLocalActivityManager().getCurrentActivity();
    if (currentActivity == null && _pageIdStack.size() > 0)
    {
      return (MBBasicViewController) getLocalActivityManager().getActivity(_pageIdStack.peek());
    }
    else
    {
      return currentActivity;
    }

  }*/
}
