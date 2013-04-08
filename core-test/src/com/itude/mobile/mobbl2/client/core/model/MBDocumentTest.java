package com.itude.mobile.mobbl2.client.core.model;

import java.util.List;

import android.test.ApplicationTestCase;
import android.util.Log;

import com.itude.mobile.android.util.AssetUtil;
import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBElementDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBMvcConfigurationParser;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions.MBInvalidPathException;

public class MBDocumentTest extends ApplicationTestCase<MBApplicationCore>
{

  private byte[]                    configData;
  private byte[]                    jsonDocumentData;
  private byte[]                    xmlDocumentData;
  private String[]                  beforeSortingElements;
  private String[]                  afterSortingElements;
  private String[]                  afterSortingElementsNext;
  private String[]                  afterSortingElementsThird;
  private String[]                  afterSortingElementsFourth;
  private MBConfigurationDefinition config;
  private MBDocumentDefinition      settingsDefinition;
  private MBDocument                jsonSettingsDocument;
  private MBDocument                xmlSettingsDocument;
  private String                    trueExpression;
  private String                    falseExpression;

  public MBDocumentTest()
  {
    super(MBApplicationCore.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    DataUtil.getInstance().setContext(getContext());

    jsonDocumentData = AssetUtil.getInstance().getByteArray("unittests/testdocument.txt");
    xmlDocumentData = AssetUtil.getInstance().getByteArray("unittests/testdocument.xml");
    configData = AssetUtil.getInstance().getByteArray("unittests/config_unittests.xml");

    MBMvcConfigurationParser configParser = new MBMvcConfigurationParser();
    config = (MBConfigurationDefinition) configParser.parseData(configData, "config");
    settingsDefinition = config.getDefinitionForDocumentName("EXT-GebruikersInstellingenGetResponse");
    jsonSettingsDocument = MBDocumentFactory.getInstance().getDocumentWithData(jsonDocumentData, MBDocumentFactory.PARSER_JSON,
                                                                               settingsDefinition);
    xmlSettingsDocument = MBDocumentFactory.getInstance().getDocumentWithData(xmlDocumentData, MBDocumentFactory.PARSER_XML,
                                                                              settingsDefinition);

    beforeSortingElements = new String[]{"1", "2", "27", "10", "28", "10"};
    afterSortingElements = new String[]{"27", "28", "2", "1", "10", "10"};
    afterSortingElementsNext = new String[]{"10", "10", "1", "2", "28", "27"};
    afterSortingElementsThird = new String[]{"27", "28", "2", "1", "10", "10"};
    afterSortingElementsFourth = new String[]{"27", "28", "2", "10", "10", "1"};

    trueExpression = "'1'=='1'";
    falseExpression = "'0'=='1'";
  }

  public void testElementSorting()
  {
    assertNotNull(jsonSettingsDocument);

    MBElement element = jsonSettingsDocument.getElementsWithName("EXT-GebruikersInstellingenGetResult").get(0)
        .getElementsWithName("Koersenmenuoptions").get(0);
    assertNotNull(element);

    List<MBElement> sortingElements = element.getElementsWithName("EXTGebruikersInstellingenGetReplyKoersenmenuoptionsKoersenmenuoption");

    // Check elements before sorting
    for (int i = 0; i < sortingElements.size(); i++)
    {
      assertEquals(beforeSortingElements[i], sortingElements.get(i).getValueForAttribute("Fondsenlijstnummer"));
    }

    // Sort the elements
    element.sortElements("EXTGebruikersInstellingenGetReplyKoersenmenuoptionsKoersenmenuoption", "-Naam, +Fondsenlijstnummer");
    sortingElements = element.getElementsWithName("EXTGebruikersInstellingenGetReplyKoersenmenuoptionsKoersenmenuoption");

    // Check items after having sorted them
    for (int i = 0; i < sortingElements.size(); i++)
    {
      assertEquals(afterSortingElements[i], sortingElements.get(i).getValueForAttribute("Fondsenlijstnummer"));
    }

    // Sort them another time
    element.sortElements("EXTGebruikersInstellingenGetReplyKoersenmenuoptionsKoersenmenuoption",
                         "+Naam, -Fondsenlijstnummer, +HoofdfondstypeNr");
    sortingElements = element.getElementsWithName("EXTGebruikersInstellingenGetReplyKoersenmenuoptionsKoersenmenuoption");

    // Check items after having sorted them for a second time
    for (int i = 0; i < sortingElements.size(); i++)
    {
      assertEquals(afterSortingElementsNext[i], sortingElements.get(i).getValueForAttribute("Fondsenlijstnummer"));
    }

    // Sort them for the third time
    element.sortElements("EXTGebruikersInstellingenGetReplyKoersenmenuoptionsKoersenmenuoption",
                         "-Naam, +Fondsenlijstnummer, -HoofdfondstypeNr");
    sortingElements = element.getElementsWithName("EXTGebruikersInstellingenGetReplyKoersenmenuoptionsKoersenmenuoption");

    // Check items after having sorted them for a second time
    for (int i = 0; i < sortingElements.size(); i++)
    {
      assertEquals(afterSortingElementsThird[i], sortingElements.get(i).getValueForAttribute("Fondsenlijstnummer"));
    }

    // Sort them for the fourth and last time
    element.sortElements("EXTGebruikersInstellingenGetReplyKoersenmenuoptionsKoersenmenuoption",
                         "-Naam, -Fondsenlijstnummer, -HoofdfondstypeNr");
    sortingElements = element.getElementsWithName("EXTGebruikersInstellingenGetReplyKoersenmenuoptionsKoersenmenuoption");

    // Check items after having sorted them for a second time
    for (int i = 0; i < sortingElements.size(); i++)
    {
      assertEquals(afterSortingElementsFourth[i], sortingElements.get(i).getValueForAttribute("Fondsenlijstnummer"));
    }
  }

  public void testExpression()
  {
    assertNotNull(jsonSettingsDocument);

    assertEquals("true", jsonSettingsDocument.evaluateExpression(trueExpression));
    assertEquals("false", jsonSettingsDocument.evaluateExpression(falseExpression));
  }

  public void testChildren()
  {
    assertNotNull(xmlSettingsDocument);

    msg(xmlSettingsDocument.getName());

  }

  public void testAddElementWithIndex()
  {
    MBDocumentDefinition docDef = config.getDefinitionForDocumentName("MBGenericRequest");

    MBDocument doc = new MBDocument(docDef);
    assertNotNull(doc);

    MBElementDefinition elementDef = docDef.getElementWithPath("/Request");
    MBElement requestElement = new MBElement(elementDef);
    doc.addElement(requestElement);

    MBElementDefinition paramDef = elementDef.getChildWithName("Parameter");

    MBElement param1Element = new MBElement(paramDef);
    param1Element.setAttributeValue("param1", "key");
    requestElement.addElement(param1Element);

    MBElement param2Element = new MBElement(paramDef);
    param2Element.setAttributeValue("param2", "key");
    requestElement.addElement(param2Element);

    MBElement param3Element = requestElement.createElement("Parameter");
    param3Element.setAttributeValue("param3", "key");

    String beforeParam1Value = doc.getValueForPath("/Request[0]/Parameter[0]/@key");
    String beforeParam2Value = doc.getValueForPath("/Request[0]/Parameter[1]/@key");
    String beforeParam3Value = doc.getValueForPath("/Request[0]/Parameter[2]/@key");

    assertEquals("param1", beforeParam1Value);
    assertEquals("param2", beforeParam2Value);
    assertEquals("param3", beforeParam3Value);

    // until now, we tested the normal behavior of a document and adding elements to it. Now comes the real test

    MBElement param4Element = new MBElement(paramDef);
    param4Element.setAttributeValue("param4", "key");
    requestElement.addElement(param4Element, 0);

    MBElement param5Element = requestElement.createElement("Parameter", 3);
    param5Element.setAttributeValue("param5", "key");

    doc.clearPathCache();

    String afterParam1Value = doc.getValueForPath("/Request[0]/Parameter[1]/@key");
    String afterParam2Value = doc.getValueForPath("/Request[0]/Parameter[2]/@key");
    String afterParam3Value = doc.getValueForPath("/Request[0]/Parameter[4]/@key");
    String afterParam4Value = doc.getValueForPath("/Request[0]/Parameter[0]/@key");
    String afterParam5Value = doc.getValueForPath("/Request[0]/Parameter[3]/@key");

    assertEquals("param1", afterParam1Value);
    assertEquals("param2", afterParam2Value);
    assertEquals("param3", afterParam3Value);
    assertEquals("param4", afterParam4Value);
    assertEquals("param5", afterParam5Value);

    MBElement invalidElement = new MBElement(paramDef);
    invalidElement.setAttributeValue("invalid", "key");

    try
    {
      requestElement.addElement(param4Element, -1);

      fail("Add element at position -1 should throw an exception");
    }
    catch (MBInvalidPathException e)
    {
      msg("This is correct behavior");
    }

    try
    {
      requestElement.addElement(param4Element, -10000);

      fail("Add element at position -1000 should throw an exception");
    }
    catch (MBInvalidPathException e)
    {
      msg("This is correct behavior");
    }

    try
    {
      requestElement.addElement(param4Element, 50000);

      fail("Add element at position 50000 should throw an exception");
    }
    catch (MBInvalidPathException e)
    {
      msg("This is correct behavior");
    }

    try
    {
      requestElement.createElement("Parameter", -1);

      fail("Create element at position -1 should throw an exception");
    }
    catch (MBInvalidPathException e)
    {
      msg("This is correct behavior");
    }

    try
    {
      requestElement.createElement("Parameter", -1000);

      fail("Create element at position -1000 should throw an exception");
    }
    catch (MBInvalidPathException e)
    {
      msg("This is correct behavior");
    }

    try
    {
      requestElement.createElement("Parameter", 50000);

      fail("Create element at position 50000 should throw an exception");
    }
    catch (MBInvalidPathException e)
    {
      msg("This is correct behavior");
    }
  }

  private void msg(String message)
  {
    Log.i("TESTING", message);
  }

}
