package com.itude.mobile.mobbl2.client.core.model;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBMvcConfigurationParser;
import com.itude.mobile.mobbl2.client.core.util.AssetUtil;
import com.itude.mobile.mobbl2.client.core.util.DataUtil;

public class MBDocumentFactoryTest extends ApplicationTestCase<MBApplicationCore>
{

  private byte[]                    configData;
  private byte[]                    mobbl1DocumentData;
  private byte[]                    jsonDocumentData;
  private byte[]                    xmlDocumentData;
  private MBConfigurationDefinition config;

  public MBDocumentFactoryTest()
  {
    super(MBApplicationCore.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    DataUtil.getInstance().setContext(getContext());
    mobbl1DocumentData = AssetUtil.getInstance().getByteArray("unittests/testdocument2.xml");
    jsonDocumentData = AssetUtil.getInstance().getByteArray("unittests/testdocument.txt");
    xmlDocumentData = AssetUtil.getInstance().getByteArray("unittests/testdocument.xml");
    configData = AssetUtil.getInstance().getByteArray("unittests/config_unittests.xml");

    MBMvcConfigurationParser configParser = new MBMvcConfigurationParser();
    config = (MBConfigurationDefinition) configParser.parseData(configData, "config");
  }

  public void testMobbl1Parsing()
  {
    assertNotNull(mobbl1DocumentData);
    assertNotNull(configData);
    assertNotNull(config);

    MBDocumentDefinition docDef = config.getDefinitionForDocumentName("EXT-GebruikersInstellingenGetResponse");
    assertNotNull(docDef);

    MBDocument document = MBDocumentFactory.getInstance().getDocumentWithData(mobbl1DocumentData, MBDocumentFactory.PARSER_MOBBL1, docDef);
    assertNotNull(document);
  }

  public void testJsonParsing()
  {
    assertNotNull(jsonDocumentData);
    assertNotNull(configData);
    assertNotNull(config);

    MBDocumentDefinition docDef = config.getDefinitionForDocumentName("EXT-GebruikersInstellingenGetResponse");
    assertNotNull(docDef);

    MBDocument document = MBDocumentFactory.getInstance().getDocumentWithData(jsonDocumentData, MBDocumentFactory.PARSER_JSON, docDef);
    assertNotNull(document);
  }

  public void testXMLParsing()
  {
    assertNotNull(xmlDocumentData);
    assertNotNull(configData);
    assertNotNull(config);

    MBDocumentDefinition docDef = config.getDefinitionForDocumentName("EXT-GebruikersInstellingenGetResponse");
    assertNotNull(docDef);

    MBDocument document = MBDocumentFactory.getInstance().getDocumentWithData(xmlDocumentData, MBDocumentFactory.PARSER_XML, docDef);
    assertNotNull(document);
  }

}
