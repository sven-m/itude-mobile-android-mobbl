package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBViewManager.MBViewState;
import com.itude.mobile.mobbl2.client.core.controller.background.MBPerformActionInBackgroundRunner;
import com.itude.mobile.mobbl2.client.core.controller.background.MBPreparePageInBackgroundRunner;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBInvalidOutcomeException;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBNoOutcomesDefinedException;
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
import com.itude.mobile.mobbl2.client.core.util.DataUtil;
import com.itude.mobile.mobbl2.client.core.util.DeviceUtil;
import com.itude.mobile.mobbl2.client.core.util.MBDevice;
import com.itude.mobile.mobbl2.client.core.view.MBPage;

public class MBApplicationController extends Application
{
  private MBApplicationFactory                 _applicationFactory;
  private MBViewManager                        _viewManager;
  private boolean                              _suppressPageSelection;
  private MBOutcome                            _outcomeWhichCausedModal;
  private Map<String, MBPage>                  _pages;
  private Map<String, HashMap<String, MBPage>> _pagesForName;
  private String                               _modalPageID;

  private static MBApplicationController       _instance = null;

  ///////////////////// Android lifecycle methods
  @Override
  public void onCreate()
  {
    Context context = getBaseContext();
    DataUtil.getInstance().setContext(context);
    DeviceUtil.getInstance().setContext(context);
    MBDevice.getInstance();
    super.onCreate();
    _instance = this;
    _pages = new HashMap<String, MBPage>();
    _pagesForName = new HashMap<String, HashMap<String, MBPage>>();
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
    Log.d("MOBBL", "MBApplicationController.startApplication");
    Log.d(Constants.APPLICATION_NAME, "Device info:");
    Log.d(Constants.APPLICATION_NAME, MBDevice.getInstance().toString());

    _applicationFactory = applicationFactory;

    // FIXME: there must be a better way of getting the root Activity
    _viewManager = MBViewManager.getInstance();

    _viewManager.setSinglePageMode((MBMetadataService.getInstance().getDialogs().size() <= 1));

    fireInitialOutcomes();
  }

  private void fireInitialOutcomes()
  {
    MBOutcome initialOutcome = new MBOutcome();
    initialOutcome.setOriginName("Controller");
    initialOutcome.setOutcomeName("init");
    initialOutcome.setDialogName(getActiveDialogName());
    initialOutcome.setNoBackgroundProcessing(true);

    _suppressPageSelection = true;
    handleOutcome(initialOutcome);
    _suppressPageSelection = false;
    _viewManager.activateDialogWithName(MBMetadataService.getInstance().getFirstDialogDefinition().getName());
  }

