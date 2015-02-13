package com.itude.mobile.mobbl.core.model;

import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;

public class MBElementTest extends MBDocumentAbstractTest
{

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  public void testAsXML()
  {
    assertNotNull(getXmlDocumentData());
    assertNotNull(getConfigData());
    assertNotNull(getConfig());

    MBDocumentDefinition docDef = getConfig().getDefinitionForDocumentName("Books");
    assertNotNull(docDef);

    MBDocumentFactory documentFactory = MBDocumentFactory.getInstance();
    String parserType = MBDocumentFactory.PARSER_XML;

    MBDocument document = documentFactory.getDocumentWithData(getXmlDocumentData(), parserType, docDef);
    assertNotNull(document);

    // first pass
    String pass1 = document.asXmlWithLevel(new StringBuffer(4096), 0).toString();

    // second pass
    byte[] serializedXmlData = pass1.getBytes();

    MBDocument reparsedDocument = documentFactory.getDocumentWithData(serializedXmlData, parserType, docDef);

    String pass2 = document.asXmlWithLevel(new StringBuffer(4096), 0).toString();

    assertEquals(pass1, pass2);
  }

}
