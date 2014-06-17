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
package com.itude.mobile.mobbl.core.model;

import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;

public class MBDocumentFactoryTest extends MBDocumentAbstractTest
{

  public void testJsonParsing()
  {
    assertNotNull(getJsonDocumentData());
    assertNotNull(getConfigData());
    assertNotNull(getConfig());

    MBDocumentDefinition docDef = getConfig().getDefinitionForDocumentName("Books");
    assertNotNull(docDef);

    MBDocument document = MBDocumentFactory.getInstance().getDocumentWithData(getJsonDocumentData(), MBDocumentFactory.PARSER_JSON, docDef);
    assertNotNull(document);
  }

  public void testXMLParsing()
  {
    assertNotNull(getXmlDocumentData());
    assertNotNull(getConfigData());
    assertNotNull(getConfig());

    MBDocumentDefinition docDef = getConfig().getDefinitionForDocumentName("Books");
    assertNotNull(docDef);

    MBDocument document = MBDocumentFactory.getInstance().getDocumentWithData(getXmlDocumentData(), MBDocumentFactory.PARSER_XML, docDef);
    assertNotNull(document);
  }

}
