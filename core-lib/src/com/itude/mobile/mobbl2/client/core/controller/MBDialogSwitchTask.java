package com.itude.mobile.mobbl2.client.core.controller;

public class MBDialogSwitchTask extends MBOutcomeTask
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

    if ("MODAL".equals(getOutcome().getDisplayMode()) || "MODALWITHCLOSEBUTTON".equals(getOutcome().getDisplayMode())
        || "MODALFORMSHEET".equals(getOutcome().getDisplayMode()) || "MODALFORMSHEETWITHCLOSEBUTTON".equals(getOutcome().getDisplayMode())
        || "MODALPAGESHEET".equals(getOutcome().getDisplayMode()) || "MODALPAGESHEETWITHCLOSEBUTTON".equals(getOutcome().getDisplayMode())
        || "MODALFULLSCREEN".equals(getOutcome().getDisplayMode())
        || "MODALFULLSCREENWITHCLOSEBUTTON".equals(getOutcome().getDisplayMode())
        || "MODALCURRENTCONTEXT".equals(getOutcome().getDisplayMode())
        || "MODALCURRENTCONTEXTWITHCLOSEBUTTON".equals(getOutcome().getDisplayMode()))
    {
      applicationController.setOutcomeWhichCausedModal(getOutcome());
    }
    else if ("ENDMODAL".equals(getOutcome().getDisplayMode()))
    {
      if (applicationController.getModalPageID() != null)
      {
        viewManager.endModalDialog(applicationController.getModalPageID());
      }
    }
    else if ("ENDMODAL_CONTINUE".equals(getOutcome().getDisplayMode()))
    {
      viewManager.endModalDialog();
      applicationController.setOutcomeWhichCausedModal(getOutcome());
    }
    else if ("POP".equals(getOutcome().getDisplayMode()))
    {
      viewManager.popPage(getOutcome().getDialogName());
      getOutcome().setDialogName(null);
    }
    else if ("POPALL".equals(getOutcome().getDisplayMode()))
    {
      viewManager.endDialog(getOutcome().getDialogName(), true);
    }
    else if ("CLEAR".equals(getOutcome().getDisplayMode()))
    {
      viewManager.resetView();
    }
    else if ("END".equals(getOutcome().getDisplayMode()))
    {
      viewManager.endDialog(getOutcome().getDialogName(), false);
    }
    else if (getOutcome().getDialogName() != null && !"BACKGROUND".equals(getOutcome().getDisplayMode())
             && !getOutcome().getDialogName().equals(_dialogWhenCreated) && !applicationController.isSuppressPageSelection()) viewManager
        .getDialogManager().activateDialog(getOutcome().getDialogName());

  }
}
