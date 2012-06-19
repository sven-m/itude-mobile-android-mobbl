package com.itude.mobile.mobbl2.client.core.util;

import java.util.Date;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;

public class DateUtilitiesTest extends ApplicationTestCase<MBApplicationController>
{

  public DateUtilitiesTest()
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

  public void testDateFromString()
  {
    Date result = DateUtilities.dateFromString("01-02-2010", "dd-MM-yyyy");
    assertEquals(1, result.getDate());
    assertEquals(1, result.getMonth());
    assertEquals(110, result.getYear());
  }

  public void testGetYear()
  {
    Date result = DateUtilities.dateFromString("01-02-2010", "dd-MM-yyyy");
    assertEquals(1, result.getDate());
    assertEquals(1, result.getMonth());
    assertEquals("2010", DateUtilities.getYear(result, "yyyy"));
  }
}
