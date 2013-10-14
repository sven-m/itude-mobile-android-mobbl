/*
 * (C) Copyright ItudeMobile.
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
package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import android.util.Log;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.android.util.FileUtil;
import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBDocumentFactory;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.datamanager.MBDataHandlerBase;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBFileDataHandler extends MBDataHandlerBase
{

  @Override
  public MBDocument loadDocument(String documentName)
  {
    return loadDocument(documentName, "");
  }

  @Override
  public MBDocument loadDocument(String documentName, String parser)
  {
    Log.d(Constants.APPLICATION_NAME, "MBFileDataHandler.loadDocument: " + documentName);
    String fileName = determineFileName(documentName, parser);
    MBDocumentDefinition docDef = MBMetadataService.getInstance().getDefinitionForDocumentName(documentName);
    byte[] data = DataUtil.getInstance().readFromAssetOrFile(fileName);

    if (data == null)
    {
      return null;
    }
    else
    {
      // User XML parser as a default
      if (StringUtil.isNotEmpty(parser))
      {
        return MBDocumentFactory.getInstance().getDocumentWithData(data, parser, docDef);
      }

      return MBDocumentFactory.getInstance().getDocumentWithData(data, MBDocumentFactory.PARSER_XML, docDef);
    }
  }

  @Override
  public void storeDocument(MBDocument document)
  {

    if (document != null)
    {
      String fileName = determineFileName(document.getName());
      StringBuffer sb = new StringBuffer(4096);
      String xml = document.asXmlWithLevel(sb, 0, false).toString();// TODO, set the last parameter to true if we want to properly escape the document to be stored

      Log.d(Constants.APPLICATION_NAME, "Writing document " + document.getName() + " to " + fileName);

      try
      {
        // TODO: parameterize character encoding.
        FileUtil.getInstance().writeToFile(xml.getBytes(), fileName, "UTF-8");
      }
      catch (Exception e)
      {
        Log.w(Constants.APPLICATION_NAME, "MBFileDataHandler.storeDocument: Error writing document " + document.getName() + " to "
                                          + fileName, e);
      }
    }
  }

  private String determineFileName(String documentName)
  {
    return determineFileName(documentName, null);
  }

  private String determineFileName(String documentName, String documentParser)
  {
    if (MBDocumentFactory.PARSER_JSON.equals(documentParser))
    {
      return "documents/" + documentName + ".json";
    }

    return "documents/" + documentName + ".xml";
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument args, MBEndPointDefinition endPoint)
  {
    return loadDocument(documentName);
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument args, String parser, MBEndPointDefinition endPoint)
  {
    return loadDocument(documentName, parser);
  }
}
