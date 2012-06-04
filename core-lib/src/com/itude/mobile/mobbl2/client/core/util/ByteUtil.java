package com.itude.mobile.mobbl2.client.core.util;

import java.io.UnsupportedEncodingException;

import android.util.Log;

/**
 *
 */
public final class ByteUtil
{

  public static String UTF8 = "UTF-8";

  private ByteUtil()
  {
  }

  static public byte[] encodeStringToBytes(String result, String encodingType)
  {
    try
    {
      return result.getBytes(encodingType);
    }
    catch (UnsupportedEncodingException e)
    {
      Log.w(Constants.APPLICATION_NAME, "unable is encode bytes with type " + encodingType);
    }
    return result.getBytes();
  }

  static public byte[] encodeBytes(byte[] bytes, String encodingType)
  {
    String result = encodeBytesToString(bytes, encodingType);
    try
    {
      return result.getBytes(UTF8);
    }
    catch (UnsupportedEncodingException e)
    {
      Log.w(Constants.APPLICATION_NAME, "unable is encode bytes with type " + encodingType);
    }
    return result.getBytes();
  }

  static public String encodeBytesToString(byte[] bytes, String encodingType)
  {
    String result = "";
    try
    {
      result = new String(bytes, encodingType);
    }
    catch (UnsupportedEncodingException e)
    {
      Log.w(Constants.APPLICATION_NAME, "unable is encode bytes with type " + encodingType);
    }
    return result;
  }

}
