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
package com.itude.mobile.mobbl.core.controller.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.android.util.ViewUtilities;
import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.controller.MBDialogController;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.controller.util.trace.StrictModeWrapper;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBDataManagerService.OperationListener;
import com.itude.mobile.mobbl.core.services.MBEvent;
import com.itude.mobile.mobbl.core.services.MBEventListener;
import com.itude.mobile.mobbl.core.services.MBLocalizationService;
import com.itude.mobile.mobbl.core.services.MBWindowChangedEventListener;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.MBProperties;
import com.itude.mobile.mobbl.core.util.threads.MBThread;
import com.itude.mobile.mobbl.core.view.MBOutcomeListenerProtocol;
import com.itude.mobile.mobbl.core.view.MBPage;
import com.itude.mobile.mobbl.core.view.MBPanel;
import com.itude.mobile.mobbl.core.view.builders.MBPanelViewBuilder;
import com.itude.mobile.mobbl.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl.core.view.components.MBHeader;

/**
 * View controller for displaying one MBPage. An MBBasicViewController can be displayed in the following ways:
 * <ul>
 * <li>fullscreen</li>
 * <li>as part of a screen (Fragment)</li>
 * <li>modal</li>
 * <li>fullscreen modal</li>
 * </ul>
 */
public class MBBasicViewController extends DialogFragment
    implements
      MBEventListener,
      MBWindowChangedEventListener,
      OnClickListener,
      OperationListener
{

  private ViewGroup                             _contentView;
  private MBPage                                _page;
  private ScrollView                            _mainScrollView        = null;
  private View                                  _rootView              = null;
  private View                                  _mainScrollViewContent = null;
  private boolean                               _isDialogClosable      = false;
  private boolean                               _isDialogFullscreen    = false;
  private boolean                               _isDialogCancelable    = false;                                     //i.e. back button dismisses dialog when true
  private final List<MBEvent>                   eventQueue             = new ArrayList<MBEvent>();
  private static boolean                        _strictModeAvailable   = false;
  // avoid cyclical dependencies
  private WeakReference<MBDialogController>     _dialogController;
  private boolean                               _rebuildView;
  private final List<MBOutcomeListenerProtocol> _outcomeListeners      = new ArrayList<MBOutcomeListenerProtocol>();

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
      boolean enableStrictMode = MBProperties.getInstance().getBooleanProperty(Constants.C_PROPERTY_STRICTMODE);
      if (enableStrictMode)
      {
        StrictModeWrapper.enableDefaults();
      }
    }

    // setStyle has no effect when used as a normal Fragment, only when used as a dialog
    setStyle(STYLE_NO_TITLE, getTheme());

    if (getArguments() != null)
    {
      String outcomeID = getArguments().getString("id");
      if (StringUtil.isNotBlank(outcomeID))
      {
        MBLog.d(Constants.APPLICATION_NAME, "MBBasicViewController.onCreate: found id=" + outcomeID);

        MBDocument pageDoc = getPage().getDocument();
        if (getPage().isReloadOnDocChange() && pageDoc != null)
        {
          MBDataManagerService.getInstance().registerOperationListener(pageDoc.getDocumentName(), this);
        }
      }

    }
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onResume()
  {
    super.onResume();
    handleOnWindowActivated();
  }

  @Override
  public void onPause()
  {
    super.onPause();
    handleOnLeavingWindow();
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    _isDialogClosable = getArguments().getBoolean("closable", false);
    _isDialogFullscreen = getArguments().getBoolean("fullscreen", false);
    _isDialogCancelable = getArguments().getBoolean("cancelable", false) || _isDialogCancelable;

    if (_isDialogClosable)
    {
      ViewGroup view = buildInitialView(LayoutInflater.from(getActivity()));

      /*
       * Add this view and a close button to a wrapper view that will be used as the content view of our AlertDialog
       */

      // unable to use the holo light theme as pre honeycomb doesn't know AlertDialog.Builder(context, theme) 
      AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
      AlertDialog dialog = adb.create();

      View content = addCloseButtonToClosableDialogView(view, dialog);

      dialog.setView(content);

      dialog.setOnKeyListener(new OnKeyListener()
      {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
        {
          if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
          {
            return onBackKeyPressed();
          }

          return false;
        }
      });

      return dialog;
    }

    if (_isDialogFullscreen)
    {
      setStyle(STYLE_NO_TITLE, android.R.style.Theme);
    }

    return super.onCreateDialog(savedInstanceState);
  }

  public View addCloseButtonToClosableDialogView(View dialogView, final AlertDialog dialogToCloseOnclick)
  {
    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    FragmentActivity context = getActivity();
    LinearLayout wrapper = new LinearLayout(context);
    wrapper.setOrientation(LinearLayout.VERTICAL);

    styleHandler.styleDialogCloseButtonWrapper(wrapper);

    LayoutParams prevDialogViewParams = dialogView.getLayoutParams();
    int width = LayoutParams.MATCH_PARENT;
    int height = LayoutParams.MATCH_PARENT;

    if (prevDialogViewParams != null)
    {
      width = prevDialogViewParams.width;
      height = prevDialogViewParams.height;
    }

    LinearLayout.LayoutParams dialogViewParams = new LinearLayout.LayoutParams(width, height);
    dialogView.setLayoutParams(dialogViewParams);
    wrapper.addView(dialogView);

    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    Button closeButton = new Button(context);
    closeButton.setLayoutParams(buttonParams);
    closeButton.setText(MBLocalizationService.getInstance().getTextForKey("Close"));
    styleHandler.styleDialogCloseButton(closeButton);
    closeButton.setOnClickListener(new View.OnClickListener()
    {

      @Override
      public void onClick(View v)
      {
        MBViewManager.getInstance().endDialog(getDialogController().getName(), false);
      }
    });

    wrapper.addView(closeButton);

    return wrapper;
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
      _contentView = buildInitialView(inflater);
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
  protected ViewGroup buildInitialView(LayoutInflater inflater)
  {
    ViewGroup view = MBViewBuilderFactory.getInstance().getPageViewBuilder().buildPageView(_page);
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
      if (layout != null) layout.setPadding(0, 0, 0, 0);

      styleCloseButton();
    }

    if (getShowsDialog() && _isDialogFullscreen)
    {
      Window window = getDialog().getWindow();
      window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    if (controller != null && controller.getViewManager() != null && controller.getViewManager().getActiveDialog() != null)
    {
      controller.getViewManager().getActiveDialog().handleAllOnWindowActivated();
    }
  }

  @Override
  public void onDestroy()
  {
    MBPage page = getPage();
    if (page != null && page.isReloadOnDocChange())
    {
      MBDocument pageDoc = page.getDocument();

      if (pageDoc != null)
      {
        MBDataManagerService.getInstance().unregisterOperationListener(pageDoc.getDocumentName(), this);
      }
    }

    super.onDestroy();
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

  /**
   * Looks up the {@link MBPage} associated with this instance and sets the view property with a fresh view hierarchy constructed from the page definition.
   * 
   * @param contentViewNeedsToBeSet boolean indicating if the content view needs to be set during rebuild
   */
  public void rebuildView(final boolean contentViewNeedsToBeSet)
  {
    // it is possible for the fragment to get detached in the meantime,
    // so cache the activity
    final Activity activity = getActivity();
    if (activity == null) return;
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

            ViewGroup view = buildInitialView(LayoutInflater.from(activity));

            fragmentContainer.addView(view);
            _contentView = view;
          }
          else MBLog.w(Constants.APPLICATION_NAME, "Failed to refresh view for page " + getPage().getPageName()
                                                   + ", has the activity been created?");
        }
      }
    };
    activity.runOnUiThread(runnable);
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

    if (_rebuildView)
    {
      rebuildView(true);
    }

    // Make sure orientation for the page is as expected
    MBViewManager.getInstance().setOrientation(getPage());

    for (MBOutcomeListenerProtocol listener : _outcomeListeners)
      MBApplicationController.getInstance().getOutcomeHandler().registerOutcomeListener(listener);
  }

  @Override
  public void handleOnLeavingWindow()
  {
    for (MBOutcomeListenerProtocol listener : _outcomeListeners)
      MBApplicationController.getInstance().getOutcomeHandler().unregisterOutcomeListener(listener);
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
    View newContent = pnvb.buildPanelView(panel);
    setMainScrollViewContent(newContent);
    replaceMainScrollViewContent(newContent);
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
    MBViewManager.getInstance().endDialog(getDialogController().getName(), false);
  }

  // Intercepting the back button
  public boolean onBackKeyPressed()
  {
    return false;
  }

  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    return false;
  }

  public void setDialogController(MBDialogController dialog)
  {
    _dialogController = new WeakReference<MBDialogController>(dialog);
  }

  public MBDialogController getDialogController()
  {
    return _dialogController != null ? _dialogController.get() : null;
  }

  @Override
  public void onDocumentStored(MBDocument document)
  {
    getPage().setDocument(document);
    if (isVisible())
    {
      rebuildView(true);
    }
    else
    {
      _rebuildView = true;
    }
  }

  public void registerOutcomeListener(MBOutcomeListenerProtocol listener)
  {
    if (!_outcomeListeners.contains(listener))
    {
      _outcomeListeners.add(listener);
      MBApplicationController.getInstance().getOutcomeHandler().registerOutcomeListener(listener);
    }
  }

  public void unregisterOutcomeListener(MBOutcomeListenerProtocol listener)
  {
    _outcomeListeners.remove(listener);
    MBApplicationController.getInstance().getOutcomeHandler().unregisterOutcomeListener(listener);
  }
}
