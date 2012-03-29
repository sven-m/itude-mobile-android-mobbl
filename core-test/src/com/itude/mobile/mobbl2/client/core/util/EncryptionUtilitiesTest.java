package com.itude.mobile.mobbl2.client.core.util;

import java.io.UnsupportedEncodingException;

import android.test.ApplicationTestCase;
import android.util.Base64;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;
import com.itude.mobile.mobbl2.client.core.services.MBMetadataService;

public class EncryptionUtilitiesTest extends ApplicationTestCase<MBApplicationController>
{

  private static final String TESTSTRING   = "toplinealex";
  private static final String ENCODINGTYPE = "Windows-1252";

  public EncryptionUtilitiesTest()
  {
    super(MBApplicationController.class);
  }

  @Override
  protected void setUp() throws Exception
  {
    createApplication();
    MBMetadataService.setConfigName("unittests/config_unittests.xml");
    MBMetadataService.setEndpointsName("testconfig/endpoints.xml");
    super.setUp();
  }

  public void testEncryptWiki1()
  {
    byte[] result = EncryptionUtilities.encrypt("Key", "Plaintext");
    assertEquals("BBF316E8D940AF0AD3", EncryptionUtilities.byte2string(result).toUpperCase());
  }

  public void testEncryptWiki2()
  {
    byte[] result = EncryptionUtilities.encrypt("Wiki", "pedia");
    assertEquals("1021BF0420", EncryptionUtilities.byte2string(result).toUpperCase());
  }

  public void testEncrypt() throws UnsupportedEncodingException
  {
    byte[] result = EncryptionUtilities.encrypt(TESTSTRING.toUpperCase(), TESTSTRING.toUpperCase());
    assertEquals("ÝÕåòÈïÇÊ_Ð", ByteUtil.encodeBytesToString(result, ENCODINGTYPE).trim());
  }

  public void testEncryptMetBase() throws UnsupportedEncodingException
  {
    byte[] result = EncryptionUtilities.encrypt(TESTSTRING.toUpperCase(), TESTSTRING.toUpperCase());
    assertEquals("w53DlcOlw7LDiMOvw4fDil/DkA8=", Base64.encodeToString(ByteUtil.encodeBytes(result, ENCODINGTYPE), Base64.DEFAULT).trim());
  }
}
