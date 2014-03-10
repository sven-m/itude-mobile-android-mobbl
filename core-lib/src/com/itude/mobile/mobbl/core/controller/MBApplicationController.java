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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import android.app.Application;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.android.util.ComparisonUtil;
import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBAlertDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl.core.controller.MBViewManager.MBActionBarInvalidationOption;
import com.itude.mobile.mobbl.core.controller.exceptions.MBInvalidOutcomeException;
import com.itude.mobile.mobbl.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBElement;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBEvent;
import com.itude.mobile.mobbl.core.services.MBLocalizationService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.MBWindowChangeType.WindowChangeType;
import com.itude.mobile.mobbl.core.services.exceptions.MBNoDocumentException;
import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.util.imagecache.ImageUtil;
import com.itude.mobile.mobbl.core.view.MBAlert;
import com.itude.mobile.mobbl.core.view.MBOutcomeListenerProtocol;
import com.itude.mobile.mobbl.core.view.MBPage;

/** 
 * Facade for all navigation control and logic sequencing.
 * 
 * The MBApplicationController is responsible for determining which MBPage or MBAction should be constructed when an MBOutcome is triggered.
 * The handleOutcome method is the main usage. The MBOutcomes are defined in the application configuration which is typically the config.xml file. Alternatively config.xml may reference a file using the <Include ...> directive in which case outcomes.xmlx is an often used convention. 
 */
public class MBApplicationController extends Application implements MBOutcomeListenerProtocol
{
  private MBApplicationFactory           _applicationFactory;
  private MBViewManager                  _viewManager;
  private boolean                        _suppressPageSelection;
  private boolean                        _backStackEnabled;
  private MBOutcomeHandler               _outcomeHandler;
  private boolean                        _shuttingDown            = false;

  private static MBApplicationController _instance                = null;

  private ApplicationState               _currentApplicationState = ApplicationState.NOTSTARTED;

  public static enum ApplicationState {
    NOTSTARTED, STARTING, STARTED
  }

  ///////////////////// Android lifecycle methods
  @Override
  public void onCreate()
  {
    _currentApplicationState = ApplicationState.STARTING;

    Context context = getBaseContext();
    DataUtil.getInstance().setContext(context);
    DeviceUtil.getInstance().setContext(context);
    super.onCreate();
    _instance = this;

    //    Looper.getMainLooper().setMessageLogging(new LogPrinter(Log.VERBOSE, "uithread"));
  }

  ////////////////////////////////////////////////

  // override startController() to customize the startup sequence
  public void startController()
  {
    MBMetadataService.setConfigName("config.xml");
    startApplication(MBApplicationFactory.getInstance());
  }

  ////////////////////////////////////////////////

  public MBViewManager getViewManager()
  {
    return _viewManager;
  }

  public static MBApplicationController getInstance()
  {
    return _instance;
  }

  public MBApplicationController currentInstance()
  {
    return MBApplicationController.getInstance();
  }

  public void startApplication(MBApplicationFactory applicationFactory)
  {
    Log.d(Constants.APPLICATION_NAME, "MBApplicationController.startApplication");
    Log.d(Constants.APPLICATION_NAME, "Device info:\n");
    Log.d(Constants.APPLICATION_NAME, DeviceUtil.getInstance().toString());

    startOutcomeHandler();

    _applicationFactory = applicationFactory;

    // FIXME: there must be a better way of getting the root Activity
    _viewManager = MBViewManager.getInstance();

    _viewManager.prepareForApplicationStart();
    ImageUtil.loadImageCache(getBaseContext().getCacheDir());

    fireInitialOutcomes();
  }

  public void fireInitialOutcomes()
  {
    _outcomeHandler.registerOutcomeListener(this);
    MBOutcome initialOutcome = new MBOutcome();
    initialOutcome.setOrigin(new MBOutcome.Origin().withAction("Controller"));
    initialOutcome.setOutcomeName("init");

    _suppressPageSelection = true;
    _backStackEnabled = false;
    handleOutcome(initialOutcome);
  }

