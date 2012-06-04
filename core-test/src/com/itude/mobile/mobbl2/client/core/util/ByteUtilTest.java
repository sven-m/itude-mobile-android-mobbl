package com.itude.mobile.mobbl2.client.core.util;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;

public class ByteUtilTest extends ApplicationTestCase<MBApplicationController>
{
  private static final String TESTSTRING = "blabla";

  public ByteUtilTest()
  {
    super(MBApplicationController.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    createApplication();

    super.setUp();
  }

  public void testEncodeStringToBytes()
  {
    byte[] result = ByteUtil.encodeStringToBytes(TESTSTRING, ByteUtil.UTF8);
    assertEquals(TESTSTRING, ByteUtil.encodeBytesToString(result, ByteUtil.UTF8));
  }

}
