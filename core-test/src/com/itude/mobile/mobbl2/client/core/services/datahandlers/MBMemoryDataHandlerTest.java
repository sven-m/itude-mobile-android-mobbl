package com.itude.mobile.mobbl2.client.core.services.datahandlers;

import java.util.Date;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.util.DataUtil;

public class MBMemoryDataHandlerTest extends ApplicationTestCase<MBApplicationCore>
{

  public MBMemoryDataHandlerTest()
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

  public void testLoadDocumentString()
  {
    MBDocument doc = MBDataManagerService.getInstance().loadDocument("TestDocument1");
    assertNotNull(doc);
  }

  public void testStoreDocument()
  {
    MBDocument doc = MBDataManagerService.getInstance().loadDocument("TestDocument1");
    MBDocument copy = doc.clone();
    MBElement el = ((MBElement) copy.getValueForPath("/LoginInfo[0]"));
    String testValue = String.valueOf((new Date()).getTime());
    el.setAttributeValue(testValue, "LoginMessage");
    MBDataManagerService.getInstance().storeDocument(copy);
    copy = MBDataManagerService.getInstance().loadDocument("TestDocument1");
    assertNotSame(doc, copy);
  }

}
