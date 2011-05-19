package com.itude.mobile.mobbl2.client.core.util;

import java.util.Comparator;
import java.util.Vector;

import com.itude.mobile.mobbl2.client.core.model.MBElement;

public class MBDynamicAttributeComparator implements Comparator<Object>
{

  private final Vector<Object[]> trace;

  public MBDynamicAttributeComparator(Vector<Object[]> trace)
  {
    this.trace = trace;
  }

  public int compare(Object object1, Object object2)
  {
    return nestedCompare(object1, object2, 0);
  }

  private int nestedCompare(Object object1, Object object2, int level)
  {
    int returnInt = 0;

    if (object1 instanceof MBElement && object2 instanceof MBElement)
    {
      MBElement el1 = (MBElement) object1;
      MBElement el2 = (MBElement) object2;

      String compString = (String) trace.get(level)[0];
      String attribute1 = el1.getValueForAttribute(compString);
      String attribute2 = el2.getValueForAttribute(compString);

      if (attribute1.compareTo(attribute2) < 0)
      {
        returnInt = -1;
      }
      else if (attribute1.compareTo(attribute2) > 0)
      {
        returnInt = 1;
      }
      else
      {
        // Same value
        if (level == trace.size() - 1)
        {
          returnInt = 0;
        }
        else
        {
          returnInt = nestedCompare(object1, object2, (level + 1));
        }
      }

    }

    if (level > 0)
    {
      boolean parentAscending = (Boolean) trace.get(level - 1)[1];
      boolean ascending = (Boolean) trace.get(level)[1];

      if (parentAscending != ascending)
      {
        returnInt *= -1;
      }
    }
    else
    {
      if (!(Boolean) trace.get(level)[1])
      {
        returnInt *= -1;
      }
    }

    return returnInt;
  }

}
