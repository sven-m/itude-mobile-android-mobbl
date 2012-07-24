package com.itude.mobile.mobbl2.client.core.util;

public class ComparisonUtil
{
  public static boolean safeEquals(Object o1, Object o2)
  {
    if (o1 == null && o2 == null) return true;
    if (o1 == null || o2 == null) return false;
    return o1.equals(o2);
  }

  public static <T extends Comparable<T>> int safeCompare(T o1, T o2)
  {
    return safeCompare(o1, o2, true);
  }

  public static <T extends Comparable<T>> int safeCompare(T o1, T o2, boolean nullsFirst)
  {
    if (o1 == null && o2 == null) return 0;
    if (o1 != null && o2 != null) return o1.compareTo(o2);
    if (o1 == null) return nullsFirst ? -1 : 1;
    return nullsFirst ? 1 : -1;
  }

  public static <T> boolean in(T obj, T... args)
  {
    for (T arg : args)
      if (safeEquals(obj, arg)) return true;

    return false;
  }
}