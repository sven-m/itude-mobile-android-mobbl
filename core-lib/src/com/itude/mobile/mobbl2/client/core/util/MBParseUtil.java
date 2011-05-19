package com.itude.mobile.mobbl2.client.core.util;

import android.util.Log;

public class MBParseUtil
{
  public static Float floatValueDutch(String stringToConvert)
  {
    // if we have a comma, replace with a dot
    String converted = stringToConvert.replace(",", ".");

    Float returnValue = null;
    try
    {
      returnValue = Float.parseFloat(converted);
    }
    catch (NumberFormatException e)
    {
      Log.d(Constants.APPLICATION_NAME, "Could not convert " + stringToConvert + " to float");
    }

    return returnValue;
  }

  public static Double doubleValueDutch(String stringToConvert)
  {

    if (stringToConvert == null)
    {
      return null;
    }

    // if we have a comma, replace with a dot
    String converted = stringToConvert.replace(",", ".");

    Double returnValue = null;
    try
    {
      returnValue = Double.parseDouble(converted);
    }
    catch (NumberFormatException e)
    {
      Log.d(Constants.APPLICATION_NAME, "Could not convert " + stringToConvert + " to double");
    }

    return returnValue;
  }
}