  @Override
  public void outcomeProduced(MBOutcome outcome)
  {
  }

  @Override
  public void afterOutcomeHandled(MBOutcome outcome)
  {
    if (outcome.getOrigin().matches("Controller") && outcome.getOutcomeName().equals("init"))

    _outcomeHandler.sendEmptyMessage(Constants.C_MESSAGE_INITIAL_OUTCOMES_FINISHED);

  }

  /**
   * Visibility should be as strict as possible (as always). Because this method is called
   * from the MBOutcomeHandler the visibility is set to package (controller).
   */
  void finishedInitialOutcomes()
  {
    _suppressPageSelection = false;
    _backStackEnabled = true;

    final EnumSet<MBActionBarInvalidationOption> actionBarRefreshOptions = EnumSet.noneOf(MBActionBarInvalidationOption.class);

    _viewManager.runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        MBViewManager.getInstance().invalidateActionBar(actionBarRefreshOptions);
        MBViewManager.getInstance().invalidateOptionsMenu(false, false);
        MBViewManager.getInstance().getDialogManager().activateHome();

        /*
        if (!homeDialogDefinition.isShowAsTab() || DeviceUtil.getInstance().isPhone()) {
          activateDialogWithName(homeDialogDefinition.getName());
        }*/

        MessageQueue myQueue = Looper.myQueue();
        myQueue.addIdleHandler(new IdleHandler()
        {

          @Override
          public boolean queueIdle()
          {
            _currentApplicationState = ApplicationState.STARTED;
            onApplicationStarted();
            return false;
          }
        });
      }
    });

  }

  protected void onApplicationStarted()
  {
    // Let's see if we want to fire an outcome after the initial ones but before we let the application know it's finished starting
    final String outcomeName = _viewManager.getIntent().getStringExtra(Constants.C_INTENT_POST_INITIALOUTCOMES_OUTCOMENAME);
    if (outcomeName != null)
    {
      _viewManager.runOnUiThread(new Runnable()
      {

        @Override
        public void run()
        {
          handleOutcome(new MBOutcome(outcomeName, null));
        }
      });
    }
  }

  private String getActivePageStack()
  {
    String result = null;
    if (_viewManager != null && _viewManager.getActiveDialog() != null)
    {
      result = _viewManager.getActiveDialog().getDefaultPageStack();
    }
    return result;
  }

  public void handleOutcome(MBOutcome outcome)
  {
    handleOutcome(outcome, true);
  }

  public void handleOutcomeSynchronously(MBOutcome outcome)
  {
    _outcomeHandler.handleOutcomeSynchronously(outcome, true);
  }

  /**
   * @param outcome
   * @param throwException true if Mobbl needs to handle exceptions that may occur, false otherwise (e.g. when the outcome already is an exception)
   */
  private void handleOutcome(MBOutcome outcome, boolean throwException)
  {
    Bundle data = new Bundle();

    data.putParcelable("outcome", outcome);
    data.putBoolean("throwException", throwException);

    // This happens when handleOutcome is triggered before MBViewManager.onRestart is
    if (_outcomeHandler == null)
    {
      startOutcomeHandler();
    }

    final Message msg = _outcomeHandler.obtainMessage();
    msg.setData(data);

    _outcomeHandler.sendMessage(msg);
  }

  ////////////// PAGE HANDLING

  public static class PageBuildResult
  {
    public final MBOutcome outcome;
    public final MBPage    page;
    public final boolean   backstackEnabled;

    public PageBuildResult(MBOutcome outcome, MBPage page, boolean backstackEnabled)
    {
      this.outcome = outcome;
      this.page = page;
      this.backstackEnabled = backstackEnabled;

    }
  }

  public PageBuildResult preparePage(MBOutcome causingOutcome, String pageName, boolean backStackEnabled)
  {
    try
    {

      // construct the page
      MBPageDefinition pageDefinition = MBMetadataService.getInstance().getDefinitionForPageName(pageName);

      MBDocument document = prepareDocument(causingOutcome, pageDefinition.getDocumentName());

      final String displayMode = causingOutcome.getDisplayMode();

      final MBPage page = _applicationFactory.getPageConstructor().createPage(pageDefinition, document, causingOutcome.getPath());
      page.setController(this);
      page.setPageStackName(causingOutcome.getPageStackName());
      // Fallback on the lastly selected dialog if there is no dialog set in the outcome:
      if (page.getPageStackName() == null)
      {
        page.setPageStackName(getActivePageStack());
      }

      PageBuildResult result = new PageBuildResult(causingOutcome, page, backStackEnabled);
      return result;
    }
    catch (Exception e)
    {
      handleException(e, causingOutcome);
      return null;
    }
  }

  public void showResultingPage(final PageBuildResult result)
  {
    try
    {

      _viewManager.showPage(result.page, result.outcome.getDisplayMode(), result.backstackEnabled);
    }
    catch (Exception e)
    {
      handleException(e, result.outcome);
    }
  }

  ////////END OF PAGE HANDLING

  private MBDocument prepareDocument(MBOutcome causingOutcome, String documentName) throws MBInvalidOutcomeException, MBNoDocumentException
  {

    // Load the document from the store
    MBDocument document = null;

    if (causingOutcome.getTransferDocument())
    {
      if (causingOutcome.getDocument() == null)
      {
        String msg = "No document provided (null) in outcome by action/page/alert=" + causingOutcome.getOrigin()
                     + " but transferDocument='TRUE' in outcome definition";
        throw new MBInvalidOutcomeException(msg);
      }
      String actualType = causingOutcome.getDocument().getDefinition().getName();
      if (!actualType.equals(documentName))
      {
        String msg = "Document provided via outcome by action/page/alert=" + causingOutcome.getOrigin()
                     + " (transferDocument='TRUE') is of type " + actualType + " but must be of type " + documentName;
        throw new MBInvalidOutcomeException(msg);
      }
      document = causingOutcome.getDocument();
    }
    else
    {
      document = MBDataManagerService.getInstance().loadDocument(documentName);

      if (document == null)
      {
        document = MBDataManagerService.getInstance().loadDocument(documentName);
        String msg = "Document with name " + documentName + " not found (check filesystem/webservice)";
        throw new MBNoDocumentException(msg);
      }
    }

    return document;
  }

  ////////ALERT (AlertDialog) HANDLING

  public Object[] prepareAlert(MBOutcome causingOutcome, String alertName, Boolean backStackEnabled)
  {
    Object[] result = null;
    try
    {

      // construct the alert
      MBAlertDefinition alertDefinition = MBMetadataService.getInstance().getDefinitionForAlertName(alertName);

      MBDocument document = prepareDocument(causingOutcome, alertDefinition.getDocumentName());

      // Alerts need no background processing
      showResultingAlert(causingOutcome, alertDefinition, document, backStackEnabled);
    }
    catch (Exception e)
    {
      handleException(e, causingOutcome);
    }
    return result;
  }

  public void showResultingAlert(MBOutcome causingOutcome, MBAlertDefinition alertDefinition, MBDocument document,
                                 final boolean backStackEnabled)
  {

    try
    {
      final MBAlert alert = _applicationFactory.createAlert(alertDefinition, document, causingOutcome.getPath());

      Handler mainHandler = new Handler(MBViewManager.getInstance().getMainLooper());
      Runnable myRunnable = new Runnable()
      {
        @Override
        public void run()
        {
          _viewManager.showAlert(alert, backStackEnabled);
        }

      };
      mainHandler.post(myRunnable);

    }
    catch (Exception e)
    {
      handleException(e, causingOutcome);
    }

  }

  ////////END OF ALERT (AlertDialog) HANDLING

  ////////ACTION HANDLING

  public MBOutcome performAction(MBOutcome causingOutcome, MBActionDefinition actionDef)
  {
    AssertUtil.notNull("causingOutcome", causingOutcome);
    AssertUtil.notNull("actionDef", actionDef);
    try
    {

      MBAction action = _applicationFactory.createAction(actionDef.getClassName());
      if (action == null)
      {
        throw new MBException("No action found for " + actionDef.getClassName());
      }

      MBOutcome actionOutcome = action.execute(causingOutcome.getDocument(), causingOutcome.getPath());

      if (actionOutcome == null)
      {
        Log.d(Constants.APPLICATION_NAME, "MBApplicationController.performActionInBackground: " + "No outcome produced by action "
                                          + actionDef.getName() + " (outcome == null); no further procesing.");
      }
      else
      {
        if (Constants.C_DISPLAY_MODE_BACKGROUND.equals(causingOutcome.getDisplayMode()))
        {
          actionOutcome.setDisplayMode(Constants.C_DISPLAY_MODE_BACKGROUND);
        }
        else if (Constants.C_DISPLAY_MODE_BACKGROUNDPIPELINEREPLACE.equals(causingOutcome.getDisplayMode()))
        {
          actionOutcome.setDisplayMode(Constants.C_DISPLAY_MODE_BACKGROUNDPIPELINEREPLACE);
        }
        actionOutcome.setPageStackName(ComparisonUtil.coalesce(actionOutcome.getPageStackName(), causingOutcome.getPageStackName()));
        MBOutcome.Origin origin = new MBOutcome.Origin();
        origin.withAction(actionDef.getName());
        origin.withPageStack(actionOutcome.getPageStackName());
        origin.withOutcome(causingOutcome.getOutcomeName());
        origin.withDialog(causingOutcome.getOrigin().getDialog());
        actionOutcome.setOrigin(origin);
        return actionOutcome;
      }
    }
    catch (Exception e)
    {
      handleException(e, causingOutcome);
    }
    return null;

  }

  ////////END OF ACTION HANDLING

  ////////EVENT HANDLING
  public void addEventToAllListeners(MBEvent event)
  {
    for (MBBasicViewController vc : getViewManager().getAllFragments())
    {
      vc.addEventToQueue(event);
    }
  }

  public void addEventToPage(MBEvent event, String pageName)
  {
    for (MBBasicViewController vc : getViewManager().getAllFragments())
    {
      if (vc.getPage().getName().equals(pageName)) vc.addEventToQueue(event);
    }
  }

  public void addEventToPages(MBEvent event, String[] pageNames)
  {
    List<String> pages = Arrays.asList(pageNames);
    for (MBBasicViewController vc : getViewManager().getAllFragments())
    {
      if (pages.contains(vc.getPage().getName())) vc.addEventToQueue(event);
    }

  }

  ////////END OF EVENT HANDLING

  ////////WINDOW CHANGED HANDLING
  public void changedWindow(MBBasicViewController controller, WindowChangeType eventType)
  {
    if (eventType == WindowChangeType.ACTIVATE)
    {
      controller.handleOnWindowActivated();
    }
    else if (eventType == WindowChangeType.LEAVING)
    {
      controller.handleOnLeavingWindow();
    }
  }

  ////////END OF WINDOW CHANGED HANDLING

  public void handleException(Exception exception, MBOutcome outcome)
  {
    if (_outcomeHandler == null)
    {
      Log.w(Constants.APPLICATION_NAME, "Skipping handleException because outcomeHandler is null");
      return;
    }

    Log.w(Constants.APPLICATION_NAME, "________EXCEPTION RAISED______________________________________________________________");
    Log.w(Constants.APPLICATION_NAME, exception);
    Log.w(Constants.APPLICATION_NAME, "______________________________________________________________________________________");

    MBDocument exceptionDocument = MBDataManagerService.getInstance().loadDocument(MBConfigurationDefinition.DOC_SYSTEM_EXCEPTION);
    String name = MBLocalizationService.getInstance().getTextForKey("General error");
    String description = MBLocalizationService.getInstance().getTextForKey(exception.getMessage());
    Throwable local = exception;
    boolean isMB = false;

    // check for an MBException for nicer messages in the app
    do
    {
      if (local instanceof MBException)
      {
        isMB = true;
        String titleName;
        if ((titleName = MBLocalizationService.getInstance().getTextForKey(((MBException) local).getName())) != null
            && titleName.length() > 0)
        {
          name = titleName;
        }
        description = MBLocalizationService.getInstance().getTextForKey(local.getMessage());
      }
    }
    while (!isMB && ((local = local.getCause()) != null));

    exceptionDocument.setValue(name, MBConfigurationDefinition.PATH_SYSTEM_EXCEPTION_NAME);
    exceptionDocument.setValue(description, MBConfigurationDefinition.PATH_SYSTEM_EXCEPTION_DESCRIPTION);
    String origin = outcome.getOrigin() != null ? outcome.getOrigin().toString() : null;
    exceptionDocument.setValue(origin, MBConfigurationDefinition.PATH_SYSTEM_EXCEPTION_ORIGIN);
    exceptionDocument.setValue(outcome.getOutcomeName(), MBConfigurationDefinition.PATH_SYSTEM_EXCEPTION_OUTCOME);
    for (StackTraceElement traceElement : exception.getStackTrace())
    {
      MBElement stackline = exceptionDocument.createElement("/Exception[0]/Stackline");
      String line = traceElement.toString();
      if (line.length() > 52) line = line.substring(0, 52);
      stackline.setAttributeValue(line, "line");
    }

    MBDataManagerService.getInstance().storeDocument(exceptionDocument);

    MBMetadataService metadataService = MBMetadataService.getInstance();

    // See if there is an outcome defined for this particular exception
    List<MBOutcomeDefinition> outcomeDefinitions = metadataService.getOutcomeDefinitionsForOrigin(outcome.getOrigin(), exception.getClass()
        .getSimpleName(), false);
    if (outcomeDefinitions.size() != 0)
    {
      MBOutcome specificExceptionHandler = new MBOutcome(outcome);
      specificExceptionHandler.setOutcomeName(exception.getClass().getSimpleName());
      specificExceptionHandler.setDocument(exceptionDocument);
      handleOutcome(specificExceptionHandler);
    }
    else
    {
      // There is no specific exception handler defined. So fall back on the generic one
      outcomeDefinitions = metadataService.getOutcomeDefinitionsForOrigin(outcome.getOrigin(), "exception", false);
      if (outcomeDefinitions.isEmpty())
      {
        Log.w(Constants.APPLICATION_NAME, "No outcome with origin=" + outcome
                                          + " name=exception defined to handle errors; so re-throwing exception");
        throw new RuntimeException(exception);
      }
      if ("exception".equals(outcome.getOutcomeName()))
      {
        Log.w(Constants.APPLICATION_NAME,
              "Error in handling an outcome with name=exception (i.e. the error handling in the controller is probably misconfigured) Re-throwing exception");
        throw new RuntimeException(exception);
      }

      MBOutcome genericExceptionHandler = new MBOutcome("exception", exceptionDocument);
      genericExceptionHandler.setPageStackName(outcome.getPageStackName());
      genericExceptionHandler.setPath(outcome.getPath());

      _outcomeHandler.handleOutcomeSynchronously(genericExceptionHandler, false);
    }
  }

  public void resetController()
  {
    _viewManager.resetView();
    fireInitialOutcomes();
  }

  public void resetControllerPreservingCurrentDialog()
  {
    _viewManager.resetViewPreservingCurrentDialog();
  }

  public void startOutcomeHandler()
  {
    if (_outcomeHandler != null)
    {
      Log.w(Constants.APPLICATION_NAME, "Outcome handler already started, so skipping");
      return;
    }

    MBOutcomeHandlerThread outcomeHandlerThread = new MBOutcomeHandlerThread("outcomeHandler");
    outcomeHandlerThread.start();

    while ((_outcomeHandler = outcomeHandlerThread.getOutcomeHandler()) == null)
    {
      Log.d(Constants.APPLICATION_NAME, "Waiting for OutcomeHandler to settle down...");
    }
    Log.d(Constants.APPLICATION_NAME, "OutcomeHandler settled, continue startup");
  }

  public void stopOutcomeHandler()
  {
    if (_outcomeHandler != null)
    {
      _outcomeHandler.getLooper().quit();
      _outcomeHandler = null;

      if (ApplicationState.STARTED != _currentApplicationState)
      {
        _viewManager.finish();
      }
    }
  }

  public void handleSearchRequest(Intent searchIntent)
  {
    final String query;
    final boolean isProgressive;

    query = searchIntent.getStringExtra(SearchManager.QUERY);

    isProgressive = !Intent.ACTION_SEARCH.equals(searchIntent.getAction());

    String searchPath = "";

    MBDocument searchConfigDoc = MBDataManagerService.getInstance().loadDocument(Constants.C_DOC_SEARCH_CONFIGURATION);
    String normalSearchOutcome = searchConfigDoc.getValueForPath(Constants.C_EL_SEARCH_CONFIGURATION + "/"
                                                                 + Constants.C_EL_SEARCH_CONFIGURATION_ATTR_NORMAL_SEARCH_OUTCOME);
    String progressiveSearchOutcome = searchConfigDoc
        .getValueForPath(Constants.C_EL_SEARCH_CONFIGURATION + "/" + Constants.C_EL_SEARCH_CONFIGURATION_ATTR_PROGRESSIVE_SEARCH_OUTCOME);
    searchPath = searchConfigDoc.getValueForPath(Constants.C_EL_SEARCH_CONFIGURATION + "/"
                                                 + Constants.C_EL_SEARCH_CONFIGURATION_ATTR_SEARCH_PATH);

    String path = Uri.decode(searchIntent.getDataString());

    MBDocument searchRequest = MBDataManagerService.getInstance().loadDocument(Constants.C_DOC_SEARCH_REQUEST);
    searchRequest.setValue(query, Constants.C_EL_SEARCH_REQUEST + "/" + Constants.C_EL_SEARCH_REQUEST_ATTR_QUERY);
    searchRequest.setValue(isProgressive, Constants.C_EL_SEARCH_REQUEST + "/" + Constants.C_EL_SEARCH_REQUEST_ATTR_IS_PROGRESSIVE);
    searchRequest.setValue(normalSearchOutcome, Constants.C_EL_SEARCH_REQUEST + "/"
                                                + Constants.C_EL_SEARCH_REQUEST_ATTR_NORMAL_SEARCH_OUTCOME);
    searchRequest.setValue(progressiveSearchOutcome, Constants.C_EL_SEARCH_REQUEST + "/"
                                                     + Constants.C_EL_SEARCH_REQUEST_ATTR_PROGRESSIVE_SEARCH_OUTCOME);

    MBOutcome searchOutcome = new MBOutcome();
    searchOutcome.setOrigin(new MBOutcome.Origin().withDialog(Constants.C_MOBBL_ORIGIN_NAME_CONTROLLER));
    searchOutcome.setOutcomeName(Constants.C_MOBBL_ORIGIN_CONTROLLER_NAME_SEARCH);
    searchOutcome.setDocument(searchRequest);
    searchOutcome.setPath((path != null) ? path + searchPath : null);

    handleOutcome(searchOutcome);

  }

  public boolean getBackStackEnabled()
  {
    return _backStackEnabled;
  }

  public MBOutcomeHandler getOutcomeHandler()
  {
    return _outcomeHandler;
  }

  public ApplicationState getApplicationState()
  {
    return _currentApplicationState;
  }

  public void setApplicationState(ApplicationState state)
  {
    _currentApplicationState = state;
  }

  public boolean isSuppressPageSelection()
  {
    return _suppressPageSelection;
  }

  public void setShuttingDown(boolean shuttingDown)
  {
    _shuttingDown = shuttingDown;
  }

  public boolean isShuttingDown()
  {
    return _shuttingDown;
  }
}
