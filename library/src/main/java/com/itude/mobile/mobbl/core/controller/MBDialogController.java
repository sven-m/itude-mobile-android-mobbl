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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.itude.mobile.android.util.UniqueIntegerGenerator;
import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageStackDefinition;
import com.itude.mobile.mobbl.core.controller.helpers.MBActivityHelper;
import com.itude.mobile.mobbl.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl.core.services.MBLocalizationService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.threads.MBThread;
import com.itude.mobile.mobbl.core.util.threads.exception.MBInterruptedException;
import com.itude.mobile.mobbl.core.view.MBPage;
import com.itude.mobile.mobbl.core.view.builders.MBDialogDecorator;
import com.itude.mobile.mobbl.core.view.builders.MBViewBuilderFactory;

/**
 * Dialog controller class
 */
public class MBDialogController extends ContextWrapper
{

  private String                                   _name;
  private String                                   _iconName;
  private String                                   _dialogMode;
  private Object                                   _rootController;
  private boolean                                  _temporary;
  private final Map<String, MBPageStackController> _pageStacks           = new HashMap<String, MBPageStackController>();
  private final List<Integer>                      _sortedDialogIds      = new ArrayList<Integer>();
  private View                                     _mainContainer;
  private boolean                                  _shown                = false;
  private FragmentStack                            _fragmentStack;
  private String                                   _title;
  private Configuration                            _configurationChanged = null;
  private final Queue<ShowPageEntry>               _queuedPages          = new LinkedList<MBDialogController.ShowPageEntry>();
  private String                                   _defaultPageStack     = null;
  private String                                   _contentType;
  private MBDialogDecorator                        _decorator;

  public MBDialogController()
  {
    super(MBViewManager.getInstance());
  }

