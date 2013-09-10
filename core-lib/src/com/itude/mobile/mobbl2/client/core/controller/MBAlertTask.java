package com.itude.mobile.mobbl2.client.core.controller;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBAlertDefinition;

public class MBAlertTask extends MBOutcomeTask
{
  private final MBAlertDefinition _alertDefinition;

  public MBAlertTask(MBOutcomeTaskManager manager, MBAlertDefinition alertDefinition)
  {
    super(manager);
    _alertDefinition = alertDefinition;
  }

  public MBAlertDefinition getAlertDefinition()
  {
    return _alertDefinition;
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
    applicationController.prepareAlert(new MBOutcome(getOutcome()), getAlertDefinition().getName(),
                                       applicationController.getBackStackEnabled());

  }

}
