package com.itude.mobile.mobbl.core.model.parser;

import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;

public interface MBDocumentParser
{

  public MBDocument getDocumentWithData(byte[] data, MBDocumentDefinition definition);

}
