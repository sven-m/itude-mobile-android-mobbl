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
