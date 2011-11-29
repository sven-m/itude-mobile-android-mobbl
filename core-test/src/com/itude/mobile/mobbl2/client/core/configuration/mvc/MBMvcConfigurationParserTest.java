package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import java.util.ArrayList;
import java.util.Iterator;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.util.AssetUtil;

public class MBMvcConfigurationParserTest extends ApplicationTestCase<MBApplicationCore>
{

  private MBMvcConfigurationParser  parser;

  private byte[]                    data;

  private MBConfigurationDefinition config;

  public MBMvcConfigurationParserTest()
  {
    super(MBApplicationCore.class);

  }

  @Override
  protected void setUp() throws Exception
  {
    parser = new MBMvcConfigurationParser();

    if (config == null)
    {
      AssetUtil.getInstance().setContext(getContext());
      data = AssetUtil.getInstance().getByteArray("unittests/config_unittests.xml");
      config = (MBConfigurationDefinition) parser.parseData(data, "Config");
    }
  }

  public void testConfigParsing()
  {
    assertNotNull(config);
  }

  public void testDocumentsElementsAndAttributes()
  {
    assertNotNull(config);

    MBDocumentDefinition docDef = config.getDefinitionForDocumentName("EXT-GebruikersInstellingenGetResponse");
    assertNotNull(docDef);

    MBElementDefinition resultDef = docDef.getChildWithName("EXT-GebruikersInstellingenGetResult");
    assertNotNull(resultDef);

    String[] resultChildren = resultDef.getChildElementNames().split(",");
    for (int i = 0; i < resultChildren.length; i++)
    {
      MBElementDefinition childDef = resultDef.getChildren().get(i);
      assertNotNull(childDef);
      assertEquals(resultChildren[i].replaceAll(" ", ""), childDef.getName());

      if (childDef.getName().equals("Koersenmenuoptions"))
      {
        MBElementDefinition subChildDef = childDef.getChildren().get(0);
        assertNotNull(subChildDef);
        assertEquals("EXTGebruikersInstellingenGetReplyKoersenmenuoptionsKoersenmenuoption", subChildDef.getName());

        assertEquals(3, subChildDef.getAttributes().size());

        String[] attributes = subChildDef.getAttributeNames().split(",");

        for (int j = 0; j < attributes.length; j++)
        {
          MBAttributeDefinition attribute = subChildDef.getAttributeWithName(attributes[j].replaceAll(" ", ""));
          assertNotNull(attribute);
          assertEquals("string", attribute.getType());
          assertEquals(attributes[j].replaceAll(" ", ""), attribute.getName());
        }

      }

    }
  }

  public void testActions()
  {
    assertNotNull(config);

    Iterator<String> actions = config.getActions().keySet().iterator();
    while (actions.hasNext())
    {
      String action = actions.next();
      MBActionDefinition actionDef = config.getActions().get(action);
      assertNotNull(actionDef);
      assertEquals(action, actionDef.getClassName());
      assertEquals(action, actionDef.getName());
    }

  }

  public void testOutcomes()
  {
    assertNotNull(config);

    assertEquals(147, config.getOutcomes().size());

    ArrayList<MBOutcomeDefinition> outcomes1 = (ArrayList<MBOutcomeDefinition>) config
        .getOutcomeDefinitionsForOrigin("PAGE-tab_my_account", "OUTCOME-page_my_transactions");
    assertNotNull(outcomes1);
    assertEquals(2, outcomes1.size());

    for (int i = 0; i < outcomes1.size(); i++)
    {
      MBOutcomeDefinition outDef = outcomes1.get(i);
      assertNotNull(outDef);
      assertEquals("PAGE-tab_my_account", outDef.getOrigin());
      assertEquals("OUTCOME-page_my_transactions", outDef.getName());

      if (i == 0)
      {
        assertEquals("${SessionState:Session[0]/@loggedIn}", outDef.getPreCondition());
        assertEquals("PortfolioTransactionOverviewAction", outDef.getAction());
      }
      else
      {
        assertEquals("!${SessionState:Session[0]/@loggedIn}", outDef.getPreCondition());
        assertEquals("PAGE-page_login", outDef.getAction());
        assertEquals("MODAL", outDef.getDisplayMode());
      }

    }

    for (int j = 0; j < config.getOutcomes().size(); j++)
    {
      MBOutcomeDefinition def = config.getOutcomes().get(j);
      assertNotNull(def);

      if (def.getOrigin().equals("*") && def.getName().equals("OUTCOME-page_brochure_thanks")
          && def.getAction().equals("PAGE-page_brochure_thanks"))
      {
        assertEquals(true, def.getNoBackgroundProcessing());
      }

      if (def.getOrigin().equals("PAGE-tab_my_account") && def.getName().equals("OUTCOME-page_my_transactions")
          && def.getAction().equals("PortfolioTransactionOverviewAction"))
      {
        assertEquals(false, def.getNoBackgroundProcessing());
      }

    }

  }

