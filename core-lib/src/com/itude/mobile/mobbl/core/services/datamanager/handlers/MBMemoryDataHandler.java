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

import android.util.Log;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.android.util.exceptions.DataParsingException;
import com.itude.mobile.mobbl.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBDocumentFactory;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.datamanager.MBDataHandlerBase;
import com.itude.mobile.mobbl.core.util.Constants;

/**
 * Retrieves and stores MBDocument instances in memory only
 */
public class MBMemoryDataHandler extends MBDataHandlerBase
{
  private final Map<String, MBDocument> _dictionary;

  public MBMemoryDataHandler()
  {
    super();
    _dictionary = new Hashtable<String, MBDocument>();
  }

  @Override
  public MBDocument loadDocument(String documentName)
  {
    MBDocument doc = _dictionary.get(documentName);
    if (doc == null)
    {
      // Not yet in the store; handle default construction of the document using a file as template
      String fileName = "documents/" + documentName + ".xml";
      byte[] data = null;
      try
      {
        data = DataUtil.getInstance().readFromAssetOrFile(fileName);
      }
      catch (DataParsingException e)
      {
        Log.d(Constants.APPLICATION_NAME, "Unable to find file " + fileName + " in assets");
      }
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

  @Override
  public MBDocument loadFreshDocument(String documentName)
  {

    if (_dictionary.containsKey(documentName))
    {
      _dictionary.remove(documentName);
    }

    return loadDocument(documentName);
  }

}
