package com.itude.mobile.mobbl.core.services.datahandlers;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class MockHttpClient implements HttpClient
{

  private HttpRequest lastRequest;
  private String      response;
  private int         responseCode;

  public MockHttpClient(String response, int responseCode)
  {
    this.response = response;
    this.responseCode = responseCode;
  }

  public HttpResponse doExecute(HttpRequest request) throws IOException, ClientProtocolException
  {
    ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
    StatusLine line = new BasicStatusLine(version, responseCode, "Great success!");
    HttpResponse response = new BasicHttpResponse(line);
    response.setEntity(new StringEntity(this.response));
    lastRequest = request;
    return response;
  }

  public HttpRequest getLastRequest()
  {
    return lastRequest;
  }

  public HttpUriRequest getLastUriRequest()
  {
    return lastRequest instanceof HttpUriRequest ? (HttpUriRequest) lastRequest : null;
  }

  @Override
  public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException
  {
    return doExecute(request);
  }

  @Override
  public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException
  {
    return doExecute(request);
  }

  @Override
  public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException
  {
    return doExecute(request);
  }

  @Override
  public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1) throws IOException, ClientProtocolException
  {
    return arg1.handleResponse(doExecute(arg0));
  }

  @Override
  public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException
  {
    return doExecute(request);
  }

  @Override
  public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1, HttpContext arg2) throws IOException,
      ClientProtocolException
  {
    return arg1.handleResponse(doExecute(arg0));
  }

  @Override
  public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2) throws IOException, ClientProtocolException
  {
    return arg2.handleResponse(doExecute(arg1));
  }

  @Override
  public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2, HttpContext arg3) throws IOException,
      ClientProtocolException
  {
    return arg2.handleResponse(doExecute(arg1));
  }

  @Override
  public ClientConnectionManager getConnectionManager()
  {
    return null;
  }

  @Override
  public HttpParams getParams()
  {
    return getLastRequest().getParams();
  }

}
