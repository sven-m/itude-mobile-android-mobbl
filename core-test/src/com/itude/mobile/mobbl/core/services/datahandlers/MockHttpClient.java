package com.itude.mobile.mobbl.core.services.datahandlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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

  private HttpUriRequest lastRequest;

  @Override
  public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException
  {
    ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
    StatusLine line = new BasicStatusLine(version, 200, "Great success!");
    HttpResponse response = new BasicHttpResponse(line);
    response.setEntity(new StringEntity("<Result>blarp</Result>"));
    lastRequest = request;
    return response;
  }

  public HttpUriRequest getLastRequest()
  {
    return lastRequest;
  }

  @Override
  public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1) throws IOException, ClientProtocolException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1, HttpContext arg2) throws IOException,
      ClientProtocolException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2) throws IOException, ClientProtocolException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2, HttpContext arg3) throws IOException,
      ClientProtocolException
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ClientConnectionManager getConnectionManager()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HttpParams getParams()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
