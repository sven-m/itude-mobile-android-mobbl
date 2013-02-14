package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;

public class MBBundleDefinition extends MBDefinition
{
  private String _languageCode;
  private String _url;

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtil.appendIndentString(appendToMe, level).append("<Bundle languageCode='").append(getLanguageCode()).append("' url='")
        .append(getUrl()).append("' />");
  }

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
