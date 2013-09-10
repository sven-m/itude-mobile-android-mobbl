package com.itude.mobile.mobbl2.client.core.controller;

public class MBDialogSwitchTask extends MBOutcomeTask
{

  public MBDialogSwitchTask(MBOutcomeTaskManager manager)
  {
    super(manager);
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
  }

}
