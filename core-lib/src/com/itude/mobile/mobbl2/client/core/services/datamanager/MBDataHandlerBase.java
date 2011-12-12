package com.itude.mobile.mobbl2.client.core.services.datamanager;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBDataHandlerBase implements MBDataHandler
{

  @Override
  public MBDocument loadDocument(String documentName)
  {
    Log.w(Constants.APPLICATION_NAME, "MBDataHandlerBase: No loadDocument implementation for " + documentName);
    return null;
  }

  /**
   * Load document with specified name using a specific parser eg. JSON or XML.
   * Use static variables from MBDocumentFactory to specify the parser.
   * @param documentName
   * @param parser
   * @return
   */
  @Override
  public MBDocument loadDocument(String documentName, String parser)
  {
    return loadDocument(documentName);
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument args)
  {
    Log.w(Constants.APPLICATION_NAME, "MBDataHandlerBase: No loadDocument implementation for " + documentName);
    return null;
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument args, String parser)
  {
    return loadDocument(documentName, args);
  }

  @Override
  public void storeDocument(MBDocument document)
  {
    Log.w(Constants.APPLICATION_NAME, "MBDataHandlerBase: No storeDocument implementation for " + document.getDefinition().getName());
  }

}
