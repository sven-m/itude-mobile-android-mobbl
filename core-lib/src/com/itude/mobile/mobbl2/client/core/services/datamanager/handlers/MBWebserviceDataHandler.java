package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import com.itude.mobile.mobbl2.client.core.configuration.webservices.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.webservices.MBWebservicesConfiguration;
import com.itude.mobile.mobbl2.client.core.configuration.webservices.MBWebservicesConfigurationParser;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.datamanager.MBDataHandlerBase;
import com.itude.mobile.mobbl2.client.core.util.AssetUtil;
import com.itude.mobile.mobbl2.client.core.util.DataUtil;

public class MBWebserviceDataHandler extends MBDataHandlerBase
{
  private final MBWebservicesConfiguration _webServiceConfiguration;

  public MBWebserviceDataHandler()
  {
    MBWebservicesConfigurationParser parser = new MBWebservicesConfigurationParser();
    String documentName = MBMetadataService.getEndpointsName();
    byte[] data = DataUtil.getInstance().readFromAssetOrFile(documentName);
    _webServiceConfiguration = (MBWebservicesConfiguration) parser.parseData(data, documentName);
  }

  @Override
  public MBDocument loadDocument(String documentName)
  {
    MBDocument result = loadDocument(documentName, null);
    // TODO: check if networkactivity indicator needs to be explicitly managed
    // iPhone code: [[NSNotificationCenter defaultCenter] postNotificationName:@"NetworkActivity" object: nil];
    return result;
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument args)
  {
    return null;
  }

  @Override
  public void storeDocument(MBDocument document)
  {
  }

  public MBEndPointDefinition getEndPointForDocument(String name)
  {
    return _webServiceConfiguration.getEndPointForDocumentName(name);
  }

}
