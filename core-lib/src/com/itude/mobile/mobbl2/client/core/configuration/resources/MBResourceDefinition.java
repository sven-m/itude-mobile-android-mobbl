package com.itude.mobile.mobbl2.client.core.configuration.resources;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBResourceDefinition extends MBDefinition
{
  private String  _resourceId;
  private String  _url;
  private String  _color;
  private String  _align;
  private boolean _cacheable;
  private int     _ttl;

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtilities.appendIndentString(appendToMe, level).append("<Resource id='").append(getResourceId()).append("' url='")
        .append(getUrl()).append("' cacheable='").append(getCacheable()).append("' ttl='").append(getTtl()).append("' color='")
        .append(getColor()).append("' align='").append(getAlign()).append("' />");
  }

  public String getResourceId()
  {
    return _resourceId;
  }

  public void setResourceId(String resourceId)
  {
    _resourceId = resourceId;
  }

  public String getUrl()
  {
    return _url;
  }

  public void setUrl(String url)
  {
    _url = url;
  }

  public boolean getCacheable()
  {
    return _cacheable;
  }

  public void setCacheable(boolean cacheable)
  {
    _cacheable = cacheable;
  }

  public int getTtl()
  {
    return _ttl;
  }

  public void setTtl(int ttl)
  {
    _ttl = ttl;
  }

  public String getColor()
  {
    return _color;
  }

  public void setColor(String color)
  {
    _color = color;
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
