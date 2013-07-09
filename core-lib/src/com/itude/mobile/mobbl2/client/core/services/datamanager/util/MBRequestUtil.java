package com.itude.mobile.mobbl2.client.core.services.datamanager.util;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBElement;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public final class MBRequestUtil
{
  public static void setRequestParameter(boolean value, String key, MBDocument doc)
  {
    setRequestParameter(value ? Constants.C_TRUE : Constants.C_FALSE, key, doc);
  }

  /**
   * @param root root element
   * @param value value
   * @param key key
   * @param doc {@link MBDocument}
   */
  public static void setRequestParameter(String value, String key, MBDocument doc)
  {
    MBElement request = (MBElement) doc.getValueForPath("Operation[0]");
    MBElement parameter = request.createElement("Parameter");
    parameter.setAttributeValue(key, "key");
    parameter.setAttributeValue(value, "value");
  }

}
