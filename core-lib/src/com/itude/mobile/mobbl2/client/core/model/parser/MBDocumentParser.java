package com.itude.mobile.mobbl2.client.core.model.parser;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public interface MBDocumentParser
{

  public MBDocument getDocumentWithData(byte[] data, MBDocumentDefinition definition);

}
