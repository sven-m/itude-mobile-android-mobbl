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
