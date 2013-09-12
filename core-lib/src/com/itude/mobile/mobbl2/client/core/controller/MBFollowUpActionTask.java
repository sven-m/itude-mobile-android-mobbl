package com.itude.mobile.mobbl2.client.core.controller;

public class MBFollowUpActionTask extends MBOutcomeTask<Object>
{
  private final ResultContainer<MBOutcome> _outcome;

  public MBFollowUpActionTask(MBOutcomeTaskManager manager, ResultContainer<MBOutcome> outcome)
  {
    super(manager);
    _outcome = outcome;
  }

  @Override
  protected void execute()
  {
    if (_outcome.getResult() != null) MBApplicationController.getInstance().handleOutcome(_outcome.getResult());
  }

}
