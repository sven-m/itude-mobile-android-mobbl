package com.itude.mobile.mobbl2.client.core.util;

import java.util.regex.Pattern;

public class MBValidationUtil
{
  public static boolean validateEmail(String value)
  {
    if (StringUtilities.isBlank(value))
    {
      return true;
    }

    if (!Pattern.matches("^[0-9a-zA-Z][\\w+_.-]*@\\w[\\w+_.-]*\\.[a-zA-Z]{2,9}$", value))
    {
      return false;
    }

    return true;
  }
}
