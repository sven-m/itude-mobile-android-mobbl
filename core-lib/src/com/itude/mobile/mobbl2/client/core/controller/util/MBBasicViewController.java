package com.itude.mobile.mobbl2.client.core.controller.util;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.controller.util.trace.StrictModeWrapper;
import com.itude.mobile.mobbl2.client.core.services.MBEvent;
import com.itude.mobile.mobbl2.client.core.services.MBEventListener;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBWindowChangedEventListener;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBDevice;
import com.itude.mobile.mobbl2.client.core.util.MBProperties;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;
import com.itude.mobile.mobbl2.client.core.util.ViewUtilities;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;
import com.itude.mobile.mobbl2.client.core.view.MBPage;
import com.itude.mobile.mobbl2.client.core.view.MBPanel;
import com.itude.mobile.mobbl2.client.core.view.builders.MBPanelViewBuilder;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl2.client.core.view.components.MBHeader;

/**
 * @author Coen Houtman
 * 
 * View controller for displaying one MBPage. An MBBasicViewController can be displayed in the following ways:
 *  - fullscreen
 *  - as part of a screen (Fragment)
 *  - modal
 *  - fullscreen modal
 */
public class MBBasicViewController extends DialogFragment implements MBEventListener, MBWindowChangedEventListener, OnClickListener
{
  private ViewGroup           _contentView;
  private MBPage              _page;
  private ScrollView          _mainScrollView        = null;
  private View                _rootView              = null;
  private View                _mainScrollViewContent = null;
  private boolean             _isDialogClosable      = false;
  private boolean             _isDialogFullscreen    = false;
  private boolean             _isDialogCancelable    = false;                   //i.e. back button dismisses dialog when true
  private final List<MBEvent> eventQueue             = new ArrayList<MBEvent>();
  private static boolean      _strictModeAvailable;

  //use the StrictModeWrapper to see if we are running on Android 2.3 or higher and StrictMode is available
  static
  {
    try
    {
      StrictModeWrapper.checkAvailable();
      _strictModeAvailable = true;
    }
    catch (Throwable throwable)
    {
      _strictModeAvailable = false;
    }
  }

  /////////////////////////////////////////
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    if (_strictModeAvailable)
    {
      String inDevelopment = MBProperties.getInstance().getValueForProperty(Constants.C_PROPERTY_INDEVELOPMENT);
      if ("true".equals(inDevelopment))
      {
        StrictModeWrapper.enableDefaults();
      }
    }

    // setStyle has no effect when used as a normal Fragment, only when used as a dialog
    setStyle(STYLE_NO_TITLE, getTheme());

    if (getArguments() != null)
    {
      String outcomeID = getArguments().getString("id");
      if (StringUtilities.isNotBlank(outcomeID))
      {
        Log.d(Constants.APPLICATION_NAME, "MBBasicViewController.onCreate: found id=" + outcomeID);

        if (getShowsDialog() && outcomeID != MBApplicationController.getInstance().getModalPageID())
        {
          _isDialogCancelable = true;
        }

        MBPage page = MBApplicationController.getInstance().getPage(outcomeID);
        setPage(page);
      }
    }
    super.onCreate(savedInstanceState);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    _isDialogClosable = getArguments().getBoolean("closable", false);
    _isDialogFullscreen = getArguments().getBoolean("fullscreen", false);
    _isDialogCancelable = getArguments().getBoolean("cancelable", false) || _isDialogCancelable;

    if (_isDialogClosable && MBDevice.getInstance().isTablet())
    {
      ViewGroup view = buildInitialView();

      // unable to use the holo light theme as pre honeycomb doesn't know AlertDialog.Builder(context, theme) 
      return new AlertDialog.Builder(getActivity())
          .setNeutralButton(MBLocalizationService.getInstance().getTextForKey("Close"), this).setView(view).create();
    }

    if (_isDialogFullscreen)
    {
      setStyle(STYLE_NO_TITLE, android.R.style.Theme);
    }

    return super.onCreateDialog(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    // do stuff when used as a dialog
    if (getShowsDialog())
    {
      setCancelable(_isDialogClosable || _isDialogCancelable);

      // view is already set in onCreateDialog for closable dialogs
      if (_isDialogClosable)
      {
        return super.onCreateView(inflater, container, savedInstanceState);
      }
    }

    if (_contentView == null)
    {
      _contentView = buildInitialView();
    }
    else
    {
      ViewUtilities.detachView(_contentView);
    }

    return _contentView;
  }

