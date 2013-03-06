package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBAlertDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogGroupDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.background.MBPerformActionInBackgroundRunner;
import com.itude.mobile.mobbl2.client.core.controller.background.MBPreparePageInBackgroundRunner;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBInvalidOutcomeException;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBNoOutcomesDefinedException;
import com.itude.mobile.mobbl2.client.core.controller.util.indicator.MBIndicator;
import com.itude.mobile.mobbl2.client.core.controller.util.indicator.MBIndicator.Type;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;

/**
 * @author Coen Houtman
 *
 */
public class MBOutcomeHandler extends Handler
{
  @Override
  public void handleMessage(Message msg)
  {
    Log.d(Constants.APPLICATION_NAME, "MBOutcomeHandler.handleMessage(): " + msg.what);
    if (msg.what == Constants.C_MESSAGE_INITIAL_OUTCOMES_FINISHED)
    {
      MBApplicationController.getInstance().finishedInitialOutcomes();
      return;
    }
    MBOutcome outcome = msg.getData().getParcelable("outcome");
    boolean throwException = msg.getData().getBoolean("throwException", true);

    if (throwException)
    {
      try
      {
        handleOutcome(outcome);
      }
      catch (Exception e)
      {
        MBApplicationController.getInstance().handleException(e, outcome);
      }
    }
    else handleOutcome(outcome);
  }

  public void handleOutcomeSynchronously(MBOutcome outcome, boolean throwException)
  {
    outcome.setNoBackgroundProcessing(true);
    if (throwException)
    {
      try
      {
        handleOutcome(outcome);
      }
      catch (Exception e)
      {
        MBApplicationController.getInstance().handleException(e, outcome);
      }
    }
    else handleOutcome(outcome);
  }

  private MBIndicator showIndicatorForOutcome(MBOutcome outcome)
  {
    /*CH: Exception pages prepare in the background. Android has trouble if the
    activity indicator is shown. Somehow this problem does not occur when preparing 
    a normal page*/
    if ("exception".equals(outcome.getOutcomeName())) return MBIndicator.show(Type.none);

    String indicator = outcome.getIndicator();
    if (indicator == null || "ACTIVITY".equals(indicator))
    {
      return MBIndicator.show(Type.activity);

    }
    else if ("PROGRESS".equals(indicator))
    {
      return MBIndicator.show(Type.indeterminate);

    }
    else throw new MBException("Unknown indicator type " + indicator);
  }

  private void handleOutcome(MBOutcome outcome)
  {
    Log.d(Constants.APPLICATION_NAME, "MBOutcomeHandler.handleOutcome: " + outcome);

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
          .w(Constants.APPLICATION_NAME,
             "MBApplicationController.doHandleOutcome: origin="
                 + outcome.getOriginName()
                 + "and name="
                 + outcome.getOutcomeName()
                 + " has persistDocument=TRUE but there is no document (probably the outcome originates from an action; which cannot have a document)");
      else MBDataManagerService.getInstance().storeDocument(outcome.getDocument());
    }

