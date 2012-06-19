package com.itude.mobile.mobbl2.client.core.util;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;

public class DoubleUtilitiesTest extends ApplicationTestCase<MBApplicationController>
{

  public DoubleUtilitiesTest()
  {
    super(MBApplicationController.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    createApplication();
    MBMetadataService.setConfigName("unittests/config_unittests.xml");
    MBMetadataService.setEndpointsName("testconfig/endpoints.xml");
    super.setUp();
  }

  public void testRound()
  {
    double result = DoubleUtilities.round(new Double(15), 2);
    assertEquals(new Double(15.00), result);
  }

  public void testRoundTwo()
  {
    double result = DoubleUtilities.round(new Double(15.56), 2);
    assertEquals(new Double(15.56), result);
  }

}
