package com.itude.mobile.mobbl2.client.core.services.operation;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public interface MBDocumentOperationDelegate
{
  public void processResult(MBDocument document);
  public void processException(Exception e);

}