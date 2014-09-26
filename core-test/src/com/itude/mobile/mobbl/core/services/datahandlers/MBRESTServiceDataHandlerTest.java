package com.itude.mobile.mobbl.core.services.datahandlers;

import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpParams;

import android.test.ApplicationTestCase;

import com.itude.mobile.android.util.DataUtil;
import com.itude.mobile.mobbl.core.MBApplicationCore;
import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.services.MBDataManagerService;
import com.itude.mobile.mobbl.core.services.MBMetadataService;
import com.itude.mobile.mobbl.core.services.datamanager.handlers.MBMockRESTServiceDataHandler;
import com.itude.mobile.mobbl.core.services.datamanager.handlers.MBMockRESTServiceDataHandler.HttpClientFactory;

public class MBRESTServiceDataHandlerTest extends ApplicationTestCase<MBApplicationCore>
{
  private final String TEST_DOCUMENT_NAME = "WebCallResult";

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
    request.setValue("POST", "Operation[0]/@httpMethod");
    request.setValue("blarp", "Operation[0]/@name");
    return request;

  }

  /*
  
  (void)testCorrectHTTPRequestLoadFreshNoArguments {
    NSData * const httpData = [self testData];
    NSArray * const mockConnectionBehavior = [self connectionBehaviorWithData:httpData andFinishWithResponseHeaders:@{}];
    
    __block NSURLRequest * urlRequest = nil;
    const MBHTTPConnectionBuilder mockConnectionBuilder = ^id<MBHTTPConnection>(NSURLRequest *request, id<MBHTTPConnectionDelegate>delegate) {
        urlRequest = [request retain];
        return [[MBMockHTTPConnection alloc] initWithRequest:request delegate:delegate connectionBehavior:mockConnectionBehavior];
    };
    
    MBMockWebServiceDataHandler * const mockWebServiceDataHandler = [[MBMockWebServiceDataHandler alloc] initWithConnectionBuilder:mockConnectionBuilder documentCacheStorage:nil];
    XCTAssertNotNil(mockWebServiceDataHandler);
    
    [mockConnectionBuilder release];
    
    __unused MBDocument *resultIsIrrelevant = [mockWebServiceDataHandler loadDocument:TestDocumentName];
    
    XCTAssertNotNil(urlRequest);
    
    NSDictionary * const effectiveHTTPHeaders = [urlRequest allHTTPHeaderFields];
    NSString * const httpMethod = [urlRequest HTTPMethod];
    
    NSDictionary * const expectedHTTPHeaders = [self defaultHTTPHeaders];
    
    XCTAssertEqualObjects(@"POST", httpMethod);
    XCTAssertEqualObjects(effectiveHTTPHeaders, expectedHTTPHeaders);
  }*/

  public void testCorrectHTTPRequestLoadFreshNoArguments()
  {
    final MockHttpClient client = new MockHttpClient();
    HttpClientFactory factory = new HttpClientFactory()
    {
      @Override
      public HttpClient createHttpClient(HttpParams httpParameters)
      {
        return client;
      }
    };

    MBMockRESTServiceDataHandler dataHandler = new MBMockRESTServiceDataHandler(factory);
    dataHandler.setDocumentFactoryType("XML");

    MBDocument request = getRequestDocument();
    MBDocument testResult = dataHandler.loadDocument(TEST_DOCUMENT_NAME, request, null);

    String httpMethod = client.getLastRequest().getMethod();

    assertEquals("POST", httpMethod);
  }

}
