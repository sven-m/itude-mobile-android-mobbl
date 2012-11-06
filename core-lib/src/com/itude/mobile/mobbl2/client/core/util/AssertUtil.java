package com.itude.mobile.mobbl2.client.core.util;

import com.itude.mobile.mobbl2.client.core.MBException;

public final class AssertUtil
{
  private AssertUtil()
  {

  }

  public static <T> void notNull(String varName, T object)
  {
    if (object == null) throw new NullPointerException(varName + " is null");
  }

  public static void notEmpty(String varName, String str)
  {
    notNull(varName, str);
    if (str.length() == 0) throw new MBException(varName + " is empty");
  }

  public static void notZero(String varName, long value)
  {
    if (value == 0) throw new MBException(varName + " is empty");
  }

}
