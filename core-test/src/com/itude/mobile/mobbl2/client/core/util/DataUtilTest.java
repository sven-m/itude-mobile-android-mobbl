package com.itude.mobile.mobbl2.client.core.util;

import java.io.UnsupportedEncodingException;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;

public class DataUtilTest extends ApplicationTestCase<MBApplicationController>
{

  public DataUtilTest()
  {
    super(MBApplicationController.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    createApplication();

    super.setUp();
  }

  public void testCompression()
  {
    try
    {
      byte[] testData = "Test content for testing compression mechanism".getBytes(Constants.C_ENCODING);
      byte[] compressed = DataUtil.getInstance().compress(testData);
      assertNotNull(compressed);
    }
    catch (UnsupportedEncodingException e)
    {
      fail(e.getMessage());
    }
  }

  public void testDecompression()
  {
    try
    {
      String testString = "Test content for testing compression mechanism";
      byte[] testData = testString.getBytes(Constants.C_ENCODING);
      
      byte[] compressed = DataUtil.getInstance().compress(testData);
      assertNotNull(compressed);
      
      byte[] result = DataUtil.getInstance().decompress(compressed);
      assertNotNull(result);
      assertEquals(testString, new String(result));
    }
    catch (UnsupportedEncodingException e)
    {
      fail(e.getMessage());
    }
  }
}
