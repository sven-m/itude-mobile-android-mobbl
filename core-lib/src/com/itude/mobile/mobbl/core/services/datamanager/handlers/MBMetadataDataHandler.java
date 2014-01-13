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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.itude.mobile.mobbl.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBElement;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.datamanager.MBDataHandlerBase;

public class MBMetadataDataHandler extends MBDataHandlerBase
{

  public static final String            DIALOGS_DOCUMENT = "MBDialogs";
  private final Map<String, MBDocument> _cache           = new ConcurrentHashMap<String, MBDocument>();

  @Override
  public MBDocument loadDocument(String documentName)
  {
    // don't cache dialogs
    if (documentName.equals(DIALOGS_DOCUMENT)) return loadDialogs();
    if (!_cache.containsKey(documentName)) _cache.put(documentName, loadFreshDocument(documentName));
    return _cache.get(documentName);
  }

  @Override
  public MBDocument loadFreshDocument(String documentName)
  {
    if (documentName.equals(DIALOGS_DOCUMENT)) return loadDialogs();
    else return null;
  }

  private MBDocument loadDialogs()
  {
    MBDocumentDefinition docDef = MBMetadataService.getInstance().getDefinitionForDocumentName(DIALOGS_DOCUMENT);
    MBDocument doc = new MBDocument(docDef);
    MBMetadataService service = MBMetadataService.getInstance();
    for (MBDialogDefinition def : service.getDialogs())
      if (def.isShowAsDocument() && def.isPreConditionValid())
      {
        MBElement element = new MBElement(docDef.getElementWithPath("/Dialog"));
        element.setAttributeValue(def.getName(), "name");
        element.setAttributeValue(def.getMode(), "mode");
        element.setAttributeValue(def.getIcon(), "icon");
        element.setAttributeValue(def.getShowAs(), "showAs");
        element.setAttributeValue(def.getTitle(), "title");
        doc.addElement(element);
      }
    return doc;

  }
}