  /**
   * Override this method to define the views once, but for both a Dialog or a Fragment.
   * 
   * @return the ViewGroup to display in either a Dialog or a Fragment.
   */
  protected ViewGroup buildInitialView()
  {
    ViewGroup view = MBViewBuilderFactory.getInstance().getPageViewBuilder().buildPageView(_page, MBViewState.MBViewStatePlain);
    MBViewBuilderFactory.getInstance().getStyleHandler().styleBackground(view);

    return view;
  }

  /**
   * @see android.support.v4.app.DialogFragment#onStart()
   * 
   * This method is used to do stuff with the AlertDialog created in onCreateDialog based
   * on optional parameters passed to this DialogFragment.
   */
  @Override
  public void onStart()
  {
    super.onStart();

    // At this point, Dialog.show is invoked; resolves the issue of first calling requestFeature
    // before doing view stuff. But this is only for the AlertDialog, i.e. closable dialogs
    if (getShowsDialog() && _isDialogClosable)
    {
      FrameLayout layout = (FrameLayout) getDialog().findViewById(android.R.id.custom);
      layout.setPadding(0, 0, 0, 0);

      if (MBDevice.getInstance().isTablet())
      {
        styleCloseButton();
      }

      if (_isDialogFullscreen)
      {
        Window window = getDialog().getWindow();
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
      }
    }
  }

  private void styleCloseButton()
  {
    final AlertDialog alertDialog = (AlertDialog) getDialog();

    Button button = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
    if (button != null)
    {
      MBViewBuilderFactory.getInstance().getStyleHandler().styleCloseButtonDialog(button);
    }
  }

  @Override
  public void onDismiss(DialogInterface dialog)
  {
    super.onDismiss(dialog);
    MBApplicationController controller = MBApplicationController.getInstance();
    MBPage rootModalPage = controller.getPage(controller.getModalPageID());
    if (_page.equals(rootModalPage)) controller.removeLastModalPageID();
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

  public void rebuildView(final boolean contentViewNeedsToBeSet)
  {

    MBPage page = getPage();
    page.rebuildView();

    MBThread runnable = new MBThread(page)
    {
      @Override
      public void runMethod()
      {
        if (contentViewNeedsToBeSet)
        {
          ViewGroup fragmentContainer = (ViewGroup) getView();

          if (fragmentContainer != null)
          {
            fragmentContainer.removeAllViews();

            ViewGroup view = MBViewBuilderFactory.getInstance().getPageViewBuilder().buildPageView(getPage(), MBViewState.MBViewStatePlain);
            MBViewBuilderFactory.getInstance().getStyleHandler().styleBackground(view);

            fragmentContainer.addView(view);
          }
          else Log.w(Constants.APPLICATION_NAME, "Failed to refresh view for page " + getPage().getPageName()
                                                 + ", has the activity been created?");
        }
      }
    };
    getActivity().runOnUiThread(runnable);
  }

  public void handleException(Exception exception)
  {
    if (getPage() != null)
    {
      getPage().handleException(exception);
    }
  }

  @Override
  public void addEventToQueue(MBEvent event)
  {
    eventQueue.add(event);
  }

  @Override
  public void removeEventFromQueue(MBEvent event)
  {
    eventQueue.remove(event);
  }

  @Override
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

  @Override
  public void handleOnWindowActivated()
  {
    if (hasOutstandingEvents())
    {
      onAfterHandlingEvents();
    }

    // Make sure orientation for the page is as expected
    MBViewManager.getInstance().setOrientation(getPage());
  }

  @Override
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

  public View getMainScrollViewContentFromRoot(View root)
  {
    if (root != null)
    {
      ScrollView mainScrollViewFromRoot = getMainScrollViewFromRoot(root);
      if (mainScrollViewFromRoot != null)
      {
        return mainScrollViewFromRoot.getChildAt(0);
      }
    }
    return null;
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
    getActivity().runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        getMainScrollView().removeAllViews();
        _mainScrollView.addView(newContent);
      }
    });
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

  @Override
  // onClick listener for closing all modal dialogs
  public void onClick(DialogInterface arg0, int arg1)
  {
    MBViewManager.getInstance().endModalDialog(MBApplicationController.getInstance().getModalPageID());
  }
}
