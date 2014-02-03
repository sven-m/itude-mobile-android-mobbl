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
import java.util.List;

import android.util.Log;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.configuration.mvc.MBActionDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBAlertDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBOutcomeDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl.core.controller.exceptions.MBNoOutcomesDefinedException;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.util.Constants;

public class MBOutcomeRunner
{
  private final MBOutcome _outcome;
  private final boolean   _throwException;
  private List<MBOutcome> _toProcess;

  public MBOutcomeRunner(MBOutcome outcome, boolean throwException)
  {
    _outcome = outcome;
    _throwException = throwException;
  }

  public void handle()
  {
    if (_throwException)
    {
      try
      {
        actuallyHandle();
      }
      catch (Exception e)
      {
        MBApplicationController.getInstance().handleException(e, _outcome);
      }
    }
    else actuallyHandle();
  }

  private void actuallyHandle()
  {
    supplementOrigin();
    clearCaches();
    prepareOutcomeCopies();
    persistIfNeeded();
    resolveDialogNames();

    for (MBOutcome outcome : _toProcess)
    {
      MBOutcomeTaskManager manager = setupTaskManager(outcome);
      manager.run();
    }
  }

  private void supplementOrigin()
  {
    if (_outcome.getOrigin() == null) _outcome.setOrigin(new MBOutcome.Origin());
    if (StringUtil.isEmpty(_outcome.getOrigin().getDialog())) _outcome.getOrigin().withDialog(MBViewManager.getInstance()
                                                                                                  .getActiveDialogName());
  }

  private void clearCaches()
  {
    if (_outcome.getDocument() != null)
    {
      _outcome.getDocument().clearAllCaches();
    }

  }

  private void prepareOutcomeCopies()
  {

    MBMetadataService metadataService = MBMetadataService.getInstance();

    List<MBOutcomeDefinition> outcomeDefinitions = metadataService.getOutcomeDefinitionsForOrigin(_outcome.getOrigin(),
                                                                                                  _outcome.getOutcomeName(), false);
    if (outcomeDefinitions.isEmpty())
    {
      String msg = "No outcome defined for origin=" + _outcome.getOrigin() + " outcome=" + _outcome.getOutcomeName();
      throw new MBNoOutcomesDefinedException(msg);
    }

    List<MBOutcome> outcomes = new ArrayList<MBOutcome>(outcomeDefinitions.size());

    for (MBOutcomeDefinition outcomeDef : outcomeDefinitions)
    {
      MBOutcome outcomeToProcess = _outcome.createCopy(outcomeDef);
      outcomes.add(outcomeToProcess);
    }

    _toProcess = outcomes;
  }

  private void persistIfNeeded()
  {
    for (MBOutcome outcome : _toProcess)

      if (outcome.getPersist())
      {
        if (_outcome.getDocument() == null) Log
            .w(Constants.APPLICATION_NAME,
               "MBApplicationController.doHandleOutcome: origin="
                   + _outcome.getOrigin()
                   + "and name="
                   + _outcome.getOutcomeName()
                   + " has persistDocument=TRUE but there is no document (probably the outcome originates from an action; which cannot have a document)");
        else MBDataManagerService.getInstance().storeDocument(_outcome.getDocument());
        break;
      }
  }

  private void resolveDialogNames()
  {
    for (MBOutcome outcome : _toProcess)
      outcome.setPageStackName(MBOutcomeHandler.resolvePageStackName(outcome.getPageStackName()));
  }

  private MBOutcomeTaskManager setupTaskManager(MBOutcome outcome)
  {
    Log.d(Constants.APPLICATION_NAME, "MBOutcomeRunner.setupTaskManager: " + outcome);

    MBOutcomeTaskManager manager = new MBOutcomeTaskManager(outcome);
    MBMetadataService metadataService = MBMetadataService.getInstance();

    manager.addTask(new MBNotifyOutcomeListenersBeforeTask(manager));

    if ("RESET_CONTROLLER".equals(outcome.getAction()))
    {
      final MBApplicationController applicationController = MBApplicationController.getInstance();
      applicationController.resetController();
    }
    else
    {

      if (outcome.isPreConditionValid())
      {

        MBActionDefinition actionDef = metadataService.getDefinitionForActionName(outcome.getAction(), false);
        MBActionTask actionTask = null;
        if (actionDef != null) manager.addTask(actionTask = new MBActionTask(manager, actionDef));

        MBPageDefinition pageDef = metadataService.getDefinitionForPageName(outcome.getAction(), false);
        if (pageDef != null)
        {

          MBPageTask pageTask = new MBPageTask(manager, pageDef);
          manager.addTask(pageTask);
          manager.addTask(new MBDialogSwitchTask(manager));
          manager.addTask(new MBShowPageTask(manager, pageTask.getResultContainer()));
        }
        else manager.addTask(new MBDialogSwitchTask(manager));

        MBAlertDefinition alertDef = metadataService.getDefinitionForAlertName(outcome.getAction(), false);
        if (alertDef != null) manager.addTask(new MBAlertTask(manager, alertDef));

        if (actionTask != null)
        {
          manager.addTask(new MBFollowUpActionTask(manager, actionTask.getResultContainer()));
        }
        manager.addTask(new MBNotifyOutcomeListenersAfterTask(manager));
      }
    }

    return manager;
  }
}
