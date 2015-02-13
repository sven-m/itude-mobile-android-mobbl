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
package com.itude.mobile.mobbl.core.services.datamanager.handlers;

import java.util.Hashtable;
import java.util.Map;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.mobbl.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBConfigurationDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBDocumentFactory;
import com.itude.mobile.mobbl.core.model.MBElement;
import com.itude.mobile.mobbl.core.model.parser.MBXmlDocumentParser;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.datamanager.MBDataHandlerBase;

/**
 * Retrieves and stores MBDocument instances on the current system
 */
public class MBSystemDataHandler extends MBDataHandlerBase
{
  private final Map<String, MBDocument> _dictionary;

  private String                        _fileName = "applicationproperties.xml";

  public MBSystemDataHandler()
  {
    super();
    _dictionary = new Hashtable<String, MBDocument>();
    initDocuments();
  }

  // overloaded method to use different applicationproperties files
  public MBSystemDataHandler(String fileName)
  {
    super();
    _dictionary = new Hashtable<String, MBDocument>();
    _fileName = fileName;
    initDocuments();
  }

  public String getFileName()
  {
    return _fileName;
  }

  public void setFileName(String fileName)
  {
    _fileName = fileName;
  }

  public void setSystemProperty(String name, String value, MBDocument doc)
  {
    MBElement prop = doc.createElementWithName("/System[0]/Property");
    prop.setAttributeValue(name, "name");
    prop.setAttributeValue(value, "value");
  }

  public void initDocuments()
  {
    MBDocumentDefinition docDef = MBMetadataService.getInstance()
        .getDefinitionForDocumentName(MBConfigurationDefinition.DOC_SYSTEM_PROPERTIES);
    MBDocument doc = docDef.createDocument();

    setSystemProperty("platform", "Android", doc);

    byte[] data = DataUtil.getInstance().readFromAssetOrFile(_fileName);

    MBXmlDocumentParser.parseFragment(data, doc, "/Application[0]", false);
    _dictionary.put(MBConfigurationDefinition.DOC_SYSTEM_PROPERTIES, doc);
  }

  @Override
  public MBDocument loadDocument(String documentName)
  {
    MBDocument doc = _dictionary.get(documentName);
    if (doc == null)
    {
      // Not yet in the store; handle default construction of the document using a file as template
      String fileName = documentName + ".xml";
      byte[] data = DataUtil.getInstance().readFromAssetOrFile(fileName);
      MBDocumentDefinition docDef = MBMetadataService.getInstance().getDefinitionForDocumentName(documentName);
      return MBDocumentFactory.getInstance().getDocumentWithData(data, MBDocumentFactory.PARSER_XML, docDef);
    }
    return doc;
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument args, MBEndPointDefinition endPoint)
  {
    return loadDocument(documentName);
  }

  @Override
  public void storeDocument(MBDocument document)
  {
    _dictionary.put(document.getName(), document);
  }

}
