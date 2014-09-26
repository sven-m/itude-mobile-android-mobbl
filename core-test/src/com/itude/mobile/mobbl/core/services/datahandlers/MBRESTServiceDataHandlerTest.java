package com.itude.mobile.mobbl.core.services.datahandlers;

import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpParams;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.mobbl.core.MBApplicationCore;
import com.itude.mobile.mobbl.core.configuration.mvc.MBDocumentDefinition;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.parser.MBXmlDocumentParser;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.datamanager.handlers.MBMockRESTServiceDataHandler;
import com.itude.mobile.mobbl.core.services.datamanager.handlers.MBMockRESTServiceDataHandler.HttpClientFactory;
import com.itude.mobile.mobbl.core.util.MBCacheManager;

public class MBRESTServiceDataHandlerTest extends ApplicationTestCase<MBApplicationCore>
{
  private static final String TEST_DOCUMENT_NAME  = "WebCallResult";
  private static final String TEST_DOCUMENT_HTTP  = "<" + TEST_DOCUMENT_NAME + "><Result>Success!</Result></" + TEST_DOCUMENT_NAME + ">";
  private static final String TEST_DOCUMENT_CACHE = "<" + TEST_DOCUMENT_NAME + "><Result>Cache!</Result></" + TEST_DOCUMENT_NAME + ">";

  public MBRESTServiceDataHandlerTest()
  {
    super(MBApplicationCore.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    createApplication();
    DataUtil.getInstance().setContext(getContext());

    MBMetadataService.setConfigName("config/config.xml");
    MBMetadataService.setEndpointsName("config/endpoints.xml");
  }

  private MBDocument getRequestDocument()
  {
    MBDocument request = MBDataManagerService.getInstance().loadDocument("MBGenericRestRequest");
    request.setValue("GET", "Operation[0]/@httpMethod");
    request.setValue("blarp", "Operation[0]/@name");
    return request;
  }

  private MockHttpClient createClient()
  {
    return new MockHttpClient(TEST_DOCUMENT_HTTP, 200);
  }

  private HttpClientFactory createFactory(final MockHttpClient client)
  {
    return new HttpClientFactory()
    {
      @Override
      public HttpClient createHttpClient(HttpParams httpParameters)
      {
        return client;
      }
    };
  }

  private MBDocument parse(String string)
  {
    MBDocumentDefinition definition = MBMetadataService.getInstance().getDefinitionForDocumentName(TEST_DOCUMENT_NAME);
    MBXmlDocumentParser parser = new MBXmlDocumentParser();
    return parser.parse(string.getBytes(), definition);
  }

  private MBMockRESTServiceDataHandler createDataHandler(MockHttpClient client)
  {

    MBMockRESTServiceDataHandler dataHandler = new MBMockRESTServiceDataHandler(createFactory(client));
    dataHandler.setDocumentFactoryType("XML");
    return dataHandler;
  }

  public void testCorrectHTTPRequestLoadFresh()
  {
    MockHttpClient client = createClient();
    MBMockRESTServiceDataHandler dataHandler = createDataHandler(client);

    MBDocument request = getRequestDocument();
    dataHandler.loadFreshDocument(TEST_DOCUMENT_NAME, request, null);

    String httpMethod = client.getLastUriRequest().getMethod();
    assertEquals("application/xml", client.getLastUriRequest().getFirstHeader("Accept").getValue());
    assertEquals("application/x-www-form-encoded", client.getLastUriRequest().getFirstHeader("Content-Type").getValue());

    assertEquals("GET", httpMethod);
    assertEquals("http://example.com/resource/blarp", client.getLastUriRequest().getURI().toString());
  }

  public void testCorrectResultLoadFresh()
  {
    MockHttpClient client = createClient();
    MBMockRESTServiceDataHandler dataHandler = createDataHandler(client);

    MBDocument request = getRequestDocument();
    MBDocument response = dataHandler.loadFreshDocument(TEST_DOCUMENT_NAME, request, null);

    assertEquals("Success!", response.getValueForPath("Result[0]/@text()"));
  }

  public void testCacheHit()
  {
    MockHttpClient client = createClient();
    MBMockRESTServiceDataHandler dataHandler = createDataHandler(client);

    MBDocument request = getRequestDocument();

    MBCacheManager.setDocument(parse(TEST_DOCUMENT_CACHE), TEST_DOCUMENT_NAME + request.getUniqueId(), 0);

    // hit the cache; results in cache document
    MBDocument response = dataHandler.loadDocument(TEST_DOCUMENT_NAME, request, null);
    assertEquals("Cache!", response.getValueForPath("Result[0]/@text()"));

    // bypass cache
    MBDocument response2 = dataHandler.loadFreshDocument(TEST_DOCUMENT_NAME, request, null);
    assertEquals("Success!", response2.getValueForPath("Result[0]/@text()"));

    // previous call  should have stored http document in cache, so test if this happened
    MBDocument response3 = dataHandler.loadDocument(TEST_DOCUMENT_NAME, request, null);
    assertEquals("Success!", response3.getValueForPath("Result[0]/@text()"));

  }

  public void testCacheMiss()
  {
    MockHttpClient client = createClient();
    MBMockRESTServiceDataHandler dataHandler = createDataHandler(client);

    MBDocument request = getRequestDocument();

    // make sure document isn't in cache
    MBCacheManager.getInstance().expireDataForKey(TEST_DOCUMENT_NAME + request.getUniqueId());

    MBDocument response = dataHandler.loadDocument(TEST_DOCUMENT_NAME, request, null);
    assertEquals("Success!", response.getValueForPath("Result[0]/@text()"));
  }

}