  public void testDialogs()
  {
    assertNotNull(config);

    assertEquals(6, config.getDialogs().size());

    Iterator<MBDialogDefinition> iterator = config.getDialogs().values().iterator();

    int i = 0;
    while (iterator.hasNext())
    {
      MBDialogDefinition dialogDef = iterator.next();
      assertNotNull(dialogDef);
      assertEquals("STACK", dialogDef.getMode());

      if (i == 0)
      {
        assertEquals("DIALOG-tab_my_account", dialogDef.getName());
        assertEquals("ICON-tab_my_account", dialogDef.getIcon());
        assertEquals("Mijn rekening", dialogDef.getTitle());
      }

      MBDialogDefinition testDef = config.getDefinitionForDialogName(dialogDef.getName());
      assertNotNull(testDef);
      assertEquals(dialogDef, testDef);

      i++;
    }

  }

  public void testPages()
  {
    assertNotNull(config);

    assertNotNull(config.getPages());
    assertEquals(87, config.getPages().size());

    MBPageDefinition page = config.getDefinitionForPageName("PAGE-page_info");
    assertNotNull(page);
    assertEquals("EXT-NieuwsBelangrijkeBerichtenGetResponse", page.getDocumentName());
    assertEquals("Info", page.getTitle());
    assertEquals(1, page.getChildren().size());

    MBPanelDefinition panel = (MBPanelDefinition) page.getChildren().get(0);
    assertNotNull(panel);
    assertEquals(2, panel.getChildren().size());

    MBPanelDefinition subPanel = (MBPanelDefinition) panel.getChildren().get(0);
    assertNotNull(subPanel);
    assertEquals("SECTION", subPanel.getType());
    assertEquals("${SessionState:Session[0]/@loggedIn}", subPanel.getPreCondition());
    assertEquals("Belangrijke berichten", subPanel.getTitle());

    MBForEachDefinition forEach = (MBForEachDefinition) ((MBPanelDefinition) subPanel.getChildren().get(0)).getChildren().get(0);
    assertNotNull(forEach);
    assertEquals("/EXT-NieuwsBelangrijkeBerichtenGetResult[0]/Berichten[0]/EXTNieuwsBelangrijkeBerichtenGetReplyBerichtenBericht", forEach
        .getValue());

    MBPanelDefinition forEachSubPanel = (MBPanelDefinition) forEach.getChildren().get(0);
    assertNotNull(forEachSubPanel);
    assertEquals(3, forEachSubPanel.getChildren().size());

    MBFieldDefinition field1 = (MBFieldDefinition) forEachSubPanel.getChildren().get(0);
    assertNotNull(field1);
    assertEquals("LABEL", field1.getDisplayType());
    assertEquals("@Kop", field1.getPath());

    MBFieldDefinition field2 = (MBFieldDefinition) forEachSubPanel.getChildren().get(1);
    assertNotNull(field2);
    assertEquals("SUBLABEL", field2.getDisplayType());
    assertEquals("@SubLabel", field2.getPath());

    MBFieldDefinition field3 = (MBFieldDefinition) forEachSubPanel.getChildren().get(2);
    assertNotNull(field3);
    assertEquals("BUTTON", field3.getDisplayType());
    assertEquals(".", field3.getPath());
    assertEquals("OUTCOME-page_info_important_messages", field3.getOutcomeName());
    assertEquals("NAVIGATION", field3.getStyle());

  }

  public void testVariable()
  {
    assertNotNull(config);

    MBVariableDefinition variable = (MBVariableDefinition) ((MBForEachDefinition) ((MBPanelDefinition) config
        .getDefinitionForPageName("PAGE-page_portfolio_orderdetail").getChildren().get(1)).getChildren().get(1)).getChildren().get(0);
    assertNotNull(variable);
    assertEquals("Historyposition", variable.getName());
    assertEquals("currentPath()", variable.getExpression());

  }

  public void testDomainAndDomainValidators()
  {
    assertNotNull(config);

    assertEquals(17, config.getDomains().size());

    Iterator<MBDomainDefinition> iterator = config.getDomains().values().iterator();

    while (iterator.hasNext())
    {
      MBDomainDefinition domDef = iterator.next();
      assertNotNull(domDef);
      assertEquals(domDef, config.getDefinitionForDomainName(domDef.getName()));
    }

    MBDomainDefinition months = config.getDefinitionForDomainName("list_portfolio_orders_search_month");
    assertNotNull(months);
    assertEquals("list_portfolio_orders_search_month", months.getName());
    assertEquals("string", months.getType());
    assertEquals(12, months.getDomainValidators().size());

    String[] monthValues = {"Januari", "Februari", "Maart", "April", "Mei", "Juni", "Juli", "Augustus", "September", "Oktober", "November",
        "December"};
    for (int i = 0; i < months.getDomainValidators().size(); i++)
    {
      MBDomainValidatorDefinition domValDef = months.getDomainValidators().get(i);
      assertNotNull(domValDef);
      assertEquals(monthValues[i], domValDef.getTitle());
      assertEquals(Integer.toString((i + 1)), domValDef.getValue());
    }

  }

}
