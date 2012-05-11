package com.itude.mobile.mobbl2.client.core.configuration.resources;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBItemDefinition extends MBDefinition
{
  private String _resource;
  private String _state;

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtilities.appendIndentString(appendToMe, level).append("<Item resource='").append(_resource).append("'")
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
}