  private String getActiveDialogName()
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
    try
    {
      doHandleOutcome(outcome);
    }
    catch (Exception e)
    {
      handleException(e, outcome);
    }
  }

  private void doHandleOutcome(MBOutcome outcome)
  {
    Log.d("MOBBL", "MBApplicationController.handleOutcome: " + outcome);

    // Make sure that the (external) document cache of the document itself is cleared since this
    // might interfere with the preconditions that are evaluated later on. Also: if the document is transferred
    // the next page / action will also have fresh copies
    if (outcome != null && outcome.getDocument() != null)
    {
      outcome.getDocument().clearAllCaches();
    }

    MBMetadataService metadataService = MBMetadataService.getInstance();

    ArrayList<MBOutcomeDefinition> outcomeDefinitions = (ArrayList<MBOutcomeDefinition>) metadataService
        .getOutcomeDefinitionsForOrigin(outcome.getOriginName(), outcome.getOutcomeName(), false);
    if (outcomeDefinitions.size() == 0)
    {
      String msg = "No outcome defined for origin=" + outcome.getOriginName() + " outcome=" + outcome.getOutcomeName();
      throw new MBNoOutcomesDefinedException(msg);
    }

    boolean shouldPersist = false;
    for (MBOutcomeDefinition outcomeDef : outcomeDefinitions)
    {
      shouldPersist |= outcomeDef.getPersist();
    }

    if (shouldPersist)
    {
      if (outcome.getDocument() == null) Log
          .w("MOBBL",
             "MBApplicationController.doHandleOutcome: origin="
                 + outcome.getOriginName()
                 + "and name="
                 + outcome.getOutcomeName()
                 + " has persistDocument=TRUE but there is no document (probably the outcome originates from an action; which cannot have a document)");
      else MBDataManagerService.getInstance().storeDocument(outcome.getDocument());
    }

    ArrayList<String> dialogs = new ArrayList<String>();
    String selectPageInDialog = "yes";

    // We need to make sure that the order of the dialog tabs conforms to the order of the outcomes
    // This is not necessarily the case because preparing of page A might take longer in the background than page B
    // Because of this, page B migh be places on a tab prior to page A which is undesired. We handle this by
    // notifying the view handler of the dialogs used by the outcome in sequental order. The viewmanager will then
    // use this information to sort the tabs

    for (MBOutcomeDefinition outcomeDef : outcomeDefinitions)
    {

      if ("RESET_CONTROLLER".equals(outcomeDef.getAction()))
      {
        resetController();
      }
      else
      {

        // Create a working copy of the outcome; we manipulate the outcome below and we want the passed outcome to be left unchanged (good practise)
        MBOutcome outcomeToProcess = new MBOutcome(outcomeDef);

        outcomeToProcess.setPath(outcome.getPath());
        outcomeToProcess.setDocument(outcome.getDocument());
        outcomeToProcess.setDialogName(outcome.getDialogName());
        outcomeToProcess.setNoBackgroundProcessing(outcome.getNoBackgroundProcessing() || outcomeDef.getNoBackgroundProcessing());

        if (outcomeToProcess.isPreConditionValid())
        {

          // Update a possible switch of dialog/display mode set by the outcome definition
          if (outcomeDef.getDialog() != null) outcomeToProcess.setDialogName(outcomeDef.getDialog());
          if (outcomeDef.getDisplayMode() != null) outcomeToProcess.setDisplayMode(outcomeDef.getDisplayMode());
          if (outcomeToProcess.getOriginDialogName() == null) outcomeToProcess.setOriginDialogName(outcomeToProcess.getDialogName());

          if (outcomeToProcess.getDialogName() != null) dialogs.add(outcomeToProcess.getDialogName());

          if ("MODAL".equals(outcomeToProcess.getDisplayMode()) || "MODALWITHCLOSEBUTTON".equals(outcomeToProcess.getDisplayMode())
              || "MODALFORMSHEET".equals(outcomeToProcess.getDisplayMode())
              || "MODALFORMSHEETWITHCLOSEBUTTON".equals(outcomeToProcess.getDisplayMode())
              || "MODALPAGESHEET".equals(outcomeToProcess.getDisplayMode())
              || "MODALPAGESHEETWITHCLOSEBUTTON".equals(outcomeToProcess.getDisplayMode())
              || "MODALFULLSCREEN".equals(outcomeToProcess.getDisplayMode())
              || "MODALFULLSCREENWITHCLOSEBUTTON".equals(outcomeToProcess.getDisplayMode())
              || "MODALCURRENTCONTEXT".equals(outcomeToProcess.getDisplayMode())
              || "MODALCURRENTCONTEXTWITHCLOSEBUTTON".equals(outcomeToProcess.getDisplayMode()))
          {
            _outcomeWhichCausedModal = outcomeToProcess;
          }
          else if ("ENDMODAL".equals(outcomeToProcess.getDisplayMode()))
          {
            if (getModalPageID() != null)
            {
              _viewManager.endModalDialog(getModalPageID());
              _viewManager.hideActivityIndicator();
            }
            else
            {
              // On the iPhone the current screen is being refreshed after dismissing an progress indicator
              // On Android this doesn't happen. To recreate this behaviour this code was introduced
              //TODO implement refresh mechanism. On dialog base or page? 
              //              (((MBDialogController) _viewManager.getCurrentActivity()).getCurrentActivity()).handleOnWindowActivated();
            }

          }
          else if ("ENDMODAL_CONTINUE".equals(outcomeToProcess.getDisplayMode()))
          {
            _viewManager.endModalDialog();
            handleOutcome(_outcomeWhichCausedModal);
            //
            _outcomeWhichCausedModal = null;
          }
          else if ("POP".equals(outcomeToProcess.getDisplayMode()))
          {
            _viewManager.popPage(outcomeToProcess.getDialogName());
          }
          else if ("POPALL".equals(outcomeToProcess.getDisplayMode()))
          {
            _viewManager.endDialog(outcomeToProcess.getDialogName(), true);
          }
          else if ("CLEAR".equals(outcomeToProcess.getDisplayMode()))
          {
            _viewManager.resetView();
          }
          else if ("END".equals(outcomeToProcess.getDisplayMode()))
          {
            _viewManager.endDialog(outcomeToProcess.getDialogName(), false);
            dialogs.remove(outcomeToProcess.getDialogName());
          }
          else
          {
            _viewManager.activateDialogWithName(outcomeToProcess.getDialogName());
          }

          MBActionDefinition actionDef = metadataService.getDefinitionForActionName(outcomeDef.getAction(), false);

          if (actionDef != null)
          {
            _viewManager.showActivityIndicator();
            if (outcomeToProcess.getNoBackgroundProcessing())
            {
              performActionInBackground(new MBOutcome(outcomeToProcess), actionDef);
            }
            else
            {
              MBPerformActionInBackgroundRunner runner = new MBPerformActionInBackgroundRunner();

              runner.setController(this);
              runner.setOutcome(new MBOutcome(outcomeToProcess));
              runner.setActionDefinition(actionDef);
              runner.execute(new Object[0]);
            }
          }

          MBPageDefinition pageDef = metadataService.getDefinitionForPageName(outcomeDef.getAction(), false);
          if (pageDef != null)
          {
            /*CH: Exception pages prepare in the background. Android has trouble if the
            activity indicator is shown. Somehow this problem does not occur when preparing 
            a normal page*/
            if (!"exception".equals(outcome.getOutcomeName())) _viewManager.showActivityIndicator();
            if (outcomeToProcess.getNoBackgroundProcessing())
            {
              preparePageInBackground(new MBOutcome(outcomeToProcess), pageDef.getName(), selectPageInDialog);
            }
            else
            {
              MBPreparePageInBackgroundRunner runner = new MBPreparePageInBackgroundRunner();
              runner.setController(this);
              runner.setOutcome(new MBOutcome(outcomeToProcess));
              runner.setPageName(pageDef.getName());
              runner.setSelectPageInDialog(selectPageInDialog);
              runner.execute(new Object[0]);
            }

            selectPageInDialog = "no";
          }
          if (actionDef == null && pageDef == null && !"none".equals(outcomeDef.getAction()))
          {
            StringBuffer tmp = new StringBuffer();
            String msg = "Invalid outcome; no action or page with name " + outcomeDef.getAction() + " defined. See outcome origin="
                         + outcomeDef.getOrigin() + "action=" + outcomeDef.getName() + " Check \n"
                         + outcomeDef.asXmlWithLevel(tmp, 5).toString();
            throw new MBInvalidOutcomeException(msg);
          }
        }
      }
    }
  }

  ////////////// PAGE HANDLING

  public Object[] preparePageInBackground(MBOutcome causingOutcome, String pageName, String selectPageInDialog)
  {
    Object[] result = null;
    try
    {

      // construct the page
      MBPageDefinition pageDefinition = MBMetadataService.getInstance().getDefinitionForPageName(pageName);

      // Load the document from the store
      MBDocument document = null;

      if (causingOutcome.getTransferDocument())
      {
        if (causingOutcome.getDocument() == null)
        {
          String msg = "No document provided (null) in outcome by action/page=" + causingOutcome.getOriginName()
                       + " but transferDocument='TRUE' in outcome definition";
          throw new MBInvalidOutcomeException(msg);
        }
        String actualType = causingOutcome.getDocument().getDefinition().getName();
        if (!actualType.equals(pageDefinition.getDocumentName()))
        {
          String msg = "Document provided via outcome by action/page=" + causingOutcome.getOriginName()
                       + " (transferDocument='TRUE') is of type " + actualType + "but must be of type " + pageDefinition.getDocumentName();
          throw new MBInvalidOutcomeException(msg);
        }
        document = causingOutcome.getDocument();
      }
      else
      {
        document = MBDataManagerService.getInstance().loadDocument(pageDefinition.getDocumentName());

        if (document == null)
        {
          document = MBDataManagerService.getInstance().loadDocument(pageDefinition.getDocumentName());
          String msg = "Document with name " + pageDefinition.getDocumentName() + " not found (check filesystem/webservice)";
          throw new MBNoDocumentException(msg);
        }
      }
      if (causingOutcome.getNoBackgroundProcessing())
      {
        showResultingPage(causingOutcome, pageDefinition, document, selectPageInDialog);
      }
      else
      {
        // calling AsyncTask calls showResultingPage in UI thread.
        Object[] backgroundResult = {causingOutcome, pageDefinition, document, selectPageInDialog};
        result = backgroundResult;
      }
    }
    catch (Exception e)
    {
      handleException(e, causingOutcome);
    }
    return result;
  }

  public void showResultingPage(MBOutcome causingOutcome, MBPageDefinition pageDefinition, MBDocument document, String selectPageInDialog)
  {
    try
    {
      String displayMode = causingOutcome.getDisplayMode();
      MBViewState viewState = _viewManager.getCurrentViewState();

      if ("MODAL".equals(displayMode)) viewState = MBViewState.MBViewStateModal;

      MBPage page = _applicationFactory.createPage(pageDefinition, document, causingOutcome.getPath(), viewState);
      page.setController(this);
      page.setDialogName(causingOutcome.getDialogName());
      // Fallback on the lastly selected dialog if there is no dialog set in the outcome:
      if (page.getDialogName() == null)
      {
        page.setDialogName(getActiveDialogName());
      }
      boolean doSelect = "yes".equals(selectPageInDialog) && !_suppressPageSelection;
      // don't add initial outcomes to the backstack
      boolean addToBackStack = !"init".equals(causingOutcome.getOutcomeName()) && !"Controller".equals(causingOutcome.getOriginName());
      _viewManager.showPage(page, displayMode, doSelect, addToBackStack);
      _viewManager.hideActivityIndicator();
    }
    catch (Exception e)
    {
      handleException(e, causingOutcome);
    }
  }

  ////////END OF PAGE HANDLING

  ////////ACTION HANDLING

  public void performActionInBackground(MBOutcome causingOutcome, MBActionDefinition actionDef)
  {
    try
    {

      MBAction action = _applicationFactory.createAction(actionDef.getClassName());
      if (action == null)
      {
        Log.d("MOBBL", "MBApplicationController.performActionInBackground: " + "No outcome produced by action " + actionDef.getName()
                       + " (outcome == null); no further procesing.");
      }

      MBOutcome actionOutcome = action.execute(causingOutcome.getDocument(), causingOutcome.getPath());

      if (actionOutcome == null)
      {
        _viewManager.hideActivityIndicator();
        Log.d("MOBBL", "MBApplicationController.performActionInBackground: " + "No outcome produced by action " + actionDef.getName()
                       + " (outcome == null); no further procesing.");
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

      handleOutcome(actionOutcome);
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
    /*for (MBBasicViewController vc : getViewManager().getAllViewControllers())
    {
      vc.addEventToQueue(event);
    }*/
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
    Log.w("MOBBL", "________EXCEPTION RAISED______________________________________________________________");
    Log.w("MOBBL", exception);
    Log.w("MOBBL", "______________________________________________________________________________________");

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
        //        name = MBLocalizationService.getInstance().getTextForKey(((MBException) local).getName());
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
      MBElement stackline = exceptionDocument.createElementWithName("/Exception[0]/Stackline");
      String line = traceElement.toString();
      if (line.length() > 52) line = line.substring(0, 52);
      stackline.setAttributeValue(line, "line");
    }

    MBDataManagerService.getInstance().storeDocument(exceptionDocument);

    MBMetadataService metadataService = MBMetadataService.getInstance();

    // We are not sure at this moment if the activity indicator is shown. But to be sure; try to hide it.
    // This might mess up the count of the activity indicators if more than one page is being constructed in the background;
    // however most of the times this will work out; so:
    //    _viewManager.hideActivityIndicatorForDialog(outcome.getDialogName());
    _viewManager.hideActivityIndicator();

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
        Log.w("MOBBL", "No outcome with origin=" + outcome.getOriginName()
                       + " name=exception defined to handle errors; so re-throwing exception");
        throw new RuntimeException(exception);
      }
      if ("exception".equals(outcome.getOutcomeName()))
      {
        Log.w("MOBBL",
              "Error in handling an outcome with name=exception (i.e. the error handling in the controller is probably misconfigured) Re-throwing exception");
        throw new RuntimeException(exception);
      }

      MBOutcome genericExceptionHandler = new MBOutcome("exception", exceptionDocument);
      genericExceptionHandler.setDialogName(outcome.getDialogName());
      genericExceptionHandler.setPath(outcome.getPath());

      doHandleOutcome(genericExceptionHandler);
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
      _modalPageID = id;
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
    return _modalPageID;
  }

  public void clearModalPageID()
  {
    _modalPageID = null;
  }
}
