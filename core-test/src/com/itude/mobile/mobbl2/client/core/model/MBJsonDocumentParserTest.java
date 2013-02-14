package com.itude.mobile.mobbl2.client.core.model;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.AssetUtil;
import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBMvcConfigurationParser;

public class MBJsonDocumentParserTest extends ApplicationTestCase<MBApplicationCore>
{

  private byte[] configData;
  private byte[] documentData;

  public MBJsonDocumentParserTest()
  {
    super(MBApplicationCore.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    DataUtil.getInstance().setContext(getContext());
    documentData = AssetUtil.getInstance().getByteArray("unittests/testdocument.txt");
    configData = AssetUtil.getInstance().getByteArray("unittests/config_unittests.xml");
  }

  public void testJsonParsingWithData()
  {
    assertNotNull(documentData);
    assertNotNull(configData);

    MBMvcConfigurationParser configParser = new MBMvcConfigurationParser();
    MBConfigurationDefinition config = (MBConfigurationDefinition) configParser.parseData(configData, "config");
    MBDocumentDefinition docDef = config.getDefinitionForDocumentName("EXT-GebruikersInstellingenGetResponse");

    assertNotNull(docDef);

    MBDocument document = MBJsonDocumentParser.getDocumentWithData(documentData, docDef);
    assertNotNull(document);

  }

  public void testJSonParsingWithString()
  {

  }

}
