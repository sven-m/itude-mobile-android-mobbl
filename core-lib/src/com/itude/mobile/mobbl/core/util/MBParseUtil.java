/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
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
package com.itude.mobile.mobbl.core.util;

import android.util.Log;

import com.itude.mobile.android.util.StringUtil;

public final class MBParseUtil
{
  private MBParseUtil()
  {
  }

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

    // remove everything but a dash, point/period, comma and numerics
    converted = converted.replaceAll("[^-.,0-9]", "");

    Double returnValue = null;
    try
    {
      returnValue = Double.parseDouble(converted);
    }
    catch (NumberFormatException e)
    {
      Log.w(Constants.APPLICATION_NAME, "Could not convert " + stringToConvert + " to double");
    }

    return returnValue;
  }

  public static boolean booleanValue(String bool, boolean returnValueIfNotParsed)
  {
    Boolean parsed = strictBooleanValue(bool);
    if (parsed == null)
    {
      return returnValueIfNotParsed;
    }

    return parsed;
  }

  public static boolean booleanValue(String bool)
  {
    return booleanValue(bool, false);
  }

  public static Boolean strictBooleanValue(String bool)
  {
    if (StringUtil.isEmpty(bool)) return null;
    // you should only use C_TRUE; the rest is there for legacy purposes
    if (bool.equalsIgnoreCase(Constants.C_TRUE) || bool.equals("1") || bool.equals("1.0") || bool.equalsIgnoreCase("true")
        || bool.equalsIgnoreCase("yes")) return true;
    else if (bool.equalsIgnoreCase(Constants.C_FALSE) || bool.equals("0") || bool.equals("0.0") || bool.equalsIgnoreCase("false")
             || bool.equalsIgnoreCase("no")) return false;
    else return null;

  }

}
