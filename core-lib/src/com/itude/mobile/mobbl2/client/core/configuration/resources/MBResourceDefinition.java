package com.itude.mobile.mobbl2.client.core.configuration.resources;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;

public class MBResourceDefinition extends MBDefinition
{
  private String  _resourceId;
  private String  _url;
  private boolean _cacheable;
  private int     _ttl;

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

}
