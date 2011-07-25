package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import java.util.Date;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.datamanager.MBDataHandler;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBDocumentOperation implements Runnable
{
  private MBDataHandler               _dataHandler;
  private String                      _documentName;
  private MBDocument                  _document;
  private MBDocument                  _arguments;
  private MBDocumentOperationDelegate _delegate;

  public MBDataHandler getDataHandler()
  {
    return _dataHandler;
  }

  public void setDataHandler(MBDataHandler dataHandler)
  {
    _dataHandler = dataHandler;
  }

  public String getDocumentName()
  {
    return _documentName;
  }

  public void setDocumentName(String documentName)
  {
    _documentName = documentName;
  }

  public MBDocument getArguments()
  {
    return _arguments;
  }

  public void setArguments(MBDocument arguments)
  {
    _arguments = arguments;
  }

  public MBDocument getDocument()
  {
    return _document;
  }

  public void setDocument(MBDocument document)
  {
    _document = document;
  }

  public MBDocumentOperation(MBDataHandler dataHandler, MBDocument document)
  {
    super();
    _dataHandler = dataHandler;
    _document = document;
  }

  public MBDocumentOperation(MBDataHandler dataHandler, String documentName, MBDocument arguments)
  {
    super();
    _dataHandler = dataHandler;
    _documentName = documentName;
    _arguments = arguments;
  }

  public void setDelegate(MBDocumentOperationDelegate delegate)
  {
    _delegate = delegate;
  }

  public MBDocumentOperationDelegate getDelegate()
  {
    return _delegate;
  }

  public MBDocument load()
  {

    long now = new Date().getTime();

    MBDocument doc = null;
    if (this.getArguments() == null) doc = getDataHandler().loadDocument(getDocumentName());
    else
    {
      doc = getDataHandler().loadDocument(getDocumentName(), getArguments().clone());
    }

    if (doc == null)
    {
      MBDocumentDefinition docDef = MBMetadataService.getInstance().getDefinitionForDocumentName(getDocumentName());
      if (docDef.getAutoCreate()) doc = docDef.createDocument();
    }
    doc.setArgumentsUsed(getArguments());
    Log.d(Constants.APPLICATION_NAME, "Loading of document " + getDocumentName() + " took " + (new Date().getTime() - now) / 1000 + " seconds");
    return doc;
  }

  public void store()
  {
    getDataHandler().storeDocument(getDocument());
  }

  public void run()
  {
    try
    {
      if (_document == null)
      {
        MBDocument document = load();
        getDelegate().processResult(document);
      }
      else
      {
        store();
        getDelegate().processResult(getDocument());
      }
    }
    catch (Exception e)
    {
      Log.w(Constants.APPLICATION_NAME, "Exception during Document Operation: " + e.getMessage(), e);
      getDelegate().processException(e);
    }
  }
}
