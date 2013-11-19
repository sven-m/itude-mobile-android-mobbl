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
package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.datamanager.MBDataHandlerBase;

public abstract class MBWebserviceDataHandler extends MBDataHandlerBase
{
  @Override
  public MBDocument loadDocument(String documentName)
  {
    return loadDocument(documentName, (MBDocument) null, null);
  }

  //
  @Override
  public MBDocument loadFreshDocument(String documentName)
  {
    return loadFreshDocument(documentName, (MBDocument) null, null);
  }

  @Override
  public MBDocument loadFreshDocument(String documentName, MBDocument doc, MBEndPointDefinition endPointDefenition)
  {
    return doLoadDocument(documentName, doc);
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument doc, MBEndPointDefinition endPointDefenition)
  {
    return doLoadDocument(documentName, doc);
  }

  protected abstract MBDocument doLoadDocument(String documentName, MBDocument doc);

  @Override
  public void storeDocument(MBDocument document)
  {
  }

  public MBEndPointDefinition getEndPointForDocument(String name)
  {
    return MBMetadataService.getInstance().getEndpointForDocumentName(name);
  }
}
