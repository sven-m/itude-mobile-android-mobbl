/*
 * (C) Copyright ItudeMobile.
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
package com.itude.mobile.mobbl2.client.core.util;

import java.util.Comparator;
import java.util.Vector;

import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBAttributeDefinition;
import com.itude.mobile.mobbl2.client.core.model.MBElement;

public class MBDynamicAttributeComparator implements Comparator<Object>
{

  private final Vector<Object[]> trace;

  public MBDynamicAttributeComparator(Vector<Object[]> trace)
  {
    this.trace = trace;
  }

  @Override
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

      MBAttributeDefinition attrDef1 = el1.getDefinition().getAttributeWithName(compString);
      MBAttributeDefinition attrDef2 = el2.getDefinition().getAttributeWithName(compString);

      String attrType1 = attrDef1.getType();
      String attrType2 = attrDef2.getType();

      if (!attrType1.equals(attrType2))
      {
        throw new MBException("Can't compare different types");
      }

      String attribute1 = el1.getValueForAttribute(compString);
      String attribute2 = el2.getValueForAttribute(compString);

      if ("string".equalsIgnoreCase(attrType1))
      {
        returnInt = compareAttributes(object1, object2, level, attribute1, attribute2);
      }
      else if ("float".equalsIgnoreCase(attrType1))
      {
        returnInt = compareAttributes(object1, object2, level, Float.parseFloat(attribute1), Float.parseFloat(attribute2));
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

  private <T> int compareAttributes(Object object1, Object object2, int level, Comparable<T> attribute1, Comparable<T> attribute2)
  {
    int returnInt;
    if (attribute1.compareTo((T) attribute2) < 0)
    {
      returnInt = -1;
    }
    else if (attribute1.compareTo((T) attribute2) > 0)
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
    return returnInt;
  }

}
