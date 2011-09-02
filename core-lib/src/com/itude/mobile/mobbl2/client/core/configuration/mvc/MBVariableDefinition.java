package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBVariableDefinition extends MBDefinition
{
  private String _expression;

  @Override
  public StringBuffer asXmlWithLevel(StringBuffer appendToMe, int level)
  {
    return StringUtilities.appendIndentString(appendToMe, level).append("<Variable name='").append(getName()).append("' expression='")
        .append(_expression).append("'/>\n");
  }

  public String getExpression()
  {
    return _expression;
  }

  public void setExpression(String expression)
  {
    _expression = expression;
  }
  
  @Override
  public String toString()
  {
    return asXmlWithLevel(new StringBuffer(), 0).toString();
  }

}
