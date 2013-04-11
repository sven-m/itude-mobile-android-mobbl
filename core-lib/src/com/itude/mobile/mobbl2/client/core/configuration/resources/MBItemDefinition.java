package com.itude.mobile.mobbl2.client.core.configuration.resources;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;

public class MBItemDefinition extends MBDefinition
{
  private String _resource;
  private String _state;
  private String _align;

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtil.appendIndentString(appendToMe, level).append("<Item resource='").append(_resource).append("'")
        .append(getAttributeAsXml("state", _state)).append("/>");
  }

  @Override
  public String toString()
  {
    return asXmlWithLevel(new StringBuffer(), 0).toString();
  }

  public void setResource(String resource)
  {
    _resource = resource;
  }

  public String getResource()
  {
    return _resource;
  }

  public void setState(String state)
  {
    _state = state;
  }

  public String getState()
  {
    return _state;
  }

  public String getAlign()
  {
    return _align;
  }

  public void setAlign(String align)
  {
    _align = align;
  }
}
