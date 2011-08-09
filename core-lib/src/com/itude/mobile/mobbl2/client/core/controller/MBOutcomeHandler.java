package com.itude.mobile.mobbl2.client.core.controller;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDialogGroupDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.background.MBPerformActionInBackgroundRunner;
import com.itude.mobile.mobbl2.client.core.controller.background.MBPreparePageInBackgroundRunner;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBInvalidOutcomeException;
import com.itude.mobile.mobbl2.client.core.controller.exceptions.MBNoOutcomesDefinedException;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.exceptions.MBRunnable;

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

    ArrayList<String> dialogs = new ArrayList<String>();
    String selectPageInDialog = "yes";

    // We need to make sure that the order of the dialog tabs conforms to the order of the outcomes
    // This is not necessarily the case because preparing of page A might take longer in the background than page B
    // Because of this, page B migh be places on a tab prior to page A which is undesired. We handle this by
    // notifying the view handler of the dialogs used by the outcome in sequental order. The viewmanager will then
    // use this information to sort the tabs

    final MBApplicationController applicationController = MBApplicationController.getInstance();
    final MBViewManager viewManager = applicationController.getViewManager();

    for (MBOutcomeDefinition outcomeDef : outcomeDefinitions)
    {
      if ("RESET_CONTROLLER".equals(outcomeDef.getAction()))
      {
        applicationController.resetController();
      }
      else
      {

        // Create a working copy of the outcome; we manipulate the outcome below and we want the passed outcome to be left unchanged (good practise)
        final MBOutcome outcomeToProcess = new MBOutcome(outcomeDef);
        outcomeToProcess.setPath(outcome.getPath());
        outcomeToProcess.setDocument(outcome.getDocument());
        outcomeToProcess.setDialogName(outcome.getDialogName());
        outcomeToProcess.setNoBackgroundProcessing(outcome.getNoBackgroundProcessing() || outcomeDef.getNoBackgroundProcessing());

        if (outcomeToProcess.isPreConditionValid())
        {

          // Update a possible switch of dialog/display mode set by the outcome definition
          if (outcomeDef.getDialog() != null && MBApplicationController.getInstance().getModalPageID() == null) outcomeToProcess
              .setDialogName(resolveDialogName(outcomeDef.getDialog()));
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
            applicationController.setOutcomeWhichCausedModal(outcomeToProcess);
          }
          else if ("ENDMODAL".equals(outcomeToProcess.getDisplayMode()))
          {
            if (applicationController.getModalPageID() != null)
            {
              viewManager.endModalDialog(applicationController.getModalPageID());
            }
            ((MBDialogController) viewManager.getCurrentActivity()).handleAllOnWindowActivated();

          }
          else if ("ENDMODAL_CONTINUE".equals(outcomeToProcess.getDisplayMode()))
          {
            viewManager.endModalDialog();
            applicationController.setOutcomeWhichCausedModal(outcomeToProcess);
          }
          else if ("POP".equals(outcomeToProcess.getDisplayMode()))
          {
            viewManager.popPage(outcomeToProcess.getDialogName());
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
            dialogs.remove(outcomeToProcess.getDialogName());
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

          final MBActionDefinition actionDef = metadataService.getDefinitionForActionName(outcomeDef.getAction(), false);

          if (actionDef != null)
          {
            viewManager.showActivityIndicator();
            if (outcomeToProcess.getNoBackgroundProcessing())
            {
              applicationController.performActionInBackground(new MBOutcome(outcomeToProcess), actionDef);
            }
            else
            {
              // AsyncTasks must be created and executed on the UI Thread!
              viewManager.runOnUiThread(new Runnable()
              {
                @Override
                public void run()
                {
                  MBPerformActionInBackgroundRunner runner = new MBPerformActionInBackgroundRunner();

                  runner.setController(applicationController);
                  runner.setOutcome(new MBOutcome(outcomeToProcess));
                  runner.setActionDefinition(actionDef);
                  runner.execute(new Object[0]);
                }
              });
            }
          }

          final MBPageDefinition pageDef = metadataService.getDefinitionForPageName(outcomeDef.getAction(), false);
          if (pageDef != null)
          {
            /*CH: Exception pages prepare in the background. Android has trouble if the
            activity indicator is shown. Somehow this problem does not occur when preparing 
            a normal page*/
            if (!"exception".equals(outcome.getOutcomeName()))
            {
              viewManager.showActivityIndicator();
            }
            if (outcomeToProcess.getNoBackgroundProcessing())
            {
              applicationController.preparePageInBackground(new MBOutcome(outcomeToProcess), pageDef.getName(), selectPageInDialog,
                                                            applicationController.getBackStackEnabled());
            }
            else
            {
              // AsyncTasks must be created and executed on the UI Thread!
              Bundle bundle = new Bundle();
              bundle.putString("selectPageInDialog", selectPageInDialog);

              Runnable runnable = new MBRunnable(null, bundle)
              {
                @Override
                public void runMethod()
                {
                  MBPreparePageInBackgroundRunner runner = new MBPreparePageInBackgroundRunner();
                  runner.setController(applicationController);
                  runner.setOutcome(new MBOutcome(outcomeToProcess));
                  runner.setPageName(pageDef.getName());
                  runner.setSelectPageInDialog(getStringParameter("selectPageInDialog"));
                  runner.setBackStackEnabled(applicationController.getBackStackEnabled());
                  runner.execute(new Object[0]);
                }
              };
              viewManager.runOnUiThread(runnable);
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

  private String resolveDialogName(String dialogName)
  {
    if (!"RIGHT".equals(dialogName) && !"LEFT".equals(dialogName))
    {
      return dialogName;
    }

    String newDialogName = null;

    String activeDialogName = MBApplicationController.getInstance().activeDialogName();
    MBDialogDefinition activeDialogDef = MBMetadataService.getInstance().getDefinitionForDialogName(activeDialogName);

    if (activeDialogDef instanceof MBDialogGroupDefinition)
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
