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
package com.itude.mobile.mobbl2.client.core.services.datamanager.util;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public final class MBRequestUtil
{

  private MBRequestUtil()
  {
  }

  /**
   * @param value boolean value
   * @param key key
   * @param doc {@link MBDocument}
   */
  public static void setRequestParameter(boolean value, String key, MBDocument doc)
  {
    setRequestParameter("Operation", value, key, doc);
  }

  /**
   * @param rootElement root element
   * @param value boolean value
   * @param key key
   * @param doc {@link MBDocument}
   */
  public static void setRequestParameter(String rootElement, boolean value, String key, MBDocument doc)
  {
    setRequestParameter(rootElement, value ? Constants.C_TRUE : Constants.C_FALSE, key, doc);
  }

  /**
   * @param value value
   * @param key key
   * @param doc {@link MBDocument}
   */
  public static void setRequestParameter(String value, String key, MBDocument doc)
  {
    setRequestParameter("Operation", value, key, doc);
  }

  /**
   * @param rootElement root element
   * @param value value
   * @param key key
   * @param doc {@link MBDocument}
   */
  public static void setRequestParameter(String rootElement, String value, String key, MBDocument doc)
  {
    MBElement request = (MBElement) doc.getValueForPath(rootElement + "[0]");
    MBElement parameter = request.createElement("Parameter");
    parameter.setAttributeValue(key, "key");
    parameter.setAttributeValue(value, "value");
  }

}
