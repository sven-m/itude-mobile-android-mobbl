package com.itude.mobile.mobbl2.client.core.configuration.mvc;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.util.StringUtilities;

public class MBVariableDefinition extends MBDefinition
{
  private String _expression;

  public StringBuffer asXmlWithLevel(StringBuffer p_appendToMe, int level)
  {
    return StringUtilities.appendIndentString(p_appendToMe, level).append("<Variable name='").append(getName()).append("' expression='")
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

}
