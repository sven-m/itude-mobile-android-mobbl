package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogGroupDefinition;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
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
    /*while (!_pageIdStack.isEmpty())
      popView(true);*/

    //TODO implement using FragmentManager

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
    /*
        for (int i = 0; i < _pageIdStack.size(); i++)
        {

          MBApplicationController.getInstance().changedWindow((MBBasicViewController) getLocalActivityManager()
                                                                  .getActivity(_pageIdStack.peek()), WindowChangeType.LEAVING);

          destroyActivity(_pageIdStack.peek());
          _viewStack.pop();

          if (_pageIdStack.peek().equals(pageName))
          {
            _pageIdStack.pop();
            break;
          }
          _pageIdStack.pop();
        }

        runOnUiThread(new Runnable()
        {

          public void run()
          {
            setContentView(_viewStack.peek());

            MBApplicationController.getInstance().changedWindow((MBBasicViewController) getLocalActivityManager().getActivity(_pageIdStack
                                                                                                                                  .peek()),
                                                                WindowChangeType.ACTIVATE);
          }
        });

        // Make sure no unnecessary views are being popped
        MBApplicationController.getInstance().clearModalPageID();*/

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
    /*final Intent intent = MBApplicationFactory.getInstance().createIntent(this, page.getPageName());
    intent.putExtra("id", id);
    MBApplicationController.getInstance().setPage(id, page);

    int pageOccurs = countPageOccurences(id);
    if (pageOccurs > 0) id += pageOccurs;

    final String finalId = id;

    if (displayMode != null)
    {
      if (displayMode.equals("POP"))
      {
        popView();
      }
    }
    runOnUiThread(new Runnable()
    {

      public void run()
      {

        Window window = getLocalActivityManager().startActivity(finalId, intent);
        View view = window.getDecorView();
        pushView(finalId, view);

      }

    });*/

    MBApplicationController.getInstance().setPage(id, page);

    if (displayMode != null)
    {
      if (displayMode.equals("POP"))
      {
        //        popView();
      }
    }

    MBBasicViewController fragment = new MBBasicViewController();
    Bundle args = new Bundle();
    args.putString("id", id);
    fragment.setArguments(args);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(_dialogIds.get(dialogName), fragment);
    if (addToBackStack) transaction.addToBackStack(null);
    transaction.commit();
  }

  public void popPageAnimated(boolean animated)
  {
  }

  public Object view()
  {
    return null;
  }

  public Object screenBoundsForDisplayMode(String displayMode)
  {
    return null;
  }

  public void setDialogGroupName(String groupName)
  {
    // TODO Auto-generated method stub

  }

  public void setPosition(String position)
  {
    // TODO Auto-generated method stub

  }

  public List<String> getSortedPageNames()
  {
    ArrayList<String> result = new ArrayList<String>();
    result.addAll(_pageIdStack);

    return result;
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
