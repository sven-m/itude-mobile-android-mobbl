package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

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
import com.itude.mobile.android.util.CollectionUtilities;
import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.android.util.DeviceUtil;
import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBAlertDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBActionBarInvalidationOption;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBInvalidOutcomeException;
import com.itude.mobile.mobbl2.client.core.controller.util.MBBasicViewController;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.MBEvent;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBWindowChangeType.WindowChangeType;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBNoDocumentException;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.imagecache.ImageUtil;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;
import com.itude.mobile.mobbl2.client.core.view.MBAlert;
import com.itude.mobile.mobbl2.client.core.view.MBPage;

public class MBApplicationController extends Application
{
  private MBApplicationFactory                 _applicationFactory;
  private MBViewManager                        _viewManager;
  private boolean                              _suppressPageSelection;
  private boolean                              _backStackEnabled;
  private MBOutcome                            _outcomeWhichCausedModal;
  private Map<String, MBPage>                  _pages;
  private Map<String, HashMap<String, MBPage>> _pagesForName;
  private Stack<String>                        _modalPageStack;
  private MBOutcomeHandler                     _outcomeHandler;

  private static MBApplicationController       _instance                = null;

  private ApplicationState                     _currentApplicationState = ApplicationState.NOTSTARTED;

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
    _pages = new HashMap<String, MBPage>();
    _pagesForName = new HashMap<String, HashMap<String, MBPage>>();
    _modalPageStack = new Stack<String>();

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
    MBOutcome initialOutcome = new MBOutcome();
    initialOutcome.setOriginName("Controller");
    initialOutcome.setOutcomeName("init");
    initialOutcome.setDialogName(getActiveDialogName());
    initialOutcome.setNoBackgroundProcessing(true);

    _suppressPageSelection = true;
    _backStackEnabled = false;
    handleOutcomeSynchronously(initialOutcome);

