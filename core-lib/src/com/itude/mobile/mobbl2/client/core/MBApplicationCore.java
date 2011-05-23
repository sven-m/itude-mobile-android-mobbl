package com.itude.mobile.mobbl2.client.core;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationFactory;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;

public class MBApplicationCore extends MBApplicationController
{

  @Override
  public void startController()
  {
    MBMetadataService.setPhoneConfigName("testconfig/testconfig.xml");
    MBMetadataService.setTabletConfigName("testconfig/testconfig_tablet.xml");
    MBMetadataService.setEndpointsName("testconfig/endpoints.xml");
    startApplication(MBApplicationFactory.getInstance());
  }
}
