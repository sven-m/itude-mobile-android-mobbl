package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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

import com.itude.mobile.mobbl2.client.core.configuration.endpoints.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationFactory;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBDocumentFactory;
import com.itude.mobile.mobbl2.client.core.services.MBDataManagerService;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResultListener;
import com.itude.mobile.mobbl2.client.core.services.MBResultListenerDefinition;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.exceptions.MBNetworkErrorException;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.exceptions.MBServerErrorException;
import com.itude.mobile.mobbl2.client.core.services.exceptions.MBDocumentNotDefinedException;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBProperties;
import com.itude.mobile.mobbl2.client.core.util.log.Logger;
import com.itude.mobile.mobbl2.client.core.util.log.LoggerFactory;

public class MBRESTServiceDataHandler extends MBWebserviceDataHandler
{

  protected final Logger      _log         = LoggerFactory.getInstance(Constants.APPLICATION_NAME);

  private static final String ENCODINGTYPE = "UTF-8";

  private String              _documentFactoryType;

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
      dataString = postAndGetResult(endPoint, endPoint.getEndPointUri(), body);

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
        networkException.setName(localizedString("Network error"));
        throw networkException;
      }
      else if (e instanceof SocketTimeoutException)
      {
        MBNetworkErrorException networkException = new MBNetworkErrorException("Internet timeout");
        networkException.setName(localizedString("Network error"));
        throw networkException;
      }
      else if (e instanceof UnknownHostException
               || (e instanceof IOException && e.getMessage() != null && e.getMessage().contains("SSL handshake")))
      {
        MBServerErrorException serverException = new MBServerErrorException("Server unreachable");
        serverException.setName(localizedString("Server message"));
        throw serverException;
      }
      throw new RuntimeException(e);
    }
  }

  protected HttpUriRequest setupHttpUriRequest(HttpUriRequest httpUriRequest)
  {
    //     Content-Type must be set because otherwise the MidletCommandProcessor servlet cannot read the XML
    httpUriRequest.setHeader("Content-Type", "text/xml");
    return httpUriRequest;
  }

  protected HttpClient prepareHttpClient(HttpParams httpParams)
  {
    return new DefaultHttpClient(httpParams);
  }

  protected String postAndGetResult(MBEndPointDefinition endPoint, String endPointUri, String body) throws UnsupportedEncodingException,
      IOException, ClientProtocolException, KeyManagementException, NoSuchAlgorithmException
  {

    String dataString = null;

    HttpUriRequest httpUriRequest = null;

    // To be backward compatible we assume that if no request method was set POST will be used. 
    if (Constants.C_HTTP_REQUEST_METHOD_GET.equalsIgnoreCase(endPoint.getRequestMethod()))
    {
      httpUriRequest = new HttpGet(endPointUri);
    }
    else
    {
      httpUriRequest = new HttpPost(endPointUri);

      if (body != null)
      {
        ((HttpPost) httpUriRequest).setEntity(new StringEntity(body, ENCODINGTYPE));
      }
    }

    // Make sure our request headers are set (if needed)
    setupHttpUriRequest(httpUriRequest);

    HttpParams httpParameters = new BasicHttpParams();
    httpParameters.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

    // Set the timeout in milliseconds until a connection is established.
    int timeoutConnection = 5000;
    // Set the default socket timeout (SO_TIMEOUT) 
    // in milliseconds which is the timeout for waiting for data.
    int timeoutSocket = 5000;

    HttpClient httpClient = prepareHttpClient(httpParameters);

    if (endPoint.getTimeout() > 0)
    {
      timeoutSocket = endPoint.getTimeout() * 1000;
      timeoutConnection = endPoint.getTimeout() * 1000;
    }
    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

    boolean allowAnyCertificate = false;

    String inDevelopment = MBProperties.getInstance().getValueForProperty(Constants.C_PROPERTY_INDEVELOPMENT);
    if ("true".equals(inDevelopment))
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

  private String localizedString(String key)
  {
    return MBLocalizationService.getInstance().getTextForKey(key);
  }

}
