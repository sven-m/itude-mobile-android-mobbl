package com.itude.mobile.mobbl2.client.core.services;

import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.datamanager.MBDataHandler;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBDocumentOperation;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBDocumentOperationDelegate;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBFileDataHandler;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBMemoryDataHandler;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBRESTServiceDataHandler;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.MBSystemDataHandler;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.mobbl1.MBMobbl1ServerDataHandler;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBNoDataManagerException;

public class MBDataManagerService
{

  public static final String                        DATA_HANDLER_MEMORY       = "MBMemoryDataHandler";
  public static final String                        DATA_HANDLER_FILE         = "MBFileDataHandler";
  public static final String                        DATA_HANDLER_SYSTEM       = "MBSystemDataHandler";
  public static final String                        DATA_HANDLER_WS_REST      = "MBRESTServiceDataHandler";
  public static final String                        DATA_HANDLER_WS_MOBBL     = "MBMobbl1ServerDataHandler";
  public static final String                        DATA_HANDLER_WS_MOBBL_XML = "MBMobbl1XmlServerDataHandler";

  private static MBDataManagerService               _instance                 = null;

  private final Map<String, MBDataHandler>          _registeredHandlers;

  private final ConcurrentHashMap<String, Set<OperationListener>> _operationListeners;

  private MBDataManagerService()
  {
    _registeredHandlers = new Hashtable<String, MBDataHandler>();
    _operationListeners = new ConcurrentHashMap<String, Set<OperationListener>>();

    registerDataHandler(new MBFileDataHandler(), DATA_HANDLER_FILE);
    registerDataHandler(new MBSystemDataHandler(), DATA_HANDLER_SYSTEM);
    registerDataHandler(new MBMemoryDataHandler(), DATA_HANDLER_MEMORY);
    registerDataHandler(new MBRESTServiceDataHandler(), DATA_HANDLER_WS_REST);
    registerDataHandler(new MBMobbl1ServerDataHandler(), DATA_HANDLER_WS_MOBBL);
    registerDataHandler(new MBMobbl1ServerDataHandler(), DATA_HANDLER_WS_MOBBL_XML);
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

  public MBDocument loadFreshDocument(String documentName)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, null);
    loader.setLoadFreshCopy(true);
    return loader.load();
  }

  public MBDocument loadDocument(String documentName, MBDocument doc)
  {
    return getLoaderForDocumentName(documentName, doc).load();
  }

  public MBDocument loadFreshDocument(String documentName, MBDocument doc)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, doc);
    loader.setLoadFreshCopy(true);
    return loader.load();
  }

  public void loadDocument(String documentName, MBDocumentOperationDelegate delegate)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, null);
    loader.setDelegate(delegate);
    loader.start();
  }

  public void loadFreshDocument(String documentName, MBDocumentOperationDelegate delegate)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, null);
    loader.setDelegate(delegate);
    loader.setLoadFreshCopy(true);
    loader.start();
  }

  public void loadDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, args);
    loader.setDelegate(delegate);
    loader.start();
  }

  public void loadFreshDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, args);
    loader.setDelegate(delegate);
    loader.setLoadFreshCopy(true);
    loader.start();
  }

  public void loadDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate,
                           MBEndPointDefinition endPointDefinition)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, args);
    loader.setEndPointDefinition(endPointDefinition);
    loader.setDelegate(delegate);
    loader.start();
  }

  public void loadFreshDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate,
                                MBEndPointDefinition endPointDefinition)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, args);
    loader.setEndPointDefinition(endPointDefinition);
    loader.setDelegate(delegate);
    loader.setLoadFreshCopy(true);
    loader.start();
  }

  public void loadDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate, String documentParser)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, args);
    loader.setDelegate(delegate);
    loader.setDocumentParser(documentParser);
    loader.start();
  }

  public void loadFreshDocument(String documentName, MBDocument args, MBDocumentOperationDelegate delegate, String documentParser)
  {
    MBDocumentOperation loader = getLoaderForDocumentName(documentName, args);
    loader.setDelegate(delegate);
    loader.setDocumentParser(documentParser);
    loader.setLoadFreshCopy(true);
    loader.start();
  }

  public void storeDocument(MBDocument document)
  {
    String documentName = document.getName();
    getHandlerForDocument(documentName).storeDocument(document);

    Set<OperationListener> list = _operationListeners.get(documentName);

    if (list != null)
    {
      // synchronized because we made a synchronized set in method registerOperationListener
      synchronized (list)
      {
        for (OperationListener listener : list)
        {
          listener.onDocumentStored(document);
        }
      }
    }
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

  public void registerOperationListener(String docName, OperationListener listener)
  {
    Set<OperationListener> list = _operationListeners.putIfAbsent(docName, Collections.synchronizedSet(new HashSet<MBDataManagerService.OperationListener>()));
    list.add(listener);
  }

  public void unregisterOperationListener(String docName, OperationListener listener)
  {
	// not entirely correct, in that if an unregisterOperationListener is called at about the same time as an registerOperationListener, with the same docName and listener, the result
	// is not completely predictable.. changes of this happening are nil, so it doesn't matter ;)
    Set<OperationListener> list = _operationListeners.get(docName);
    if (list != null)
    {
      list.remove(listener);
    }
  }

  public static interface OperationListener
  {
    public void onDocumentStored(MBDocument document);
  }
}
