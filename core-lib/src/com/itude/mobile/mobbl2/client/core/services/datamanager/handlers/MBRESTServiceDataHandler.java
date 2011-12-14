package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.itude.mobile.mobbl2.client.core.configuration.webservices.MBEndPointDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationFactory;
import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBDocumentFactory;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;
import com.itude.mobile.mobbl2.client.core.services.MBResultListener;
import com.itude.mobile.mobbl2.client.core.services.MBResultListenerDefinition;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.exceptions.MBNetworkErrorException;
import com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.exceptions.MBServerErrorException;
import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBProperties;
import com.itude.mobile.mobbl2.client.core.util.https.EasySSLSocketFactory;
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
    String body = doc.getValueForPath("/*[0]").toString();

    try
    {
      if (_log.isDebugEnabled())
      {
        _log.debug("RestServiceDataHandler is about to send this message: \n" + body + "\n to " + endPoint.getEndPointUri());
      }
      dataString = postAndGetResult(endPoint, body);

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
        String msg = MBLocalizationService.getInstance().getTextForKey("The server returned an error. Please try again later");
        //                if(delegate.err != nil) {
        //                    msg = [NSString stringWithFormat:@"%@ %@: %@", msg, delegate.err.domain, delegate.err.code];
        //                }
        throw new MBServerErrorException(msg);
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
        networkException.setName("Network error");
        throw networkException;
      }
      else if (e instanceof SocketTimeoutException)
      {
        MBNetworkErrorException networkException = new MBNetworkErrorException("Internet timeout");
        networkException.setName("Network error");
        throw networkException;
      }
      else if (e instanceof UnknownHostException
               || (e instanceof IOException && e.getMessage() != null && e.getMessage().contains("SSL handshake")))
      {
        MBServerErrorException serverException = new MBServerErrorException("Server unreachable");
        serverException.setName("Server message");
        throw serverException;
      }
      throw new RuntimeException(e);
    }
  }

  private String postAndGetResult(MBEndPointDefinition endPoint, String body) throws UnsupportedEncodingException, IOException,
      ClientProtocolException, KeyManagementException, NoSuchAlgorithmException
  {
    String dataString = null;
    HttpPost httpPost = new HttpPost(endPoint.getEndPointUri());
    // Content-Type must be set because otherwise the MidletCommandProcessor servlet cannot read the XML
    httpPost.setHeader("Content-Type", "text/xml");
    if (body != null)
    {
      httpPost.setEntity(new StringEntity(body));
    }
    HttpParams httpParameters = new BasicHttpParams();
    httpParameters.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
    
    // Set the timeout in milliseconds until a connection is established.
    int timeoutConnection = 5000;
    // Set the default socket timeout (SO_TIMEOUT) 
    // in milliseconds which is the timeout for waiting for data.
    int timeoutSocket = 5000;
    HttpClient httpClient = new DefaultHttpClient(httpParameters);
    if (endPoint.getTimeout() > 0)
    {
      timeoutSocket = endPoint.getTimeout() * 1000;
      timeoutConnection = endPoint.getTimeout() * 1000;
    }
    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

    String allowAnyCertificate = MBProperties.getInstance().getValueForProperty(Constants.C_PROPERTY_HTTPS_ALLOW_ALL_CERTIFICATES);
    if (Boolean.parseBoolean(allowAnyCertificate)) allowAnyCertificate(httpClient);

    HttpResponse httpResponse = httpClient.execute(httpPost);
    
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
    /*
     * C.H: Tried to port this from mobile web. Didn't work unfortunately. 
     */
    ClientConnectionManager ccm = httpClient.getConnectionManager();
    SchemeRegistry sr = ccm.getSchemeRegistry();
    sr.register(new Scheme("https", new EasySSLSocketFactory(), 443));
  }

}
