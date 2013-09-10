package com.itude.mobile.mobbl2.client.core.controller;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.background.MBPreparePageInBackgroundRunner;
import com.itude.mobile.mobbl2.client.core.controller.background.MBPreparePageInBackgroundRunner.PageBuildResult;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;
import com.itude.mobile.mobbl2.client.core.util.threads.exception.MBInterruptedException;

public class MBPageTask extends MBOutcomeTask implements MBPreparePageInBackgroundRunner.Callback
{

  private final MBPageDefinition _pageDefinition;

  public MBPageTask(MBOutcomeTaskManager manager, MBPageDefinition pageDefinition)
  {
    super(manager);
    _pageDefinition = pageDefinition;
  }

  public MBPageDefinition getPageDefinition()
  {
    return _pageDefinition;
  }

  @Override
  protected Threading getThreading()
  {
    return getOutcome().getNoBackgroundProcessing() ? Threading.CURRENT : Threading.BACKGROUND;
  }

  @Override
  protected void execute()
  {
    Log.d(Constants.APPLICATION_NAME, "Going to page " + getPageDefinition().getName());

    final MBApplicationController applicationController = MBApplicationController.getInstance();
    PageBuildResult result = applicationController.preparePage(new MBOutcome(getOutcome()), getPageDefinition().getName(),
                                                               applicationController.getBackStackEnabled());
    onPagePrepared(result);
  }

  @Override
  public void onPagePrepared(final PageBuildResult result)
  {
    MBViewManager.getInstance().runOnUiThread(new MBThread()
    {
      @Override
      public void runMethod() throws MBInterruptedException
      {
        MBApplicationController.getInstance().showResultingPage(result);
      }
    });

  }

}
