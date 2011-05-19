package com.itude.mobile.mobbl2.client.core.util;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;

public class MBBundleDefinition extends MBDefinition
{
  private String _languageCode;
  private String _url;

  public String getLanguageCode()
  {
    return _languageCode;
  }

  public void setLanguageCode(String languageCode)
  {
    _languageCode = languageCode;
  }

  public String getUrl()
  {
    return _url;
  }

  public void setUrl(String url)
  {
    _url = url;
  }

}
