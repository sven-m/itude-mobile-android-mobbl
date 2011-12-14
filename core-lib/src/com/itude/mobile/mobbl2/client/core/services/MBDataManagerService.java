package com.itude.mobile.mobbl2.client.core.services;

import java.util.Hashtable;
import java.util.Map;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.datamanager.MBDataHandler;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBDocumentOperation;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBDocumentOperationDelegate;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBFileDataHandler;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBMemoryDataHandler;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBMobbl1ServerDataHandler;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBRESTServiceDataHandler;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBSystemDataHandler;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBNoDataManagerException;

public class MBDataManagerService
{

  public static final String               DATA_HANDLER_MEMORY   = "MBMemoryDataHandler";
  public static final String               DATA_HANDLER_FILE     = "MBFileDataHandler";
  public static final String               DATA_HANDLER_SYSTEM   = "MBSystemDataHandler";
  public static final String               DATA_HANDLER_WS_REST  = "MBRESTServiceDataHandler";
  public static final String               DATA_HANDLER_WS_MOBBL = "MBMobbl1ServerDataHandler";

  private static MBDataManagerService      _instance             = null;

  private final Map<String, MBDataHandler> _registeredHandlers;

  private MBDataManagerService()
  {
    _registeredHandlers = new Hashtable<String, MBDataHandler>();

    registerDataHandler(new MBFileDataHandler(), DATA_HANDLER_FILE);
    registerDataHandler(new MBSystemDataHandler(), DATA_HANDLER_SYSTEM);
    registerDataHandler(new MBMemoryDataHandler(), DATA_HANDLER_MEMORY);
    registerDataHandler(new MBRESTServiceDataHandler(), DATA_HANDLER_WS_REST);
    registerDataHandler(new MBMobbl1ServerDataHandler(), DATA_HANDLER_WS_MOBBL);
  }

  public static MBDataManagerService getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBDataManagerService();
    }

    return _instance;
  }

  public MBDocument createDocument(String documentName)
  {
    MBDocumentDefinition def = MBMetadataService.getInstance().getDefinitionForDocumentName(documentName);
    return new MBDocument(def);
  }

  private MBDocumentOperation getLoaderForDocumentName(String documentName, MBDocument arguments)
  {
    return new MBDocumentOperation(getHandlerForDocument(documentName), documentName, arguments);
  }

  private MBDataHandler getHandlerForDocument(String documentName)
  {
    String dataManagerName = MBMetadataService.getInstance().getDefinitionForDocumentName(documentName).getDataManager();

    MBDataHandler handler = _registeredHandlers.get(dataManagerName);
    if (handler == null)
    {
      String msg = "No datamanager " + dataManagerName + " found for document " + documentName;
      throw new MBNoDataManagerException(msg);
    }
    return handler;
  }

  public MBDocument loadDocument(String documentName)
  {
    return getLoaderForDocumentName(documentName, null).load();
  }

  public MBDocument loadDocument(String documentName, MBDocument doc)
  {
    return getLoaderForDocumentName(documentName, doc).load();
  }

  public void loadDocument(String documentName, MBDocumentOperationDelegate delegate)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, null);
    loader.setDelegate(delegate);
    loader.start();
  }

  public void loadDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, args);
    loader.setDelegate(delegate);
    loader.start();
  }

  public void loadDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate, String documentParser)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, args);
    loader.setDelegate(delegate);
    loader.setDocumentParser(documentParser);
    loader.start();
  }

  public void storeDocument(MBDocument document)
  {
    getHandlerForDocument(document.getName()).storeDocument(document);

  }

  public void storeDocument(MBDocument document, MBDocumentOperationDelegate delegate, Object resultSelector, Object errorSelector)
  {
    MBDocumentOperation storer = new MBDocumentOperation(getHandlerForDocument(document.getName()), document);
    storer.setDelegate(delegate);
    storer.start();
  }

  public void registerDataHandler(MBDataHandler handler, String name)
  {
    _registeredHandlers.put(name, handler);
  }

}
