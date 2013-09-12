package com.itude.mobile.mobbl2.client.core.controller;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController.PageBuildResult;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBPageTask extends MBOutcomeTask<PageBuildResult>
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

    setResult(result);
  }

}
