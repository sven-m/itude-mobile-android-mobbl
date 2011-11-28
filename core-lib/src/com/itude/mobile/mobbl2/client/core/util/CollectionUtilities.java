package com.itude.mobile.mobbl2.client.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class CollectionUtilities
{

  private static Integer INTEGER_ONE = new Integer(1);

  private CollectionUtilities()
  {
  }

  /**
   * Null-safe check if the specified collection is empty.
   *  
   * @param coll the collection to check, may be null
   * @return true if empty or null
   */
  public static boolean isEmpty(Collection<?> coll)
  {
    return (coll == null || coll.isEmpty());
  }

  /**
   * Null-safe check if the specified collection is not empty.
   * 
   * @param coll the collection to check, may be null
   * @return true if non-null and non-empty
   */
  public static boolean isNotEmpty(Collection<?> coll)
  {
    return !CollectionUtilities.isEmpty(coll);
  }

  /**
   * Returns true if the given <i>java.util.Collections</i> contain exactly the same elements with exactly the same cardinalities.
   * That is, if the cardinality of e in a is equal to the cardinality of e in b, for each element e in a or b. 
   * 
   * @param a the first collection, must not be null
   * @param b the second collection, must not be null
   * @return if the collections contain the same elements with the same cardinalities.
   */
  public static boolean isEqualCollection(final Collection<?> a, final Collection<?> b)
  {

    if (a.size() != b.size())
    {
      return false;

    }
    else
    {
      Map<?, ?> mapA = getCardinalityMap(a);
      Map<?, ?> mapB = getCardinalityMap(b);

      if (mapA.size() != mapB.size())
      {
        return false;
      }
      else
      {
        Iterator<?> it = mapA.keySet().iterator();

        while (it.hasNext())
        {
          Object obj = it.next();
          if (getFreq(obj, mapA) != getFreq(obj, mapB))
          {
            return false;
          }
        }
        return true;
      }
    }
  }

  public static Map<?, ?> getCardinalityMap(final Collection<?> coll)
  {
    Map count = new HashMap();

    for (Iterator<?> it = coll.iterator(); it.hasNext();)
    {
      Object obj = it.next();
      Integer c = (Integer) (count.get(obj));

      if (c == null)
      {
        count.put(obj, INTEGER_ONE);
      }
      else
      {
        count.put(obj, new Integer(c.intValue() + 1));
      }
    }
    return count;
  }

  private static final int getFreq(final Object obj, final Map<?,?> freqMap)
  {
    Integer count = (Integer) freqMap.get(obj);
    return count != null ? count.intValue() : 0;
  }
}
