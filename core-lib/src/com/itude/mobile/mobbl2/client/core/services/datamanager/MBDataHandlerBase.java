package com.itude.mobile.mobbl2.client.core.services.datamanager;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
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

  @Override
  public MBDocument loadFreshDocument(String documentName)
  {
    Log.w(Constants.APPLICATION_NAME, "MBDataHandlerBase: No loadFreshDocument implementation for " + documentName);
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
  public MBDocument loadFreshDocument(String documentName, String parser)
  {
    return loadFreshDocument(documentName);
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument args, MBEndPointDefinition endPoint)
  {
    Log.w(Constants.APPLICATION_NAME, "MBDataHandlerBase: No loadDocument implementation for " + documentName);
    return null;
  }

  @Override
  public MBDocument loadFreshDocument(String documentName, MBDocument args, MBEndPointDefinition endPoint)
  {
    Log.w(Constants.APPLICATION_NAME, "MBDataHandlerBase: No loadFreshDocument implementation for " + documentName);
    return null;
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument args, String parser, MBEndPointDefinition endPoint)
  {
    return loadDocument(documentName, args, endPoint);
  }

  @Override
  public MBDocument loadFreshDocument(String documentName, MBDocument args, String parser, MBEndPointDefinition endPoint)
  {
    return loadFreshDocument(documentName, args, endPoint);
  }

  @Override
  public void storeDocument(MBDocument document)
  {
    Log.w(Constants.APPLICATION_NAME, "MBDataHandlerBase: No storeDocument implementation for " + document.getDefinition().getName());
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument args, MBEndPointDefinition endPointDefenition, String documentParser)
  {
    MBDocument result = null;
    if (args != null)
    {
      result = loadDocument(documentName, args, documentParser, endPointDefenition);
    }
    else
    {
      result = loadDocument(documentName, documentParser);
    }
    return result;
  }

  @Override
  public MBDocument loadFreshDocument(String documentName, MBDocument args, MBEndPointDefinition endPointDefenition, String documentParser)
  {
    MBDocument result = null;
    if (args != null)
    {
      result = loadFreshDocument(documentName, args, documentParser, endPointDefenition);
    }
    else
    {
      result = loadFreshDocument(documentName, documentParser);
    }
    return result;
  }

}
