package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogGroupDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBDevice;
import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;
import com.itude.mobile.mobbl2.client.core.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.builders.MBDialogViewBuilder.MBDialogType;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBDialogController extends FragmentActivity
{
  private String                     _name;
  private String                     _iconName;
  private String                     _dialogMode;
  private boolean                    _usesNavbar;
  private Object                     _rootController;
  private int                        _activityIndicatorCount;
  private boolean                    _temporary;
  private final Stack<View>          _viewStack        = new Stack<View>();
  private final Stack<String>        _pageIdStack      = new Stack<String>();
  private final List<Integer>        _sortedDialogIds  = new ArrayList<Integer>();
  private final Map<String, Integer> _dialogIds        = new HashMap<String, Integer>();
  private final Map<String, String>  _childDialogModes = new HashMap<String, String>();
  private boolean                    _clearDialog      = false;

  // Android lifecycle methods

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    if (controllerInit())
    {
      viewInit();
    }
  }

  /**
   * @return true if initialization was successful, false otherwise
   */
  private boolean controllerInit()
  {
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    Intent intent = getIntent();
    String dialogName = intent.getStringExtra("dialogName");

    if (dialogName != null)
    {
      setName(dialogName);
      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(dialogName);
      setIconName(dialogDefinition.getIcon());
      setDialogMode(dialogDefinition.getMode());
      setTitle(MBLocalizationService.getInstance().getTextForKey(dialogDefinition.getTitle()));
      if (dialogDefinition instanceof MBDialogGroupDefinition)
      {
        List<MBDialogDefinition> children = ((MBDialogGroupDefinition) dialogDefinition).getChildren();
        for (MBDialogDefinition dialogDef : children)
        {
          int id = UniqueIntegerGenerator.getId();
          addDialogChild(dialogDef.getName(), id, dialogDef.getMode());
        }
      }
      else
      {
        addDialogChild(_name, UniqueIntegerGenerator.getId(), _dialogMode);
      }
      _usesNavbar = ("STACK".equals(dialogDefinition.getMode()) || "TRUE".equals(dialogDefinition.getAddToNavbar()));

      return true;
    }
    else
    {
      Log.w(Constants.APPLICATION_NAME, "MBDialogController.onCreate: unable to find dialogName");
      return false;
    }
  }

  /**
   * Store the id to be used as a reference to the view
   * 
   * @param name
   * @param id
   */
  private void addDialogChild(String name, int id, String mode)
  {
    _dialogIds.put(name, id);
    _sortedDialogIds.add(id);

    // only add the modes of the children
    if (!name.equals(_name))
    {
      _childDialogModes.put(name, mode);
    }
  }

  private void viewInit()
  {
    RelativeLayout mainContainer = null;

    // handle as a single dialog
    if (_dialogIds.size() == 1)
    {
      mainContainer = (RelativeLayout) MBViewBuilderFactory.getInstance().getDialogViewBuilder()
          .buildDialog(MBDialogType.Single, _sortedDialogIds);
    }
    // handle as a group of dialogs
    else if (_dialogIds.size() > 1)
    {
      mainContainer = (RelativeLayout) MBViewBuilderFactory.getInstance().getDialogViewBuilder()
          .buildDialog(MBDialogType.Split, _sortedDialogIds);
    }

    setContentView(mainContainer);

    String outcomeID = getIntent().getStringExtra("outcomeID");
    if (outcomeID != null)
    {
      Log.d(Constants.APPLICATION_NAME, "MBDialogController.onCreate: found outcomeID=" + outcomeID);
      MBPage page = MBApplicationController.getInstance().getPage(outcomeID);
      showPage(page, null, outcomeID, page.getDialogName(), false);
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus)
  {
    if (hasFocus)
    {
      getParent().setTitle(getTitle());
    }
    super.onWindowFocusChanged(hasFocus);
  }

  ////////////////////////////

  /**
   * 
   */
  public void clearAllViews()
  {
    if (getName().equals(MBViewManager.getInstance().getActiveDialogName()))
    {
      doClearAllViews();
    }
    else
    {
      _clearDialog = true;
    }
  }

  private void doClearAllViews()
  {
    _clearDialog = false;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    if (fragmentManager.getBackStackEntryCount() > 0)
    {
      runOnUiThread(new Runnable()
      {
        @Override
        public void run()
        {
          fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
      });
    }

  }

  public void popView()
  {
    if (isBackStackEmpty()) finish();
    else getSupportFragmentManager().popBackStack();
  }

  public boolean isBackStackEmpty()
  {
    return getSupportFragmentManager().getBackStackEntryCount() == 0;
  }

  public void endModalPage(String pageName)
  {
    if (pageName != null)
    {
      getSupportFragmentManager().popBackStack(pageName, FragmentManager.POP_BACK_STACK_INCLUSIVE);

      // Make sure no unnecessary views are being popped
      MBApplicationController.getInstance().removeLastModalPageID();
    }
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
    else if ("REPLACE".equals(displayMode)
             || ("SINGLE".equals(_childDialogModes.get(dialogName)) && page.getCurrentViewState() != MBViewState.MBViewStateModal))
    {
      addToBackStack = false;
    }

    MBBasicViewController fragment = MBApplicationFactory.getInstance().createFragment(page.getPageName());
    Bundle args = new Bundle();
    args.putString("id", id);
    fragment.setArguments(args);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    if (addToBackStack)
    {
      transaction.addToBackStack(id);
    }

    if (MBDevice.getInstance().isTablet()
        && (page.getCurrentViewState() == MBViewState.MBViewStateModal || MBApplicationController.getInstance().getModalPageID() != null))
    {
      String modalPageID = MBApplicationController.getInstance().getModalPageID();

      if (modalPageID != null && MBApplicationController.getInstance().getOutcomeWhichCausedModal() != null)
      {
        displayMode = MBApplicationController.getInstance().getOutcomeWhichCausedModal().getDisplayMode();
      }

      boolean fullscreen = false;
      boolean cancelable = false;

      if ("MODAL".equals(displayMode))
      {
        fullscreen = true;
        cancelable = true;
      }
      if (displayMode != null)
      {
        if (displayMode.contains("FULLSCREEN"))
        {
          fullscreen = true;
        }

        if (displayMode.contains("WITHCLOSEBUTTON"))
        {
          args.putBoolean("closable", true);
          fragment.setArguments(args);
        }
      }
      if (fullscreen)
      {
        args.putBoolean("fullscreen", true);
        fragment.setArguments(args);
      }

      if (cancelable)
      {
        args.putBoolean("cancelable", true);
        fragment.setArguments(args);
      }

      Fragment dialogFragment = getSupportFragmentManager().findFragmentByTag(modalPageID);
      if (dialogFragment != null && !isBackStackEmpty())
      {
        getSupportFragmentManager().popBackStack();
      }
      transaction.add(fragment, id);
    }
    else {
      transaction.replace(_dialogIds.get(dialogName), fragment);
    }

    // commitAllowingStateLoss makes sure that the transaction is being commit,
    // even when the target activity is stopped. For now, this comes with the price,
    // that the page being displayed will lose its state after a configuration change (e.g. an orientation change) 
    transaction.commitAllowingStateLoss();
  }

  public List<MBBasicViewController> getAllFragments()
  {
    return getAllFragments(MBBasicViewController.class);
  }

  /**
   * @param type
   * @return 
   * @return
   * 
   * Get a list of fragments of a specific type
   */
  public <T extends MBBasicViewController> List<T> getAllFragments(Class<T> clazz)
  {
    ArrayList<T> list = new ArrayList<T>();

    for (Integer dialogId : _sortedDialogIds)
    {
      Fragment fragment = getSupportFragmentManager().findFragmentById(dialogId);

      if (fragment != null && clazz.isInstance(fragment))
      {
        list.add((T) fragment);
      }
    }

    return list;
  }

  public MBBasicViewController findFragment(String name)
  {
    MBBasicViewController fragment = null;

    if (!_dialogIds.isEmpty())
    {
      Integer frID = _dialogIds.get(name);
      if (frID != null)
      {
        fragment = (MBBasicViewController) getSupportFragmentManager().findFragmentById(frID);
      }
    }
    return fragment;
  }

  /**
   * 
   */
  public void handleAllOnWindowActivated()
  {
    if (_clearDialog)
    {
      doClearAllViews();
    }

    for (MBBasicViewController controller : getAllFragments())
    {
      handleOnWindowActivated(controller);
    }
  }

  /**
   * 
   */
  public void handleAllOnLeavingWindow()
  {
    for (MBBasicViewController controller : getAllFragments())
    {
      handleOnLeavingWindow(controller);
    }
  }

  /**
    * @param id id of the dialog
    */
  public void handleOnWindowActivated(MBBasicViewController vc)
  {
    if (vc != null) vc.handleOnWindowActivated();
  }

  /**
   * @param id id of the dialog
   */
  public void handleOnLeavingWindow(MBBasicViewController vc)
  {
    if (vc != null) vc.handleOnLeavingWindow();
  }

  public void popPageAnimated(boolean animated)
  {
  }

  public void handleOrientationChange(Configuration newConfig)
  {
    if (MBDevice.getInstance().isTablet() && "SPLIT".equals(_dialogMode))
    {
      for (int i = 0; i < _sortedDialogIds.size() - 1; i++)
      {
        Fragment fragment = getSupportFragmentManager().findFragmentById(_sortedDialogIds.get(i));
        FrameLayout fragmentContainer = (FrameLayout) fragment.getView().getParent();
        fragmentContainer.getLayoutParams().width = MBScreenUtilities.getWidthPixelsForPercentage(33);
      }
    }

    for (MBBasicViewController controller : getAllFragments())
    {
      controller.handleOrientationChange(newConfig);
    }
  }
}
