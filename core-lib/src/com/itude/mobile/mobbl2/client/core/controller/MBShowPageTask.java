package com.itude.mobile.mobbl2.client.core.controller;

import com.itude.mobile.mobbl2.client.core.controller.background.MBPreparePageInBackgroundRunner.PageBuildResult;

public class MBShowPageTask extends MBOutcomeTask<Object>
{

  private final ResultContainer<PageBuildResult> _page;

  public MBShowPageTask(MBOutcomeTaskManager manager, ResultContainer<PageBuildResult> page)
  {
    super(manager);
    _page = page;

  }

  @Override
  protected com.itude.mobile.mobbl2.client.core.controller.MBOutcomeTask.Threading getThreading()
  {
    return Threading.UI;
  }

  @Override
  protected void execute()
  {
    MBApplicationController.getInstance().showResultingPage(_page.getResult());
  }

}
