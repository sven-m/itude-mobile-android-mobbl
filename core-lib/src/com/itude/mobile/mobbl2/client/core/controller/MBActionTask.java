package com.itude.mobile.mobbl2.client.core.controller;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBActionDefinition;

public class MBActionTask extends MBOutcomeTask<MBOutcome>
{
  private final MBActionDefinition _action;

  public MBActionTask(MBOutcomeTaskManager manager, MBActionDefinition action)
  {
    super(manager);
    _action = action;
  }

  public MBActionDefinition getAction()
  {
    return _action;
  }

  @Override
  protected Threading getThreading()
  {
    return getOutcome().getNoBackgroundProcessing() ? Threading.CURRENT : Threading.BACKGROUND;
  }

  @Override
  protected void execute()
  {
    setResult(MBApplicationController.getInstance().performAction(new MBOutcome(getOutcome()), getAction()));
  }

}