  public void init(String dialog, String outcomeId)
  {
    _fragmentStack = new FragmentStack(getSupportFragmentManager());
    setName(dialog);
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
      _contentType = dialogDefinition.getContentType();

      _title = MBLocalizationService.getInstance().getTextForKey(dialogDefinition.getTitle());
      List<MBPageStackDefinition> children = dialogDefinition.getChildren();
      for (MBPageStackDefinition pageStackDef : children)
      {
        if (_defaultPageStack == null) _defaultPageStack = pageStackDef.getName();
        addPageStack(pageStackDef.getName(), UniqueIntegerGenerator.getId(), pageStackDef.getMode());
      }

      _decorator = MBViewBuilderFactory.getInstance().getDialogDecoratorBuilder().createDecorator(dialogDefinition.getDecorator(), this);
      return true;
    }
    else
    {
      MBLog.w(Constants.APPLICATION_NAME, "MBDialogController.onCreate: unable to find dialogName");
      return false;
    }
  }

  /**
   * Store the id to be used as a reference to the view
   * 
   * @param name
   * @param id
   */
  private void addPageStack(String name, int id, String mode)
  {
    MBPageStackController pageStack = new MBPageStackController(this, id, name, mode);
    _pageStacks.put(pageStack.getName(), pageStack);
    _sortedDialogIds.add(id);
  }

  public Map<String, MBPageStackController> getPageStacks()
  {
    return _pageStacks;
  }

  private void viewInit()
  {
    _mainContainer = MBViewBuilderFactory.getInstance().getDialogContentBuilder().buildDialog(_contentType, _sortedDialogIds);

  }

  // //////////////////////////

  public void activate()
  {
    try
    {
      MBViewManager.getInstance().runOnUiThread(new MBThread()
      {
        @Override
        public void runMethod() throws MBInterruptedException
        {
          _decorator.show();

          activateWithoutSwitching();

          getSupportFragmentManager().executePendingTransactions();
        }
      });

    }
    catch (Throwable t)
    {
      // panic?
      throw new MBException("Error trying to activate dialog " + getName(), t);
    }
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

    if (_configurationChanged != null)
    {
      handleOrientationChange(_configurationChanged);
    }

  }

  public void deactivate(boolean maintainStack)
  {
    removeOnBackStackChangedListenerOfCurrentDialog();
    if (!maintainStack) getFragmentStack().emptyBackStack(true);
    _shown = false;
  }

  public void clearAllViews()
  {
    if (getName().equals(MBViewManager.getInstance().getActiveDialogName())
        && !MBActivityHelper.isApplicationBroughtToBackground(getActivity()))
    {
      getFragmentStack().emptyBackStack(false);
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

  FragmentManager getSupportFragmentManager()
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

  static class ShowPageEntry
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
    MBPageStackController pageStack = _pageStacks.get(entry.getDialogName());

    pageStack.showPage(entry);

  }

  public List<MBBasicViewController> getAllFragments()
  {
    return getAllFragments(MBBasicViewController.class);
  }

  /**
   * @param clazz Class
   * @return {@link java.util.List} of fragments
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

    if (!_pageStacks.isEmpty())
    {
      int frID = _pageStacks.get(name).getId();
      fragment = (MBBasicViewController) getSupportFragmentManager().findFragmentById(frID);
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
   * @param vc the {@link com.itude.mobile.mobbl.core.controller.util.MBBasicViewController}
   */
  public void handleOnWindowActivated(MBBasicViewController vc)
  {
    if (vc != null) vc.handleOnWindowActivated();
  }

  /**
   * @param vc the {@link com.itude.mobile.mobbl.core.controller.util.MBBasicViewController}
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

      MBViewBuilderFactory.getInstance().getDialogContentBuilder().handleConfigurationChanged(newConfig, this);

      for (MBBasicViewController controller : getAllFragments())
      {
        controller.handleOrientationChange(newConfig);
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

  class FragmentStack implements OnBackStackChangedListener
  {

    private final FragmentManager _fragmentManager;

    private class SavedStackEntry
    {
      public String   id;
      public int      dialogId;
      public Fragment fragment;
    }

    private int                          _stackRoot = 0;
    private boolean                      _archived  = true;
    private final Stack<SavedStackEntry> _stack     = new Stack<SavedStackEntry>();

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

      boolean emptyBefore = _stack.isEmpty();
      _stack.clear();
      for (int i = _stackRoot; i < count; ++i)
      {
        SavedStackEntry entry = new SavedStackEntry();
        BackStackEntry bse = getFragmentManager().getBackStackEntryAt(i);
        entry.id = bse.getName();

        entry.fragment = getFragmentManager().findFragmentByTag(entry.id);
        entry.dialogId = entry.fragment.getId();
        _stack.push(entry);
      }

      if (_stack.isEmpty() && !emptyBefore)
      {
        onEmptiedBackStack();
      }
    }

    void playBackStack()
    {
      if (_archived)
      {
        _stackRoot = getFragmentManager().getBackStackEntryCount();
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
        _archived = false;
        getSupportFragmentManager().executePendingTransactions();
      }

      getFragmentManager().addOnBackStackChangedListener(this);
    }

    void emptyBackStack(boolean deactivate)
    {
      if (deactivate)
      {
        _archived = true;
        getFragmentManager().removeOnBackStackChangedListener(this);
      }

      MBViewManager.getInstance().runOnUiThread(new Runnable()
      {
        @Override
        public void run()
        {
          if (!isBackStackEmpty())
          {
            BackStackEntry backStackEntry = getFragmentManager().getBackStackEntryAt(_stackRoot);
            getFragmentManager().popBackStackImmediate(backStackEntry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
          }
        }
      });
    }

    public boolean isBackStackEmpty()
    {
      int count = getFragmentManager().getBackStackEntryCount();
      return count == _stackRoot;
    }

  }

  public String getDefaultPageStack()
  {
    return _defaultPageStack;
  }

  public String getContentType()
  {
    return _contentType;
  }

  public String getTitle()
  {
    return _title;
  }

  public MBDialogDecorator getDecorator()
  {
    return _decorator;
  }

  public void popAll()
  {
    getFragmentStack().emptyBackStack(false);
  }

  public void dismiss()
  {
    popAll();
    MBViewManager.getInstance().getDialogManager().deactivateDialog(getName());
  }

  public void onEmptiedBackStack()
  {
    if (_shown) _decorator.emptiedBackStack();
  }
}
