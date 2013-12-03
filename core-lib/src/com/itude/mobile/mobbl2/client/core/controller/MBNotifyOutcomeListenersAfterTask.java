package com.itude.mobile.mobbl2.client.core.controller;

import com.itude.mobile.mobbl2.client.core.view.MBOutcomeListenerProtocol;

public class MBNotifyOutcomeListenersAfterTask extends MBOutcomeTask<Object>
{

  public MBNotifyOutcomeListenersAfterTask(MBOutcomeTaskManager manager)
  {
    super(manager);
  }

  @Override
  protected com.itude.mobile.mobbl2.client.core.controller.MBOutcomeTask.Threading getThreading()
  {
    return Threading.UI;
  }

  @Override
  protected void execute()
  {
    for (MBOutcomeListenerProtocol listener : MBApplicationController.getInstance().getOutcomeHandler().getOutcomeListeners())
      listener.afterOutcomeHandled(getOutcome());
  }

}
