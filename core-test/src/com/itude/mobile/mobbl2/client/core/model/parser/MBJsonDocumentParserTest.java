/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl2.client.core.model.parser;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.AssetUtil;
import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.mobbl2.client.core.MBApplicationCore;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBMvcConfigurationParser;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

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
    MBDocumentDefinition docDef = config.getDefinitionForDocumentName("Books");

    assertNotNull(docDef);
    MBJsonDocumentParser parser = new MBJsonDocumentParser();
    MBDocument document = parser.getDocumentWithData(documentData, docDef);
    assertNotNull(document);

  }

  public void testJSonParsingWithString()
  {

  }

}
