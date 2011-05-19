package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBActionDefinition extends MBDefinition
{
  private String _className;

  public StringBuffer asXmlWithLevel(StringBuffer p_appendToMe, int level)
  {
    return StringUtilities.appendIndentString(p_appendToMe, level)
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
