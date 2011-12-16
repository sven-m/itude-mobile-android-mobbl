package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import java.util.Date;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.datamanager.MBDataHandler;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;
import com.itude.mobile.mobbl2.client.core.util.threads.exception.MBInterruptedException;

public class MBDocumentOperation extends MBThread
{
  private MBDataHandler               _dataHandler;
  private String                      _documentName;
  private MBDocument                  _document;
  private MBDocument                  _arguments;
  private MBEndPointDefinition        _endPointDefinition;
  private MBDocumentOperationDelegate _delegate;
  private String                      _documentParser;

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
    return _arguments != null ? _arguments.clone() : null;
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

  public String getDocumentParser()
  {
    return _documentParser;
  }

  public void setDocumentParser(String documentParser)
  {
    _documentParser = documentParser;
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

    MBDocument doc = getDataHandler().loadDocument(getDocumentName(), getArguments(), getEndPointDefinition(), getDocumentParser());

    if (doc == null)
    {
      MBDocumentDefinition docDef = MBMetadataService.getInstance().getDefinitionForDocumentName(getDocumentName());
      if (docDef.getAutoCreate())
      {
        doc = docDef.createDocument();
      }
    }
    doc.setArgumentsUsed(getArguments());
    Log.d(Constants.APPLICATION_NAME, "Loading of document " + getDocumentName() + " took " + (new Date().getTime() - now) / 1000
                                      + " seconds");
    return doc;
  }

  public void store()
  {
    getDataHandler().storeDocument(getDocument());
  }

  @Override
  public void runMethod()
  {
    try
    {
      checkForInterruption();
      if (_document == null)
      {
        MBDocument document = load();
        checkForInterruption();
        getDelegate().processResult(document);
      }
      else
      {
        store();
        checkForInterruption();
        getDelegate().processResult(getDocument());
      }
    }
    catch (Exception e)
    {
      if (e instanceof MBInterruptedException)
      {
        throw (MBInterruptedException) e;
      }

      Log.w(Constants.APPLICATION_NAME, "Exception during Document Operation: " + e.getMessage(), e);
      getDelegate().processException(e);
    }
  }

  public void setEndPointDefinition(MBEndPointDefinition endPointDefinition)
  {
    _endPointDefinition = endPointDefinition;
  }

  public MBEndPointDefinition getEndPointDefinition()
  {
    return _endPointDefinition;
  }
}
