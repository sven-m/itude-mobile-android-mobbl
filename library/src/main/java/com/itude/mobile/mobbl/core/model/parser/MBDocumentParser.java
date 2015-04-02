package com.itude.mobile.mobbl.core.model.parser;

import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;

/**
 * Default interface for all Document parsers
 */
public interface MBDocumentParser {
    /**
     * Parsers the byte array according to the {@link MBDocumentDefinition}
     *
     * @param data       byte array
     * @param definition {@link MBDocumentDefinition}
     * @return {@link MBDocument}
     */
    public MBDocument getDocumentWithData(byte[] data, MBDocumentDefinition definition);

}