    _outcomeHandler.sendEmptyMessage(Constants.C_MESSAGE_INITIAL_OUTCOMES_FINISHED);
  }

  /*
   * Visibility should be as strict as possible (as always). Because this method is called
   * from the MBOutcomeHandler the visibility is set to package (controller).
   */
  void finishedInitialOutcomes()
  {
    _suppressPageSelection = false;
    _backStackEnabled = true;

    final MBDialogDefinition homeDialogDefinition = MBMetadataService.getInstance().getHomeDialogDefinition();

    final EnumSet<MBActionBarInvalidationOption> actionBarRefreshOptions = EnumSet.noneOf(MBActionBarInvalidationOption.class);

    if (homeDialogDefinition.isShowAsTab())
    {
      actionBarRefreshOptions.add(MBActionBarInvalidationOption.SHOW_FIRST);
    }

    MBViewManager.getInstance().buildSlidingMenu();

    _viewManager.runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        MBViewManager.getInstance().invalidateActionBar(actionBarRefreshOptions);

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

  public String getActiveDialogName()
  {
    String result = null;
    if (_viewManager != null)
    {
      result = _viewManager.getActiveDialogName();
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

    final Message msg = _outcomeHandler.obtainMessage();
    msg.setData(data);

    _outcomeHandler.sendMessage(msg);
  }

  ////////////// PAGE HANDLING

  public Object[] preparePage(MBOutcome causingOutcome, String pageName, Boolean backStackEnabled)
  {
    Object[] result = null;
    try
    {

      // construct the page
      MBPageDefinition pageDefinition = MBMetadataService.getInstance().getDefinitionForPageName(pageName);

      MBDocument document = prepareDocument(causingOutcome, pageDefinition.getDocumentName());
      if (causingOutcome.getNoBackgroundProcessing())
      {
        showResultingPage(causingOutcome, pageDefinition, document, backStackEnabled);
      }
      else
      {
        // calling AsyncTask calls showResultingPage in UI thread.
        Object[] backgroundResult = {causingOutcome, pageDefinition, document, backStackEnabled};
        result = backgroundResult;
      }
    }
    catch (Exception e)
    {
      handleException(e, causingOutcome);
    }
    return result;
  }

  public void showResultingPage(final MBOutcome causingOutcome, MBPageDefinition pageDefinition, MBDocument document,
                                final boolean backStackEnabled)
  {
    try
    {
      final String displayMode = causingOutcome.getDisplayMode();

      final MBPage page = _applicationFactory.getPageConstructor().createPage(pageDefinition, document, causingOutcome.getPath(),
                                                                              MBViewState.MBViewStatePlain);
      page.setController(this);
      page.setDialogName(causingOutcome.getDialogName());
      // Fallback on the lastly selected dialog if there is no dialog set in the outcome:
      if (page.getDialogName() == null)
      {
        page.setDialogName(getActiveDialogName());
      }
      _viewManager.runOnUiThread(new MBThread(page)
      {

        @Override
        public void runMethod()
        {
          if (causingOutcome.getDialogName() != null && !"BACKGROUND".equals(causingOutcome.getDisplayMode())) _viewManager
              .activateDialogWithName(causingOutcome.getDialogName());
          _viewManager.showPage(page, displayMode, backStackEnabled);
        }
      });
    }
    catch (Exception e)
    {
      handleException(e, causingOutcome);
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
        String msg = "No document provided (null) in outcome by action/page/alert=" + causingOutcome.getOriginName()
                     + " but transferDocument='TRUE' in outcome definition";
        throw new MBInvalidOutcomeException(msg);
      }
      String actualType = causingOutcome.getDocument().getDefinition().getName();
      if (!actualType.equals(documentName))
      {
        String msg = "Document provided via outcome by action/page/alert=" + causingOutcome.getOriginName()
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

  public void performAction(MBOutcome causingOutcome, MBActionDefinition actionDef)
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
        // TODO difference between nonbackground or background processing should be implemented
        if (causingOutcome.getNoBackgroundProcessing())
        {
          handleActionResult(causingOutcome, actionDef, actionOutcome);
        }
        else
        {
          handleActionResult(causingOutcome, actionDef, actionOutcome);
        }
      }
    }
    catch (Exception e)
    {
      handleException(e, causingOutcome);
    }

  }

  private void handleActionResult(MBOutcome causingOutcome, MBActionDefinition actionDef, MBOutcome actionOutcome)
  {
    try
    {
      if (actionOutcome.getDialogName() == null) actionOutcome.setDialogName(causingOutcome.getDialogName());
      actionOutcome.setOriginName(actionDef.getName());

      _outcomeHandler.handleOutcomeSynchronously(actionOutcome, false);
    }
    catch (Exception e)
    {
      handleException(e, causingOutcome);
    }
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
    Collection<MBPage> pages = getPagesWithName(pageName);

    if (pages != null)
    {
      Iterator<MBPage> pagesIterator = pages.iterator();

      if (pages != null)
      {
        while (pagesIterator.hasNext())
        {
          pagesIterator.next().getViewController().addEventToQueue(event);
        }
      }
    }
  }

  public void addEventToPages(MBEvent event, String[] pageNames)
  {
    for (String pageName : pageNames)
    {
      Collection<MBPage> pages = getPagesWithName(pageName);

      if (pages != null)
      {
        Iterator<MBPage> pagesIterator = pages.iterator();

        if (pages != null)
        {
          while (pagesIterator.hasNext())
          {
            MBBasicViewController controller = pagesIterator.next().getViewController();
            if (controller != null)
            {
              controller.addEventToQueue(event);
            }
          }
        }
      }
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

  public synchronized MBOutcome getOutcomeWhichCausedModal()
  {
    return _outcomeWhichCausedModal;
  }

  public synchronized void setOutcomeWhichCausedModal(MBOutcome outcomeWhichCausedModal)
  {
    _outcomeWhichCausedModal = outcomeWhichCausedModal;
  }

  public void handleException(Exception exception, MBOutcome outcome)
  {
    if (_outcomeHandler == null)
    {
      // https://dev.itude.com/jira/browse/BINCKAPPS-831
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
    exceptionDocument.setValue(outcome.getOriginName(), MBConfigurationDefinition.PATH_SYSTEM_EXCEPTION_ORIGIN);
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
    ArrayList<MBOutcomeDefinition> outcomeDefinitions = (ArrayList<MBOutcomeDefinition>) metadataService
        .getOutcomeDefinitionsForOrigin(outcome.getOriginName(), exception.getClass().getSimpleName(), false);
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
      outcomeDefinitions = (ArrayList<MBOutcomeDefinition>) metadataService.getOutcomeDefinitionsForOrigin(outcome.getOriginName(),
                                                                                                           "exception", false);
      if (outcomeDefinitions.size() == 0)
      {
        Log.w(Constants.APPLICATION_NAME, "No outcome with origin=" + outcome.getOriginName()
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
      genericExceptionHandler.setDialogName(outcome.getDialogName());
      genericExceptionHandler.setPath(outcome.getPath());

      _outcomeHandler.handleOutcomeSynchronously(genericExceptionHandler, false);
    }
  }

  public String activeDialogName()
  {
    String result = null;
    if (_viewManager != null)
    {
      result = _viewManager.getActiveDialogName();
    }
    return result;
  }

  public void activateDialogWithName(String name)
  {
    _viewManager.activateDialogWithName(name);
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

    //    Bundle bundle = searchIntent.getBundleExtra(SearchManager.APP_DATA);
    //    if (bundle != null)
    //    {
    //      _searchResultNormal = bundle.getString(Constants.C_BUNDLE_NORMAL_SEARCH_OUTCOME);
    //      _searchResultProgressive = bundle.getString(Constants.C_BUNDLE_PROGRESSIVE_SEARCH_OUTCOME);
    //      searchPath = bundle.getString(Constants.C_BUNDLE_SEARCH_PATH);
    //    }

    MBDocument searchConfigDoc = MBDataManagerService.getInstance().loadDocument(Constants.C_DOC_SEARCH_CONFIGURATION);
    String searchPage = searchConfigDoc.getValueForPath(Constants.C_EL_SEARCH_CONFIGURATION + "/"
                                                        + Constants.C_EL_SEARCH_CONFIGURATION_ATTR_SEARCH_PAGE);
    String searchAction = searchConfigDoc.getValueForPath(Constants.C_EL_SEARCH_CONFIGURATION + "/"
                                                          + Constants.C_EL_SEARCH_CONFIGURATION_ATTR_SEARCH_ACTION);
    String normalSearchOutcome = searchConfigDoc.getValueForPath(Constants.C_EL_SEARCH_CONFIGURATION + "/"
                                                                 + Constants.C_EL_SEARCH_CONFIGURATION_ATTR_NORMAL_SEARCH_OUTCOME);
    String progressiveSearchOutcome = searchConfigDoc
        .getValueForPath(Constants.C_EL_SEARCH_CONFIGURATION + "/" + Constants.C_EL_SEARCH_CONFIGURATION_ATTR_PROGRESSIVE_SEARCH_OUTCOME);
    searchPath = searchConfigDoc.getValueForPath(Constants.C_EL_SEARCH_CONFIGURATION + "/"
                                                 + Constants.C_EL_SEARCH_CONFIGURATION_ATTR_SEARCH_PATH);

    MBPageDefinition pageDefinition = MBMetadataService.getInstance().getDefinitionForPageName(searchPage);
    MBDocument document = new MBDocument(MBMetadataService.getInstance().getDefinitionForDocumentName(pageDefinition.getDocumentName()));

    MBPage page = MBApplicationFactory.getInstance().createPage(pageDefinition, document, null, MBViewState.MBViewStatePlain);
    page.setController(MBApplicationController.getInstance());
    MBApplicationController.getInstance().setPage(searchAction + searchPage, page);

    String path = Uri.decode(searchIntent.getDataString());

    MBDocument searchRequest = MBDataManagerService.getInstance().loadDocument(Constants.C_DOC_SEARCH_REQUEST);
    searchRequest.setValue(query, Constants.C_EL_SEARCH_REQUEST + "/" + Constants.C_EL_SEARCH_REQUEST_ATTR_QUERY);
    searchRequest.setValue(isProgressive, Constants.C_EL_SEARCH_REQUEST + "/" + Constants.C_EL_SEARCH_REQUEST_ATTR_IS_PROGRESSIVE);
    searchRequest.setValue(normalSearchOutcome, Constants.C_EL_SEARCH_REQUEST + "/"
                                                + Constants.C_EL_SEARCH_REQUEST_ATTR_NORMAL_SEARCH_OUTCOME);
    searchRequest.setValue(progressiveSearchOutcome, Constants.C_EL_SEARCH_REQUEST + "/"
                                                     + Constants.C_EL_SEARCH_REQUEST_ATTR_PROGRESSIVE_SEARCH_OUTCOME);

    MBOutcome searchOutcome = new MBOutcome();
    searchOutcome.setOriginName(Constants.C_MOBBL_ORIGIN_NAME_CONTROLLER);
    searchOutcome.setOutcomeName(Constants.C_MOBBL_ORIGIN_CONTROLLER_NAME_SEARCH);
    searchOutcome.setDocument(searchRequest);
    searchOutcome.setPath((path != null) ? path + searchPath : null);

    handleOutcome(searchOutcome);

  }

  /////////////////////////////////////////////////////////////////////////
  // Android cannot pass object between activities without serializing them.
  // This is a workaround for passing Pages between the controller and the DialogControllers

  public MBPage getPage(String id)
  {
    return _pages.get(id);
  }

  public Collection<MBPage> getPagesWithName(String pageName)
  {
    if (_pagesForName.containsKey(pageName))
    {
      return _pagesForName.get(pageName).values();
    }

    return null;
  }

  public void setPage(String id, MBPage page)
  {
    if (page.getCurrentViewState().equals(MBViewState.MBViewStateModal))
    {
      _modalPageStack.add(id);
    }
    _pages.put(id, page);

    if (!_pagesForName.containsKey(page.getName()))
    {
      _pagesForName.put(page.getName(), new HashMap<String, MBPage>());
    }

    _pagesForName.get(page.getName()).put(id, page);
  }

  public String getModalPageID()
  {
    return CollectionUtilities.isNotEmpty(_modalPageStack) ? _modalPageStack.peek() : null;
  }

  public void removeLastModalPageID()
  {
    _modalPageStack.pop();
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
}
