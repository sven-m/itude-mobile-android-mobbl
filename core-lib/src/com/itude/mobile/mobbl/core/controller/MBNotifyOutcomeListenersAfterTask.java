package com.itude.mobile.mobbl.core.controller;

import com.itude.mobile.mobbl.core.view.MBOutcomeListenerProtocol;

/**
 * {@link MBOutcomeTask} class describing a notify outcome listener after task
 */
public class MBNotifyOutcomeListenersAfterTask extends MBOutcomeTask<Object>
{

  public MBNotifyOutcomeListenersAfterTask(MBOutcomeTaskManager manager)
  {
    super(manager);
  }

  @Override
  protected com.itude.mobile.mobbl.core.controller.MBOutcomeTask.Threading getThreading()
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
