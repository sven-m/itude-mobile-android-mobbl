package com.itude.mobile.mobbl2.client.core.util;

public class UniqueIntegerGenerator
{

  private static int uniqueInteger = 0;

  public static int getId()
  {
    uniqueInteger++;
    return uniqueInteger;
  }

}
