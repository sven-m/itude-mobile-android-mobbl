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
package com.itude.mobile.mobbl.core.services.operation;

import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.datamanager.MBDataHandler;
import com.itude.mobile.mobbl.core.util.MBConstants;
import com.itude.mobile.mobbl.core.util.threads.MBThread;
import com.itude.mobile.mobbl.core.util.threads.exception.MBInterruptedException;

import java.util.Date;

/**
 * Class containing document operations.
 */
public class MBDocumentOperation extends MBThread {
    private MBDataHandler _dataHandler;
    private String _documentName;
    private MBDocument _document;
    private MBDocument _arguments;
    private MBEndPointDefinition _endPointDefinition;
    private MBDocumentOperationDelegate _delegate;
    private String _documentParser;
    private boolean _loadFreshCopy;

    public MBDataHandler getDataHandler() {
        return _dataHandler;
    }

    public void setDataHandler(MBDataHandler dataHandler) {
        _dataHandler = dataHandler;
    }

    public String getDocumentName() {
        return _documentName;
    }

    public void setDocumentName(String documentName) {
        _documentName = documentName;
    }

    public MBDocument getArguments() {
        return _arguments != null ? _arguments.clone() : null;
    }

    public void setArguments(MBDocument arguments) {
        _arguments = arguments;
    }

    public MBDocument getDocument() {
        return _document;
    }

    public void setDocument(MBDocument document) {
        _document = document;
    }

    public String getDocumentParser() {
        return _documentParser;
    }

    public void setDocumentParser(String documentParser) {
        _documentParser = documentParser;
    }

    public MBDocumentOperation(MBDataHandler dataHandler, MBDocument document) {
        super();
        _dataHandler = dataHandler;
        _document = document;
        _loadFreshCopy = false;
    }

    public MBDocumentOperation(MBDataHandler dataHandler, String documentName, MBDocument arguments) {
        super();
        _dataHandler = dataHandler;
        _documentName = documentName;
        _arguments = arguments;
        _loadFreshCopy = false;
    }

    public void setDelegate(MBDocumentOperationDelegate delegate) {
        _delegate = delegate;
    }

    public MBDocumentOperationDelegate getDelegate() {
        return _delegate;
    }

    public MBDocument load() {

        long now = new Date().getTime();

        MBDocument doc;
        if (_loadFreshCopy) {
            doc = getDataHandler().loadFreshDocument(getDocumentName(), getArguments(), getEndPointDefinition(), getDocumentParser());
        } else {
            doc = getDataHandler().loadDocument(getDocumentName(), getArguments(), getEndPointDefinition(), getDocumentParser());
        }

        if (doc == null) {
            MBDocumentDefinition docDef = MBMetadataService.getInstance().getDefinitionForDocumentName(getDocumentName());
            if (docDef.getAutoCreate()) {
                doc = docDef.createDocument();
            }
        }

    /* 
     * We can't assume that the autocreate boolean is true. 
     * So after previous if statement our document could still be null 
     */
        if (doc != null) {
            doc.setArgumentsUsed(getArguments());
        }
        MBLog.d(MBConstants.APPLICATION_NAME, "Loading of document " + getDocumentName() + " took " + (new Date().getTime() - now) / 1000
                + " seconds");
        return doc;
    }

    public void store() {
        getDataHandler().storeDocument(getDocument());
    }

    @Override
    public void runMethod() {
        try {
            checkForInterruption();
            if (_document == null) {
                MBDocument document = load();
                checkForInterruption();
                getDelegate().processResult(document);
            } else {
                store();
                checkForInterruption();
                getDelegate().processResult(getDocument());
            }
        } catch (Exception e) {
            if (e instanceof MBInterruptedException) {
                throw (MBInterruptedException) e;
            }

            MBLog.w(MBConstants.APPLICATION_NAME, "Exception during Document Operation: " + e.getMessage(), e);
            getDelegate().processException(e);
        }
    }

    public void setEndPointDefinition(MBEndPointDefinition endPointDefinition) {
        _endPointDefinition = endPointDefinition;
    }

    public MBEndPointDefinition getEndPointDefinition() {
        return _endPointDefinition;
    }

    public void setLoadFreshCopy(boolean loadFreshCopy) {
        _loadFreshCopy = loadFreshCopy;
    }

    public boolean isLoadFreshCopy() {
        return _loadFreshCopy;
    }
}
