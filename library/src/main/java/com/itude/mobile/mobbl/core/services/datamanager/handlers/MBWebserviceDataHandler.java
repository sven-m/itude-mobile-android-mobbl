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

import com.itude.mobile.mobbl.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.datamanager.MBDataHandlerBase;

/**
 * Retrieves and sends MBDocument instances to and from a webservice.
 * <br/>
 * The MBWebserviceDataHandler is the top level in the DataHandlers for HTTP network communication.
 * Default behavior is to process an MBDocument, add the result to the request body and perform an HTTP operation (POST/GET).
 * <p/>
 * The endpoints.xml file maps Document names to Webservice URL's together with caching and timeout information.
 * The response body is parsed and validated against the Document Definition.
 * <p/>
 * The response can be handled by a ResultListener, also defined in the endpoints.xml file. Matching is by regex, so errors can be flexibly handled.
 * <p/>
 * Override this class to influence behavior. There are a bunch of template methods for easily changing HTTP headers, HTTP method etc.
 * <p/>
 * Caching is configurable and automatic. The cache key is based on the document name and arguments. For REST webservices the operation name is one of the arguments.
 */
public abstract class MBWebserviceDataHandler extends MBDataHandlerBase {
    @Override
    public MBDocument loadDocument(String documentName) {
        return loadDocument(documentName, (MBDocument) null, null);
    }

    //
    @Override
    public MBDocument loadFreshDocument(String documentName) {
        return loadFreshDocument(documentName, (MBDocument) null, null);
    }

    @Override
    public MBDocument loadFreshDocument(String documentName, MBDocument doc, MBEndPointDefinition endPointDefenition) {
        return doLoadDocument(documentName, doc);
    }

    @Override
    public MBDocument loadDocument(String documentName, MBDocument doc, MBEndPointDefinition endPointDefenition) {
        return doLoadDocument(documentName, doc);
    }

    protected abstract MBDocument doLoadDocument(String documentName, MBDocument doc);

    @Override
    public void storeDocument(MBDocument document) {
    }

    public MBEndPointDefinition getEndPointForDocument(String name) {
        return MBMetadataService.getInstance().getEndpointForDocumentName(name);
    }
}
