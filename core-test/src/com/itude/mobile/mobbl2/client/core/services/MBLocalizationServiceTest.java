package com.itude.mobile.mobbl2.client.core.services;

import java.util.Locale;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.MBApplicationCore;

public class MBLocalizationServiceTest extends ApplicationTestCase<MBApplicationCore>
{

  public MBLocalizationServiceTest()
  {
    super(MBApplicationCore.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    createApplication();
    MBMetadataService.setConfigName("unittests/config_unittests.xml");
    MBMetadataService.getInstance().parseEndPointFile("testconfig/endpoints.xml");

    super.setUp();
  }

  public void testLocaleCode()
  {
    String localeCode = MBLocalizationService.getInstance().getLocaleCode();
    assertNotNull(localeCode);
    assertEquals("en_US", localeCode);

    Locale locale = MBLocalizationService.getInstance().getLocale();
    assertNotNull(locale);
    assertEquals("en", locale.getLanguage());
    assertEquals("US", locale.getCountry());
  }
}
