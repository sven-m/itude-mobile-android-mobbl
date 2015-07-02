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

import com.itude.mobile.mobbl.core.configuration.mvc.MBDialogDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBElement;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.datamanager.MBDataHandlerBase;

/**
 * Retrieves and stores MBDocument instances as Meta data
 */
public class MBMetadataDataHandler extends MBDataHandlerBase {

    public static final String DIALOGS_DOCUMENT = "MBDialogs";

    @Override
    public MBDocument loadDocument(String documentName) {
        // don't cache dialogs
        if (documentName.equals(DIALOGS_DOCUMENT)) return loadDialogs();
        throw new IllegalArgumentException("documentName should be " + DIALOGS_DOCUMENT);
    }

    @Override
    public MBDocument loadFreshDocument(String documentName) {
        if (documentName.equals(DIALOGS_DOCUMENT)) return loadDialogs();
        throw new IllegalArgumentException("documentName should be " + DIALOGS_DOCUMENT);
    }

    private MBDocument loadDialogs() {
        MBMetadataService service = getMetaDataService();
        MBDocumentDefinition docDef = getDialogsDefinition(service);
        MBDocument doc = new MBDocument(docDef, MBDataManagerService.getInstance());
        for (MBDialogDefinition def : service.getDialogs())
            if (def.isShowAsDocument() && def.isPreConditionValid()) {
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

    private MBDocumentDefinition getDialogsDefinition(MBMetadataService metadataService) {
        return metadataService.getDefinitionForDocumentName(DIALOGS_DOCUMENT);
    }

    private MBMetadataService getMetaDataService() {
        return MBMetadataService.getInstance();
    }
}
