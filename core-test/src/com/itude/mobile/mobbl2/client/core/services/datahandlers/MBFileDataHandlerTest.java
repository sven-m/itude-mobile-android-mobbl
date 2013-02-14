package com.itude.mobile.mobbl2.client.core.services.datahandlers;

import java.util.Date;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.android.util.FileUtil;
import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;

public class MBFileDataHandlerTest extends ApplicationTestCase<MBApplicationCore>
{

  public MBFileDataHandlerTest()
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
    MBDocument doc = MBDataManagerService.getInstance().loadDocument("Books");
    MBElement author = (MBElement) doc.getValueForPath("/Author[0]");

    assertNotNull(author);
  }

  public void testStoreDocument()
  {
    MBDocument doc = MBDataManagerService.getInstance().loadDocument("Books");
    MBDocument copy = doc.clone();
    MBElement el = ((MBElement) copy.getValueForPath("/Author[0]/Book[0]"));
    String testValue = String.valueOf((new Date()).getTime());
    el.setAttributeValue(testValue, "isbn");
    MBDataManagerService.getInstance().storeDocument(copy);
    copy = MBDataManagerService.getInstance().loadDocument("Books");
    assertNotSame(doc, copy);

    byte[] rawData = FileUtil.getInstance().getByteArray("documents/Books.xml");
    assertNotNull(rawData);
    assertEquals(copy.toString(), new String(rawData));

  }

}
