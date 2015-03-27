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

import com.itude.mobile.mobbl.core.util.MBConstants;

/**
 * {@link MBOutcomeTask} class describing a dialog swith task
 */
public class MBDialogSwitchTask extends MBOutcomeTask<Object>
{
  private final String _dialogWhenCreated;

  public MBDialogSwitchTask(MBOutcomeTaskManager manager)
  {
    super(manager);
    MBDialogController currentDialog = MBViewManager.getInstance().getDialogManager().getActiveDialog();

    // the active dialog when this task was created is kept, because it is possible that the dialog is changed between firing this outcome, and this outcome
    // task being executed. However, the expected behaviour is that the dialog isn't switched if it currently (i.e. when the outcome is fired) is active, so we
    // compare the dialog being switched to with the active dialog when the outcome was fired, instead of the active dialog when this task is handled.
    _dialogWhenCreated = currentDialog != null ? currentDialog.getName() : null;
  }

  @Override
  protected Threading getThreading()
  {
    return Threading.UI;
  }

  @Override
  protected void execute()
  {
    final MBApplicationController applicationController = MBApplicationController.getInstance();
    final MBViewManager viewManager = applicationController.getViewManager();

    if ("ENDMODAL".equals(getOutcome().getDisplayMode()))
    {
      viewManager.endDialog(getOutcome().getPageStackName(), false);
      getOutcome().setPageStackName(null);
    }
    else if ("ENDMODAL_CONTINUE".equals(getOutcome().getDisplayMode()))
    {
      viewManager.endDialog(getOutcome().getPageStackName(), true);
      getOutcome().setPageStackName(null);
    }
    else if ("POP".equals(getOutcome().getDisplayMode()))
    {
      viewManager.popPage(getOutcome().getPageStackName());
      getOutcome().setPageStackName(null);
    }
    else if ("POPALL".equals(getOutcome().getDisplayMode()))
    {
      viewManager.endDialog(getOutcome().getPageStackName(), true);
      getOutcome().setPageStackName(null);
    }
    else if ("CLEAR".equals(getOutcome().getDisplayMode()))
    {
      viewManager.resetView();
    }
    else if ("END".equals(getOutcome().getDisplayMode()))
    {
      viewManager.endDialog(getOutcome().getPageStackName(), false);
    }
    else if (getOutcome().getPageStackName() != null
             && !MBConstants.C_DISPLAY_MODE_BACKGROUND.equals(getOutcome().getDisplayMode()) //
             && getOutcome().getPageStackName() != null
             && !MBConstants.C_DISPLAY_MODE_BACKGROUNDPIPELINEREPLACE.equals(getOutcome().getDisplayMode()) //
             && !getOutcome().getPageStackName().equals(_dialogWhenCreated) //
             && !applicationController.isSuppressPageSelection())
    {
      viewManager.getDialogManager().activatePageStack(getOutcome().getPageStackName());

    }

  }
}
