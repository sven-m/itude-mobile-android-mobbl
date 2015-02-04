package com.itude.mobile.mobbl.core.controller;

import com.itude.mobile.mobbl.core.view.MBOutcomeListenerProtocol;

/**
 * {@link MBOutcomeTask} class describing a notify outcome listener before task
 */
public class MBNotifyOutcomeListenersBeforeTask extends MBOutcomeTask<Object>
{

  public MBNotifyOutcomeListenersBeforeTask(MBOutcomeTaskManager manager)
  {
    super(manager);
  }

  @Override
  protected void execute()
  {
    for (MBOutcomeListenerProtocol listener : MBApplicationController.getInstance().getOutcomeHandler().getOutcomeListeners())
      listener.outcomeProduced(getOutcome());
  }

}