    for (MBOutcomeDefinition outcomeDef : outcomeDefinitions)
    {
      if ("RESET_CONTROLLER".equals(outcomeDef.getAction()))
      {
        final MBApplicationController applicationController = MBApplicationController.getInstance();
        applicationController.resetController();
      }
      else
      {

        final MBOutcome outcomeToProcess = createOutcomeCopy(outcome, outcomeDef);

        if (outcomeToProcess.isPreConditionValid())
        {

          handleDialogChanges(outcomeToProcess);

          MBActionDefinition actionDef = metadataService.getDefinitionForActionName(outcomeDef.getAction(), false);
          if (actionDef != null) handleAction(outcomeToProcess, actionDef);

          MBPageDefinition pageDef = metadataService.getDefinitionForPageName(outcomeDef.getAction(), false);
          if (pageDef != null) handlePageTransition(outcomeToProcess, pageDef);

          MBAlertDefinition alertDef = metadataService.getDefinitionForAlertName(outcomeDef.getAction(), false);
          if (alertDef != null) handleAlert(outcomeToProcess, alertDef);

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

  private void handleDialogChanges(final MBOutcome outcomeToProcess)
  {
    final MBApplicationController applicationController = MBApplicationController.getInstance();
    final MBViewManager viewManager = applicationController.getViewManager();

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
      applicationController.setOutcomeWhichCausedModal(outcomeToProcess);
    }
    else if ("ENDMODAL".equals(outcomeToProcess.getDisplayMode()))
    {
      if (applicationController.getModalPageID() != null)
      {
        viewManager.endModalDialog(applicationController.getModalPageID());
      }
    }
    else if ("ENDMODAL_CONTINUE".equals(outcomeToProcess.getDisplayMode()))
    {
      viewManager.endModalDialog();
      applicationController.setOutcomeWhichCausedModal(outcomeToProcess);
    }
    else if ("POP".equals(outcomeToProcess.getDisplayMode()))
    {
      viewManager.popPage(outcomeToProcess.getDialogName());
      outcomeToProcess.setDialogName(null);
    }
    else if ("POPALL".equals(outcomeToProcess.getDisplayMode()))
    {
      viewManager.endDialog(outcomeToProcess.getDialogName(), true);
    }
    else if ("CLEAR".equals(outcomeToProcess.getDisplayMode()))
    {
      viewManager.resetView();
    }
    else if ("END".equals(outcomeToProcess.getDisplayMode()))
    {
      viewManager.endDialog(outcomeToProcess.getDialogName(), false);
    }
    else
    {
      viewManager.runOnUiThread(new Runnable()
      {
        @Override
        public void run()
        {
          viewManager.activateDialogWithName(outcomeToProcess.getDialogName());
        }
      });
    }
  }

  private MBOutcome createOutcomeCopy(MBOutcome outcome, MBOutcomeDefinition outcomeDef)
  {
    // Create a working copy of the outcome; we manipulate the outcome below and we want the passed outcome to be left unchanged (good practise)
    final MBOutcome outcomeToProcess = new MBOutcome(outcomeDef);
    outcomeToProcess.setPath(outcome.getPath());
    outcomeToProcess.setDocument(outcome.getDocument());
    outcomeToProcess.setDialogName(outcome.getDialogName());
    outcomeToProcess.setNoBackgroundProcessing(outcome.getNoBackgroundProcessing() || outcomeDef.getNoBackgroundProcessing());

    String copyIndicator = outcome.getIndicator();
    if (copyIndicator == null)
    {
      copyIndicator = outcomeDef.getIndicator();
    }
    outcomeToProcess.setIndicator(copyIndicator);

    // Update a possible switch of dialog/display mode set by the outcome definition
    if (outcomeDef.getDialog() != null && MBApplicationController.getInstance().getModalPageID() == null)
    {
      outcomeToProcess.setDialogName(resolveDialogName(outcomeDef.getDialog()));
    }
    if (outcomeDef.getDisplayMode() != null)
    {
      outcomeToProcess.setDisplayMode(outcomeDef.getDisplayMode());
    }
    if (outcomeToProcess.getOriginDialogName() == null)
    {
      outcomeToProcess.setOriginDialogName(outcomeToProcess.getDialogName());
    }
    return outcomeToProcess;
  }

  private void handlePageTransition(final MBOutcome outcomeToProcess, final MBPageDefinition pageDef)
  {
    Log.d(Constants.APPLICATION_NAME, "Going to page " + pageDef.getName());

    //PT: Not sure why this is here; kept after refactoring until I figure out its use.. :')
    final String selectPageInDialog = "yes";

    final MBApplicationController applicationController = MBApplicationController.getInstance();

    if (outcomeToProcess.getNoBackgroundProcessing())
    {
      applicationController.preparePage(new MBOutcome(outcomeToProcess), pageDef.getName(), selectPageInDialog,
                                        applicationController.getBackStackEnabled());
    }
    else
    {
      final MBIndicator indicator = showIndicatorForOutcome(outcomeToProcess);
      // AsyncTasks must be created and executed on the UI Thread!
      Bundle bundle = new Bundle();
      bundle.putString("selectPageInDialog", selectPageInDialog);

      Runnable runnable = new MBThread(null, bundle)
      {
        @Override
        public void runMethod()
        {
          MBPreparePageInBackgroundRunner runner = new MBPreparePageInBackgroundRunner(indicator);
          runner.setController(applicationController);
          runner.setOutcome(new MBOutcome(outcomeToProcess));
          runner.setPageName(pageDef.getName());
          runner.setSelectPageInDialog(getStringParameter("selectPageInDialog"));
          runner.setBackStackEnabled(applicationController.getBackStackEnabled());
          runner.execute(new Object[0]);
        }
      };
      MBViewManager.getInstance().runOnUiThread(runnable);
    }

  }

  private void handleAction(final MBOutcome outcomeToProcess, final MBActionDefinition actionDef)
  {

    final MBApplicationController applicationController = MBApplicationController.getInstance();

    if (outcomeToProcess.getNoBackgroundProcessing())
    {
      applicationController.performAction(new MBOutcome(outcomeToProcess), actionDef);
    }
    else
    {
      final MBIndicator indicator = showIndicatorForOutcome(outcomeToProcess);
      // AsyncTasks must be created and executed on the UI Thread!
      MBViewManager.getInstance().runOnUiThread(new Runnable()
      {
        @Override
        public void run()
        {
          MBPerformActionInBackgroundRunner runner = new MBPerformActionInBackgroundRunner(indicator);

          runner.setController(applicationController);
          runner.setOutcome(new MBOutcome(outcomeToProcess));
          runner.setActionDefinition(actionDef);
          runner.execute(new Object[0]);
        }
      });
    }
  }

  private void handleAlert(final MBOutcome outcomeToProcess, final MBAlertDefinition alertDef)
  {
    final MBApplicationController applicationController = MBApplicationController.getInstance();
    applicationController.prepareAlert(new MBOutcome(outcomeToProcess), alertDef.getName(), applicationController.getBackStackEnabled());
  }

  /***
   * In case of:
   *  1. a splitted dialog; 
   *  2. a page must always put either left or right; and
   *  3. that page can be used in multiple dialogs
   *  
   *  Instead of defining the specific dialog name, either LEFT or RIGHT can be defined as the target dialog.
   *  The page will be displayed in either the left or right part of the active dialog.
   *  
   *  Example outcome definition:
   *  <Outcome origin="*" name="OUTCOME-page_winnerslosers_overview" action="PAGE-page_winnerslosers_overview" transferDocument="TRUE" dialog="LEFT"/>
   *  
   * @param dialogName
   * @return the dialog name to place the page in
   */
  private String resolveDialogName(String dialogName)
  {
    if (!"RIGHT".equals(dialogName) && !"LEFT".equals(dialogName))
    {
      return dialogName;
    }

    String newDialogName = null;

    String activeDialogName = MBApplicationController.getInstance().activeDialogName();
    MBDialogDefinition activeDialogDef = MBMetadataService.getInstance().getDefinitionForDialogName(activeDialogName);

    if (activeDialogDef.isGroup())
    {
      MBDialogGroupDefinition activeDialogGroupDef = (MBDialogGroupDefinition) activeDialogDef;
      List<MBDialogDefinition> children = activeDialogGroupDef.getChildren();

      MBDialogDefinition dialogDef = null;
      if ("RIGHT".equals(dialogName))
      {
        dialogDef = children.get(children.size() - 1);
      }
      else if ("LEFT".equals(dialogName))
      {
        dialogDef = children.get(0);
      }

      if (dialogDef != null)
      {
        newDialogName = dialogDef.getName();

        Log.d(Constants.APPLICATION_NAME, "Dialog name '" + dialogName + "' resolved to '" + newDialogName + "'");
      }
    }

    return newDialogName;
  }
}
