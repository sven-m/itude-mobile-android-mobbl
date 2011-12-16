package com.itude.mobile.mobbl2.client.core.services.datamanager;

import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public interface MBDataHandler
{
  public MBDocument loadDocument(String documentName);

  public MBDocument loadDocument(String documentName, MBDocument args, MBEndPointDefinition endPointDefenition);

  public MBDocument loadDocument(String documentName, MBDocument args, String parser, MBEndPointDefinition endPoint);

  public MBDocument loadDocument(String documentName, String parser);

  public void storeDocument(MBDocument document);

  public MBDocument loadDocument(String documentName, MBDocument args, MBEndPointDefinition endPointDefenition, String documentParser);
}
