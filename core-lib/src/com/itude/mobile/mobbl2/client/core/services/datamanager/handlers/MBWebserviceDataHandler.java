package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.datamanager.MBDataHandlerBase;
import com.itude.mobile.mobbl2.client.core.util.MBCacheManager;

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
    MBEndPointDefinition endPoint = endPointDefenition != null ? endPointDefenition : getEndPointForDocument(documentName);

    String key = doc == null ? documentName : documentName + doc.getUniqueId();

    MBDocument result = doLoadDocument(documentName, doc);

    if (endPoint.getCacheable())
    {
      MBCacheManager.setDocument(result, key, endPoint.getTtl());
    }

    return result;
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument doc, MBEndPointDefinition endPointDefenition)
  {
    MBEndPointDefinition endPoint = endPointDefenition != null ? endPointDefenition : getEndPointForDocument(documentName);

    boolean cacheable = endPoint.getCacheable();

    String key = null;

    if (cacheable)
    {
      key = doc == null ? documentName : documentName + doc.getUniqueId();

      MBDocument result = MBCacheManager.documentForKey(key);
      if (result != null)
      {
        return result;
      }
    }

    MBDocument result = doLoadDocument(documentName, doc);

    if (cacheable)
    {
      MBCacheManager.setDocument(result, key, endPoint.getTtl());
    }

    return result;
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
