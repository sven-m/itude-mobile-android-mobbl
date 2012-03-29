package com.itude.mobile.mobbl2.client.core.services;

import java.util.Locale;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.util.DataUtil;

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
    DataUtil.getInstance().setContext(getContext());

    MBMetadataService.setConfigName("testconfig/testconfig.xml");
  }

  public void testLocaleCode()
  {
    String localeCode = MBLocalizationService.getInstance().getLocaleCode();
    assertNotNull(localeCode);
    assertEquals("en_NL", localeCode);

    Locale locale = MBLocalizationService.getInstance().getLocale();
    assertNotNull(locale);
    assertEquals("en", locale.getLanguage());
    assertEquals("NL", locale.getCountry());
  }
}
