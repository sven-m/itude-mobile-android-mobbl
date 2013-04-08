package com.itude.mobile.mobbl2.client.core.services;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.DataUtil;
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
    DataUtil.getInstance().setContext(getContext());

    MBMetadataService.setConfigName("testconfig/testconfig.xml");
  }

  public void testService()
  {
    MBLocalizationService instance = MBLocalizationService.getInstance();
    assertNotNull(instance);
  }

  public void testText()
  {
    String text = MBLocalizationService.getInstance().getTextForKey("normal");
    assertEquals("Normaal", text);
  }

  public void testTextWithArguments()
  {
    String text = MBLocalizationService.getInstance().getText("substitution", new Integer(2));
    assertEquals("Er zijn 2 variabelen", text);

    text = MBLocalizationService.getInstance().getText("substitution");
    assertNotNull(text);
  }

}
