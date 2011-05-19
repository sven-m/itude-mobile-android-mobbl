package com.itude.mobile.mobbl2.client.core.controller.util;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.services.MBEvent;
import com.itude.mobile.mobbl2.client.core.services.MBEventListener;
import com.itude.mobile.mobbl2.client.core.services.MBWindowChangedEventListener;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.components.MBHeader;

public class MBBasicViewController extends DialogFragment implements MBEventListener, MBWindowChangedEventListener
{
  private MBPage              _page;
  private ScrollView          _mainScrollView        = null;
  private View                _rootView              = null;
  private View                _mainScrollViewContent = null;
  private final List<MBEvent> eventQueue             = new ArrayList<MBEvent>();

  /////////////////////////////////////////
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    String outcomeID = getArguments().getString("id");
    if (outcomeID != null)
    {
      Log.d("MOBBL", "MBBasicViewController.onCreate: found id=" + outcomeID);
      MBPage page = MBApplicationController.getInstance().getPage(outcomeID);
      setPage(page);
    }

    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    if (getShowsDialog())
    {
      // TODO implement modal dialog frame
      return null;
    }
    else
    {
      ViewGroup view = MBViewBuilderFactory.getInstance().getPageViewBuilder().buildPageView(_page, MBViewState.MBViewStatePlain);
      MBViewBuilderFactory.getInstance().getStyleHandler().styleBackground(view);

      return view;
    }
  }

  ////////////////////////////////////////

  public MBPage getPage()
  {
    return _page;
  }

  public void setPage(MBPage page)
  {
    _page = page;
    _page.setViewController(this);
  }

  public void rebuildView(boolean contentViewNeedsToBeSet)
  {
    getPage().rebuild();

    if (contentViewNeedsToBeSet)
    {
      ViewGroup view = MBViewBuilderFactory.getInstance().getPageViewBuilder().buildPageView(getPage(), MBViewState.MBViewStatePlain);
      MBViewBuilderFactory.getInstance().getStyleHandler().styleBackground(view);
      getDialog().setContentView(view);
    }
  }

  public void handleException(Exception exception)
  {
    if (getPage() != null)
    {
      getPage().handleException(exception);
    }
  }

  public void addEventToQueue(MBEvent event)
  {
    eventQueue.add(event);
  }

  public void removeEventFromQueue(MBEvent event)
  {
    eventQueue.remove(event);
  }

  public void onAfterHandlingEvents()
  {
    eventQueue.clear();
  }

  public List<MBEvent> getEventQueue()
  {
    return eventQueue;
  }

  public boolean hasOutstandingEvents()
  {
    return (eventQueue != null && eventQueue.size() > 0);
  }

  public void handleOnWindowActivated()
  {
    if (hasOutstandingEvents())
    {
      onAfterHandlingEvents();
    }

    // Make sure orientation for the page is as expected
    MBViewManager.getInstance().setOrientation(getPage());
  }

  public void handleOnLeavingWindow()
  {
  }

  public void setRootView(View rootView)
  {
    _rootView = rootView;
  }

  public View getRootView()
  {
    return _rootView;
  }

  public MBHeader getHeaderView()
  {
    if (_rootView == null)
    {
      return ((MBHeader) getView().findViewById(android.R.id.content).findViewWithTag(Constants.C_PAGE_CONTENT_HEADER_VIEW));
    }
    else
    {
      return getHeaderViewFromRoot(_rootView);
    }

  }

  public MBHeader getHeaderViewFromRoot(View root)
  {
    return ((MBHeader) root.findViewWithTag(Constants.C_PAGE_CONTENT_HEADER_VIEW));
  }

  public View getMainScrollViewContent()
  {
    if (_mainScrollViewContent == null)
    {
      _mainScrollViewContent = getMainScrollView().getChildAt(0);
    }
    return _mainScrollViewContent;
  }

  public ScrollView getMainScrollView()
  {
    if (_rootView == null)
    {
      if (_mainScrollView == null)
      {
        _mainScrollView = (ScrollView) getView().getRootView().findViewWithTag(Constants.C_PAGE_CONTENT_VIEW);
      }
    }
    else
    {
      return getMainScrollViewFromRoot(_rootView);
    }

    return _mainScrollView;
  }

  public ScrollView getMainScrollViewFromRoot(View root)
  {
    if (_mainScrollView == null)
    {
      _mainScrollView = (ScrollView) root.findViewWithTag(Constants.C_PAGE_CONTENT_VIEW);
    }

    return _mainScrollView;
  }

  public void replaceMainScrollViewContent(final View newContent)
  {
    getMainScrollView().removeAllViews();
    _mainScrollView.addView(newContent);
  }

  public void fillMainScrollViewContentFromPanel(final MBPanel panel)
  {
    MBPanelViewBuilder pnvb = MBViewBuilderFactory.getInstance().getPanelViewBuilder();
    View newContent = pnvb.buildPanelView(panel, null);
    setMainScrollViewContent(newContent);
    replaceMainScrollViewContent(newContent);
  }

  public void fillMainScrollViewContentFromPanelInBackground(final MBPanel panel)
  {
    Thread backgroundThread = new Thread()
    {

      @Override
      public void run()
      {
        Log.d(Constants.APPLICATION_NAME, "MBBasicViewController.fillMainScrollViewContentFromPanelInBackground");
        fillMainScrollViewContentFromPanel(panel);
      }

    };
    backgroundThread.start();

  }

  public void setMainScrollViewContent(View scrollViewContent)
  {
    _mainScrollViewContent = scrollViewContent;
  }

  /**
   * 
   * @param config contains information regarding orientation change
   * @return true if the orientationchange was handled
   */
  public boolean handleOrientationChange(Configuration config)
  {
    return false;
  }
}
