package com.itude.mobile.mobbl.core.services.datamanager.handlers;

import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpParams;

public class MBMockRESTServiceDataHandler extends MBRESTServiceDataHandler
{

  public static interface HttpClientFactory
  {
    public HttpClient createHttpClient(HttpParams httpParameters);

  }

  private final HttpClientFactory _factory;

  public MBMockRESTServiceDataHandler(HttpClientFactory factory)
  {
    _factory = factory;
  }

  @Override
  protected HttpClient createHttpClient(HttpParams httpParameters)
  {
    return _factory.createHttpClient(httpParameters);
  }
}
