package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;

public class MBActionDefinition extends MBDefinition
{
  private String _className;

  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtil.appendIndentString(appendToMe, level)
                    .append("<Action name='")
                    .append(getName())
                    .append("' className='")
                    .append(getClassName())
                    .append("'/>\n");
  }

  public String getClassName()
  {
    return _className;
  }

  public void setClassName(String className)
  {
    _className = className;
  }

}
