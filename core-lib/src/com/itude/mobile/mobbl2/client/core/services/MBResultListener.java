package com.itude.mobile.mobbl2.client.core.services;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;

public interface MBResultListener
{
  public void handleResult(String result, MBDocument requestDocument, MBResultListenerDefinition definition);

}
