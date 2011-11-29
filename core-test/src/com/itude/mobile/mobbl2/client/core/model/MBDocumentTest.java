package com.itude.mobile.mobbl2.client.core.model;

import java.util.List;

import android.test.ApplicationTestCase;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBMvcConfigurationParser;
import com.itude.mobile.mobbl2.client.core.util.AssetUtil;
import com.itude.mobile.mobbl2.client.core.util.DataUtil;

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

  private void msg(String message)
  {
    Log.i("TESTING", message);
  }

}
