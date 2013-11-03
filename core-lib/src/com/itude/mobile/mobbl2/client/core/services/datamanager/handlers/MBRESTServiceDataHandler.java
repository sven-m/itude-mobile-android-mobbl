/*
 * (C) Copyright ItudeMobile.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import static com.itude.mobile.mobbl2.client.core.services.MBLocalizationService.getLocalizedString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationFactory;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBDocumentFactory;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResultListener;
import com.itude.mobile.mobbl2.client.core.services.MBResultListenerDefinition;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.exceptions.MBNetworkErrorException;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.exceptions.MBServerErrorException;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBDocumentNotDefinedException;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBCacheManager;
import com.itude.mobile.mobbl2.client.core.util.MBProperties;
import com.itude.mobile.mobbl2.client.core.util.log.Logger;
import com.itude.mobile.mobbl2.client.core.util.log.LoggerFactory;

public class MBRESTServiceDataHandler extends MBWebserviceDataHandler
{

  protected final Logger _log = LoggerFactory.getInstance(Constants.APPLICATION_NAME);

  private String         _documentFactoryType;

  public MBRESTServiceDataHandler()
  {
    super();
  }

  public String getDocumentFactoryType()
  {
    return _documentFactoryType;
  }

  public void setDocumentFactoryType(String documentFactoryType)
  {
    _documentFactoryType = documentFactoryType;
  }

  @Override
  public MBDocument loadFreshDocument(String documentName, MBDocument doc, MBEndPointDefinition endPointDefenition)
  {
    MBEndPointDefinition endPoint = endPointDefenition != null ? endPointDefenition : getEndPointForDocument(documentName);

    String key = doc == null ? documentName : documentName + doc.getUniqueId();

    MBDocument result = doLoadDocument(documentName, doc);

    if (endPoint.getCacheable())
    {
      MBCacheManager.setDocument(result, key, endPoint.getTtl());
    }

    return result;
  }

  @Override
  public MBDocument loadDocument(String documentName, MBDocument doc, MBEndPointDefinition endPointDefenition)
  {
    MBEndPointDefinition endPoint = endPointDefenition != null ? endPointDefenition : getEndPointForDocument(documentName);

    boolean cacheable = false;
    String key = null;

    // Look for any cached result for GET requests. If there; return it
    String operationMethod = doc.getValueForPath("/Operation[0]/@httpMethod");

    if (Constants.C_HTTP_REQUEST_METHOD_GET.equalsIgnoreCase(operationMethod))
    {
      cacheable = endPoint.getCacheable();
    }

    if (cacheable)
    {
      key = doc == null ? documentName : documentName + doc.getUniqueId();

      MBDocument result = MBCacheManager.documentForKey(key);
      if (result != null)
      {
        return result;
      }
    }

    MBDocument result = doLoadDocument(documentName, doc);

    if (cacheable)
    {
      MBCacheManager.setDocument(result, key, endPoint.getTtl());
    }

    return result;
  }

  @Override
  public MBDocument doLoadDocument(String documentName, MBDocument doc)
  {
    MBEndPointDefinition endPoint = getEndPointForDocument(documentName);

    if (endPoint == null)
    {
      if (_log.isWarnEnabled())
      {
        _log.warn("No endpoint defined for document name " + documentName);
      }
      return null;
    }

    if (_log.isDebugEnabled())
    {
      _log.debug("MBRESTServiceDataHandler:doLoadDocument " + documentName + " from " + endPoint.getEndPointUri());
    }

    String dataString = null;
    MBDocument responseDoc = null;

    // We want to be able to perform a request without sending any body (GET request)
    String body = null;

    if (doc != null)
    {
      body = doc.getValueForPath("/*[0]").toString();
    }

    try
    {
      if (_log.isDebugEnabled())
      {
        _log.debug("RestServiceDataHandler is about to send this message: \n" + body + "\n to " + endPoint.getEndPointUri());
      }

      String operationMethod = doc.getValueForPath("/Operation[0]/@httpMethod");

      // Let's get our possibly altered url
      String urlString = getRequestUrlFromDocument(endPoint.getEndPointUri(), doc);

      dataString = postAndGetResult(endPoint, operationMethod, urlString, body);

      if (_log.isDebugEnabled())
      {
        _log.debug("RestServiceDataHandler received this message: " + dataString);

      }

      boolean serverErrorHandled = false;

      for (MBResultListenerDefinition lsnr : endPoint.getResultListeners())
      {
        if (lsnr.matches(dataString))
        {
          MBResultListener rl = MBApplicationFactory.getInstance().createResultListener(lsnr.getName());
          rl.handleResult(dataString, doc, lsnr);
          serverErrorHandled = true;
        }
      }

      if (dataString != null)
      {
        byte[] data = dataString.getBytes();

        responseDoc = MBDocumentFactory.getInstance().getDocumentWithData(data,
                                                                          getDocumentFactoryType(),
                                                                          MBMetadataService.getInstance()
                                                                              .getDefinitionForDocumentName(documentName));
      }

      // if the response document is empty and unhandled by endpoint listeners let the user know there is a problem
      if (!serverErrorHandled && responseDoc == null)
      {
        throw new MBServerErrorException("The server returned an error. Please try again later");
      }
      return responseDoc;
    }
    // TODO: clean up exception handling
    catch (Exception e)
    {
      if (_log.isDebugEnabled())
      {
        _log.debug("Sent xml:\n" + body);
        _log.debug("Received:\n" + dataString, e.getCause());
      }

      if (e instanceof RuntimeException)
      {
        throw (RuntimeException) e;
      }
      else if (e instanceof SocketException)
      {
        MBNetworkErrorException networkException = new MBNetworkErrorException("No internet connection");
        networkException.setName(getLocalizedString("Network error"));
        throw networkException;
      }
      else if (e instanceof SocketTimeoutException)
      {
        MBNetworkErrorException networkException = new MBNetworkErrorException("Internet timeout");
        networkException.setName(getLocalizedString("Network error"));
        throw networkException;
      }
      else if (e instanceof UnknownHostException
               || (e instanceof IOException && e.getMessage() != null && e.getMessage().contains("SSL handshake")))
      {
        MBServerErrorException serverException = new MBServerErrorException("Server unreachable");
        serverException.setName(getLocalizedString("Server message"));
        throw serverException;
      }
      throw new RuntimeException(e);
    }
  }

  protected String getRequestUrlFromDocument(String urlString, //
                                             MBDocument document) //
      throws UnsupportedEncodingException
  {

    String operationName = document.getValueForPath("/Operation[0]/@name");

    boolean firstParam = true;
    StringBuffer sb = new StringBuffer(urlString);
    if (StringUtil.isNotBlank(operationName))
    {
      sb.append(operationName);
    }
    for (MBElement el : (List<MBElement>) document.getValueForPath("/Operation[0]/Parameter"))
    {
      String key = el.getValueForAttribute("key");
      String value = el.getValueForAttribute("value");

      if (firstParam)
      {
        sb.append("?");
        firstParam = false;
      }
      else
      {
        sb.append("&");
      }
      sb.append(key + "=" + URLEncoder.encode(value, Constants.C_ENCODING));
    }
    return sb.toString();
  }

  protected DefaultHttpClient createHttpClient(HttpParams httpParameters)
  {
    return new DefaultHttpClient(httpParameters);
  }

  protected String postAndGetResult(MBEndPointDefinition endPoint, //
                                    String operationMethod, //
                                    String endPointUri, //
                                    String body) throws //
      UnsupportedEncodingException, //
      IOException, //
      ClientProtocolException, //
      KeyManagementException, //
      NoSuchAlgorithmException
  {

    String dataString = null;

    HttpParams httpParameters = new BasicHttpParams();
    httpParameters.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

    // Set the timeout in milliseconds until a connection is established.
    int timeoutConnection = 5000;
    // Set the default socket timeout (SO_TIMEOUT) 
    // in milliseconds which is the timeout for waiting for data.
    int timeoutSocket = 5000;

    DefaultHttpClient httpClient = createHttpClient(httpParameters);

    HttpUriRequest httpUriRequest = setupHttpUriRequestType(operationMethod, endPointUri, body);

    // Make sure our request headers are set (if needed)
    setupHttpUriRequestHeader(httpUriRequest);

    if (endPoint.getTimeout() > 0)
    {
      timeoutSocket = endPoint.getTimeout() * 1000;
      timeoutConnection = endPoint.getTimeout() * 1000;
    }
    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

    boolean allowAnyCertificate = false;

    boolean inDevelopment = MBProperties.getInstance().getBooleanProperty(Constants.C_PROPERTY_INDEVELOPMENT);
    if (inDevelopment)
    {
      try
      {
        MBDocument environmentDocument = MBDataManagerService.getInstance().loadDocument(Constants.C_APPLICATION_ENVIRONMENT);
        allowAnyCertificate = Boolean.parseBoolean((String) environmentDocument.getValueForPath("Secure[0]/@allowAll"));
      }
      catch (MBDocumentNotDefinedException dnde)
      {
        if (_log.isDebugEnabled())
        {
          _log.debug("No Environment properties set");
        }
      }
    }

    if (allowAnyCertificate)
    {
      allowAnyCertificate(httpClient);
    }

    HttpResponse httpResponse = httpClient.execute(httpUriRequest);

    int responseCode = httpResponse.getStatusLine().getStatusCode();
    String responseMessage = httpResponse.getStatusLine().getReasonPhrase();
    if (responseCode != HttpStatus.SC_OK)
    {
      if (_log.isErrorEnabled())
      {
        _log.error("MBRESTServiceDataHandler.loadDocument: Received HTTP responseCode=" + responseCode + ": " + responseMessage);
      }
    }

    HttpEntity entity = httpResponse.getEntity();
    if (entity != null)
    {
      dataString = getDataFromEntity(entity);
    }
    return dataString;
  }

  protected void setupHttpUriRequestHeader(HttpUriRequest httpUriRequest)
  {
    httpUriRequest.addHeader("Content-Type", "application/x-www-form-encoded");
    httpUriRequest.addHeader("Accept", "application/xml");
  }

  private HttpUriRequest setupHttpUriRequestType(String operationName, String endPointUri, String body) throws UnsupportedEncodingException
  {
    HttpUriRequest httpUriRequest;
    // To be backward compatible we assume that if no request method was set POST will be used. 
    if (Constants.C_HTTP_REQUEST_METHOD_GET.equalsIgnoreCase(operationName))
    {
      httpUriRequest = new HttpGet(endPointUri);
    }
    else if (Constants.C_HTTP_REQUEST_METHOD_PUT.equalsIgnoreCase(operationName))
    {
      httpUriRequest = new HttpPut(endPointUri);
      if (body != null)
      {
        ((HttpPut) httpUriRequest).setEntity(new StringEntity(body, Constants.C_ENCODING));
      }
    }
    else if (Constants.C_HTTP_REQUEST_METHOD_DELETE.equalsIgnoreCase(operationName))
    {
      httpUriRequest = new HttpDelete(endPointUri);
    }
    else if (Constants.C_HTTP_REQUEST_METHOD_HEAD.equalsIgnoreCase(operationName))
    {
      httpUriRequest = new HttpHead(endPointUri);
    }
    else if (Constants.C_HTTP_REQUEST_METHOD_POST.equalsIgnoreCase(operationName))
    {
      httpUriRequest = new HttpPost(endPointUri);

      if (body != null)
      {
        ((HttpPost) httpUriRequest).setEntity(new StringEntity(body, Constants.C_ENCODING));
      }
    }
    else
    {
      httpUriRequest = new HttpPost(endPointUri);

      if (body != null)
      {
        ((HttpPost) httpUriRequest).setEntity(new StringEntity(body, Constants.C_ENCODING));
      }
    }
    return httpUriRequest;
  }

  private String getDataFromEntity(HttpEntity entity) throws IOException
  {
    String dataString;
    InputStream inStream = entity.getContent();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int i = inStream.read(buffer);
    while (i > -1)
    {
      bos.write(buffer, 0, i);
      i = inStream.read(buffer);
    }
    inStream.close();
    dataString = new String(bos.toByteArray());
    return dataString;
  }

  @Override
  public void storeDocument(MBDocument document)
  {
  }

  private void allowAnyCertificate(HttpClient httpClient) throws KeyManagementException, NoSuchAlgorithmException
  {

    KeyStore trustStore;
    try
    {
      trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      trustStore.load(null, null);

      SSLSocketFactory sf = new TrustedSSLSocketFactory(trustStore);
      sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

      ClientConnectionManager ccm = httpClient.getConnectionManager();
      SchemeRegistry sr = ccm.getSchemeRegistry();
      sr.register(new Scheme("https", sf, 443));
    }
    catch (KeyStoreException kse)
    {
      if (_log.isErrorEnabled())
      {
        _log.error("Could not make keystore " + kse.getMessage(), kse);
      }
    }
    catch (CertificateException ce)
    {
      if (_log.isErrorEnabled())
      {
        _log.error("Could not make locate certificate " + ce.getMessage(), ce);
      }
    }
    catch (IOException ioe)
    {
      if (_log.isErrorEnabled())
      {
        _log.error(ioe.getMessage(), ioe);
      }
    }
    catch (UnrecoverableKeyException urke)
    {
      if (_log.isErrorEnabled())
      {
        _log.error(urke.getMessage(), urke);
      }
    }
  }

  public class TrustedSSLSocketFactory extends SSLSocketFactory
  {
    private final SSLContext sslContext = SSLContext.getInstance("TLS");

    public TrustedSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException,
        UnrecoverableKeyException
    {
      super(truststore);

      TrustManager tm = new X509TrustManager()
      {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
          return null;
        }
      };

      sslContext.init(null, new TrustManager[]{tm}, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException
    {
      return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException
    {
      return sslContext.getSocketFactory().createSocket();
    }
  }

}
