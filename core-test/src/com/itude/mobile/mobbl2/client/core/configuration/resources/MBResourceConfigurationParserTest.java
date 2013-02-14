package com.itude.mobile.mobbl2.client.core.configuration.resources;

import java.util.ArrayList;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.AssetUtil;
import com.itude.mobile.android.util.FileUtil;
import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBBundleDefinition;

public class MBResourceConfigurationParserTest extends ApplicationTestCase<MBApplicationCore>
{

  private MBResourceConfigurationParser parser;

  private final String[]                resourceIds    = {"config", "endpoints", "ICON-tab_home", "ICON-tab_shares"};

  private final String[]                resourceValues = {"file://config.xml", "file://endpoints.xml", "file://ic_menu_home.png",
      "file://ic_menu_shares.png"                      };
  private final String[]                bundleIds      = {"nl", "fr"};
  private final String[]                bundleValues   = {"file://texts-nl.xml", "file://texts-nl.xml"};

  private byte[]                        data;

  public MBResourceConfigurationParserTest()
  {
    super(MBApplicationCore.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    parser = new MBResourceConfigurationParser();

    ArrayList<String> _resourceAttributes = new ArrayList<String>();
    _resourceAttributes.add("xmlns");
    _resourceAttributes.add("id");
    _resourceAttributes.add("url");
    _resourceAttributes.add("cacheable");
    _resourceAttributes.add("ttl");
    parser.setResourceAttributes(_resourceAttributes);

    ArrayList<String> _bundleAttributes = new ArrayList<String>();
    _bundleAttributes.add("xmlns");
    _bundleAttributes.add("languageCode");
    _bundleAttributes.add("url");
    parser.setBundleAttributes(_bundleAttributes);

    createApplication();
    FileUtil.getInstance().setContext(getContext());
    data = AssetUtil.getInstance().getByteArray("unittests/testresources.xml");
  }

  public void testResourceParsing()
  {
    MBResourceConfiguration configuration = (MBResourceConfiguration) parser.parseData(data, "Resources");

    // Test resources
    for (int i = 0; i < resourceIds.length; i++)
    {
      MBResourceDefinition def = configuration.getResourceWithID(resourceIds[i]);
      assertEquals(resourceIds[i], def.getResourceId());
      assertEquals(resourceValues[i], def.getUrl());

      // TODO add some test to check cacheable and ttl 
    }

  }

  public void testBundleParsing()
  {
    // TODO create tests for bundles with same language

    MBResourceConfiguration configuration = (MBResourceConfiguration) parser.parseData(data, "Resources");

    // Test bundles
    for (int j = 0; j < bundleIds.length; j++)
    {
      ArrayList<MBBundleDefinition> bundles = (ArrayList<MBBundleDefinition>) configuration.getBundlesForLanguageCode(bundleIds[j]);
      MBBundleDefinition bundle = bundles.get(0);
      assertEquals(bundleIds[j], bundle.getLanguageCode());
      assertEquals(bundleValues[j], bundle.getUrl());
    }
  }

}
