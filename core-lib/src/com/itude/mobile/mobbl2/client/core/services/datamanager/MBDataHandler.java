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
package com.itude.mobile.mobbl2.client.core.services.datamanager;

import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public interface MBDataHandler
{
  public MBDocument loadDocument(String documentName);

  public MBDocument loadFreshDocument(String documentName);

  public MBDocument loadDocument(String documentName, MBDocument args, MBEndPointDefinition endPointDefenition);

  public MBDocument loadFreshDocument(String documentName, MBDocument args, MBEndPointDefinition endPointDefenition);

  public MBDocument loadDocument(String documentName, MBDocument args, String parser, MBEndPointDefinition endPoint);

  public MBDocument loadFreshDocument(String documentName, MBDocument args, String parser, MBEndPointDefinition endPoint);

  public MBDocument loadDocument(String documentName, String parser);

  public MBDocument loadFreshDocument(String documentName, String parser);

  public void storeDocument(MBDocument document);

  public MBDocument loadDocument(String documentName, MBDocument args, MBEndPointDefinition endPointDefenition, String documentParser);

  public MBDocument loadFreshDocument(String documentName, MBDocument args, MBEndPointDefinition endPointDefenition, String documentParser);
}
