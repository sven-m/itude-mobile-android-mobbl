package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.android.util.ScreenUtil;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.android.util.UniqueIntegerGenerator;
import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogGroupDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.controller.helpers.MBActivityHelper;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;
import com.itude.mobile.mobbl2.client.core.util.threads.exception.MBInterruptedException;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.builders.MBDialogViewBuilder.MBDialogType;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBDialogController extends ContextWrapper
{

  private String                     _name;
  private String                     _iconName;
  private String                     _dialogMode;
  private String                     _outcomeId;
  private Object                     _rootController;
  private boolean                    _temporary;
  private final List<Integer>        _sortedDialogIds      = new ArrayList<Integer>();
  private final Map<String, Integer> _dialogIds            = new HashMap<String, Integer>();
  private final Map<String, String>  _childDialogModes     = new HashMap<String, String>();
  private View                       _mainContainer;
  private boolean                    _shown                = false;
  private FragmentStack              _fragmentStack;
  private String                     _title;
  private boolean                    _clearDialog;
  private Configuration              _configurationChanged = null;
  private final Queue<ShowPageEntry> _queuedPages          = new LinkedList<MBDialogController.ShowPageEntry>();

  public MBDialogController()
  {
    super(MBViewManager.getInstance());
  }

  public void init(String dialog, String outcomeId)
  {
    _fragmentStack = new FragmentStack(getSupportFragmentManager());
    setName(dialog);
    setOutcomeId(outcomeId);
    if (controllerInit())
    {
      viewInit();
    }
  }

  public void finish()
  {
    getActivity().finishFromChild(this);
  }

  void shutdown()
  {
    onShutdown();
  }

  protected void onShutdown()
  {
    // hook to be called when the application is shutting down
  }

  private MBViewManager getActivity()
  {
    return MBViewManager.getInstance();
  }

  /**
   * @return true if initialization was successful, false otherwise
   */
  private boolean controllerInit()
  {

    if (getName() != null)
    {
      MBDialogDefinition dialogDefinition = MBMetadataService.getInstance().getDefinitionForDialogName(getName());
      setIconName(dialogDefinition.getIcon());
      setDialogMode(dialogDefinition.getMode());

      _title = MBLocalizationService.getInstance().getTextForKey(dialogDefinition.getTitle());
      if (dialogDefinition.isGroup())
      {
        List<MBDialogDefinition> children = ((MBDialogGroupDefinition) dialogDefinition).getChildren();
        for (MBDialogDefinition dialogDef : children)
        {
          addDialogChild(dialogDef.getName(), UniqueIntegerGenerator.getId(), dialogDef.getMode());
        }
      }
      else
      {
        addDialogChild(_name, UniqueIntegerGenerator.getId(), _dialogMode);
      }
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
    // handle as a single dialog
    if (_dialogIds.size() == 1)
    {
      _mainContainer = MBViewBuilderFactory.getInstance().getDialogViewBuilder().buildDialog(MBDialogType.Single, _sortedDialogIds);
    }
    // handle as a group of dialogs
    else if (_dialogIds.size() > 1)
    {
      _mainContainer = MBViewBuilderFactory.getInstance().getDialogViewBuilder().buildDialog(MBDialogType.Split, _sortedDialogIds);
    }

    if (getOutcomeId() != null)
    {
      /*
       * Log.d(Constants.APPLICATION_NAME,
       * "MBDialogController.onCreate: found outcomeID=" +
       * getOutcomeId()); MBPage page =
       * MBApplicationController.getInstance().getPage(getOutcomeId());
       * showPage(page, null, getOutcomeId(), page.getDialogName(),
       * false);
       */
    }
  }

  // //////////////////////////

  public void activate()
  {
    // this is intentionally not done with runOnUIThread, to make sure the fragment transaction that gets committed in activateWithoutSwitching is run after
    // the .setContentView call as soon as possible
    new Handler().post(new MBThread()
    {
      @Override
      public void runMethod() throws MBInterruptedException
      {
        try
        {
          getActivity().setContentView(_mainContainer);
          activateWithoutSwitching();
          getSupportFragmentManager().executePendingTransactions();
          getActivity().setTitle(_title);
        }
        catch (Throwable t)
        {
          // panic?
          throw new MBException("Meh!", t);
        }
      }
    });

  }

  public void activateWithoutSwitching()
  {
    getFragmentStack().playBackStack();

    if (!_shown)
    {
      _shown = true;
      for (ShowPageEntry entry : _queuedPages)
        showPage(entry);

      _queuedPages.clear();
    }

    if (_clearDialog)
    {
      getFragmentStack().emptyBackStack(false);
      _fragmentStack = new FragmentStack(getSupportFragmentManager());
      _clearDialog = false;
      _queuedPages.clear();
    }

    if (_configurationChanged != null)
    {
      handleOrientationChange(_configurationChanged);
    }

  }

  public void deactivate()
  {
    getFragmentStack().emptyBackStack(true);
    _shown = false;
  }

  public void clearAllViews()
  {
    if (getName().equals(MBViewManager.getInstance().getActiveDialogName())
        && !MBActivityHelper.isApplicationBroughtToBackground(getActivity()))
    {
      getFragmentStack().emptyBackStack(false);
    }
    else
    {
      // _clearDialog = true;
    }
  }

  public void removeOnBackStackChangedListenerOfCurrentDialog()
  {
    //_shown = false;
    if (_fragmentStack != null)
    {
      _fragmentStack.getFragmentManager().removeOnBackStackChangedListener(_fragmentStack);
    }
  }

  public void addOnBackStackChangedListener()
  {

    if (_fragmentStack != null)
    {
      _fragmentStack.getFragmentManager().addOnBackStackChangedListener(_fragmentStack);
    }

  }

  private FragmentManager getSupportFragmentManager()
  {
    return getActivity().getSupportFragmentManager();
  }

  public FragmentStack getFragmentStack()
  {
    return _fragmentStack;
  }

  public void popView()
  {
    if (getFragmentStack().isBackStackEmpty()) finish();
    else getSupportFragmentManager().popBackStack();
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

  private String getOutcomeId()
  {
    return _outcomeId;
  }

  private void setOutcomeId(String outcomeId)
  {
    _outcomeId = outcomeId;
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

  private static class ShowPageEntry
  {
    public MBPage getPage()
    {
      return page;
    }

    public void setPage(MBPage page)
    {
      this.page = page;
    }

    public String getDisplayMode()
    {
      return displayMode;
    }

    public void setDisplayMode(String displayMode)
    {
      this.displayMode = displayMode;
    }

    public String getId()
    {
      return id;
    }

    public void setId(String id)
    {
      this.id = id;
    }

    public String getDialogName()
    {
      return dialogName;
    }

    public void setDialogName(String dialogName)
    {
      this.dialogName = dialogName;
    }

    public boolean isAddToBackStack()
    {
      return addToBackStack;
    }

    public void setAddToBackStack(boolean addToBackStack)
    {
      this.addToBackStack = addToBackStack;
    }

    private MBPage  page;
    private String  displayMode;
    private String  id;
    private String  dialogName;
    private boolean addToBackStack;

  }

  public void showPage(MBPage page, String displayMode, String id, String dialogName, boolean addToBackStack)
  {
    ShowPageEntry entry = new ShowPageEntry();
    entry.setPage(page);
    entry.setDisplayMode(displayMode);
    entry.setId(id);
    entry.setDialogName(dialogName);
    entry.setAddToBackStack(addToBackStack);

    if (this._shown) showPage(entry);
    else _queuedPages.add(entry);
  }

  public void showPage(ShowPageEntry entry)
  {
    MBApplicationController.getInstance().setPage(entry.getId(), entry.getPage());

    if ("POP".equals(entry.getDisplayMode()))
    {
      popView();
    }
    else if ("REPLACE".equals(entry.getDisplayMode())
             || ("SINGLE".equals(_childDialogModes.get(entry.getDialogName())) && entry.getPage().getCurrentViewState() != MBViewState.MBViewStateModal))
    {
      entry.setAddToBackStack(false);
    }
    else if ("REPLACEDIALOG".equals(entry.getDisplayMode()) && !getFragmentStack().isBackStackEmpty())
    {
      getFragmentStack().emptyBackStack(false);
    }

    MBBasicViewController fragment = MBApplicationFactory.getInstance().createFragment(entry.getPage().getPageName());
    fragment.setDialogController(this);
    Bundle args = new Bundle();
    args.putString("id", entry.getId());
    fragment.setArguments(args);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    if (entry.getPage().getCurrentViewState() == MBViewState.MBViewStateModal
        || MBApplicationController.getInstance().getModalPageID() != null)
    {
      String modalPageId = MBApplicationController.getInstance().getModalPageID();

      if (entry.isAddToBackStack())
      {
        transaction.addToBackStack(entry.getId());
      }

      if (modalPageId != null && MBApplicationController.getInstance().getOutcomeWhichCausedModal() != null)
      {
        entry.setDisplayMode(MBApplicationController.getInstance().getOutcomeWhichCausedModal().getDisplayMode());
      }

      boolean fullscreen = false;
      boolean cancelable = false;

      if ("MODAL".equals(entry.getDisplayMode()))
      {
        fullscreen = true;
        cancelable = true;
      }
      if (entry.getDisplayMode() != null)
      {
        if (entry.getDisplayMode().contains("FULLSCREEN"))
        {
          fullscreen = true;
        }

        if (entry.getDisplayMode().contains("WITHCLOSEBUTTON"))
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

      Fragment dialogFragment = getSupportFragmentManager().findFragmentByTag(modalPageId);
      if (dialogFragment != null && !getFragmentStack().isBackStackEmpty())
      {
        getSupportFragmentManager().popBackStack();
      }

      transaction.add(fragment, entry.getId());
    }
    else
    {
      if (entry.isAddToBackStack())
      {
        transaction.addToBackStack(entry.getId());
      }
      else
      {
        if (!getFragmentStack().isBackStackEmpty())
        {
          getSupportFragmentManager().popBackStack();
          transaction.addToBackStack(entry.getId());
        }
      }
      transaction.replace(_dialogIds.get(entry.getDialogName()), fragment, entry.getId());
    }

    // commitAllowingStateLoss makes sure that the transaction is being
    // commit,
    // even when the target activity is stopped. For now, this comes with
    // the price,
    // that the page being displayed will lose its state after a
    // configuration change (e.g. an orientation change)
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
   *         Get a list of fragments of a specific type
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
   * @param id
   *            id of the dialog
   */
  public void handleOnWindowActivated(MBBasicViewController vc)
  {
    if (vc != null) vc.handleOnWindowActivated();
  }

  /**
   * @param id
   *            id of the dialog
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
    if (getName().equals(MBViewManager.getInstance().getActiveDialogName()))
    {
      if (DeviceUtil.isTablet() && "SPLIT".equals(_dialogMode))
      {
        for (int i = 0; i < _sortedDialogIds.size() - 1; i++)
        {
          Fragment fragment = getSupportFragmentManager().findFragmentById(_sortedDialogIds.get(i));
          // if the fragment didn't load correctly (e.g. a network
          // error occurred), we don't want to crash the app
          if (fragment != null)
          {
            FrameLayout fragmentContainer = (FrameLayout) fragment.getView().getParent();
            fragmentContainer.getLayoutParams().width = ScreenUtil.getWidthPixelsForPercentage(getBaseContext(), 33);
          }
        }
      }

      for (MBBasicViewController controller : getAllFragments())
      {
        controller.handleOrientationChange(newConfig);
      }

      String modalPageID = MBApplicationController.getInstance().getModalPageID();
      if (StringUtil.isNotBlank(modalPageID))
      {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(modalPageID);
        if (fragment != null && fragment instanceof MBBasicViewController)
        {
          ((MBBasicViewController) fragment).handleOrientationChange(newConfig);
        }
      }

      _configurationChanged = null;
    }
    else
    {
      _configurationChanged = newConfig;
    }

  }

  public View getMainContainer()
  {
    return _mainContainer;
  }

  // Back button press handling
  public boolean onBackPressed()
  {
    boolean handled = false;
    for (MBBasicViewController controller : getAllFragments())
    {
      if (controller.onBackKeyPressed())
      {
        handled = true;
      }
    }

    if (!handled)
    {
      popView();
    }

    return true;
  }

  /*
   * To enable fragments to catch key events we will need to be able to pass
   * it through to them
   */
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {

    boolean handled = false;

    for (MBBasicViewController controller : getAllFragments())
    {
      if (controller.onKeyDown(keyCode, event))
      {
        handled = true;
      }
    }

    return handled;
  }

  public boolean onMenuItemSelected(int featureId, MenuItem item)
  {
    return false;
  }

  public boolean onSearchRequested()
  {
    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchableInfo searchableInfo = searchManager.getSearchableInfo(MBViewManager.getInstance().getComponentName());

    if (searchableInfo == null)
    {
      return false;
    }

    MBViewManager.getInstance().runOnUiThread(new MBThread()
    {
      @Override
      public void runMethod()
      {
        MBViewManager.getInstance().startSearch(null, false, null, false);
      }
    });
    return true;
  }

  public boolean dispatchTouchEvent(MotionEvent ev)
  {
    return false;
  }

  private static class FragmentStack implements OnBackStackChangedListener
  {

    private final FragmentManager _fragmentManager;

    private static class SavedStackEntry
    {
      public String   id;
      public int      dialogId;
      public Fragment fragment;
    }

    private final Stack<SavedStackEntry> _stack = new Stack<SavedStackEntry>();

    public FragmentStack(FragmentManager manager)
    {
      _fragmentManager = manager;
    }

    public FragmentManager getFragmentManager()
    {
      return _fragmentManager;
    }

    @Override
    public void onBackStackChanged()
    {
      int count = getFragmentManager().getBackStackEntryCount();

      _stack.clear();
      for (; _stack.size() < count;)
      {
        SavedStackEntry entry = new SavedStackEntry();
        BackStackEntry bse = getFragmentManager().getBackStackEntryAt(_stack.size());
        entry.id = bse.getName();

        entry.fragment = getFragmentManager().findFragmentByTag(entry.id);
        entry.dialogId = entry.fragment.getId();
        _stack.push(entry);
      }

    }

    private void playBackStack()
    {
      if (!_stack.isEmpty())
      {
        for (SavedStackEntry sse : _stack)
        {
          if (sse.dialogId != 0)
          {
            FragmentTransaction fr = getFragmentManager().beginTransaction();

            fr.addToBackStack(sse.id);
            fr.replace(sse.dialogId, sse.fragment, sse.id);
            fr.commitAllowingStateLoss();
          }

        }

      }

      getFragmentManager().addOnBackStackChangedListener(this);
    }

    private void emptyBackStack(boolean deactivate)
    {
      if (deactivate)
      {
        getFragmentManager().removeOnBackStackChangedListener(this);
      }

      MBViewManager.getInstance().runOnUiThread(new Runnable()
      {
        @Override
        public void run()
        {
          if (!isBackStackEmpty())
          {
            BackStackEntry backStackEntry = getFragmentManager().getBackStackEntryAt(0);
            getFragmentManager().popBackStackImmediate(backStackEntry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
          }
        }
      });
    }

    public boolean isBackStackEmpty()
    {
      return getFragmentManager().getBackStackEntryCount() == 0;
    }

  }

}
