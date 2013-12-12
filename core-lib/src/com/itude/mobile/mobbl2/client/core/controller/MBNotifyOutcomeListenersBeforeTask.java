package com.itude.mobile.mobbl2.client.core.controller;

import com.itude.mobile.mobbl2.client.core.view.MBOutcomeListenerProtocol;

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
