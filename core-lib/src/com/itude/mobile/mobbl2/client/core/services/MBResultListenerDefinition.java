package com.itude.mobile.mobbl2.client.core.services;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;

public class MBResultListenerDefinition extends MBDefinition
{
  private String   _matchExpression;
  private String[] _matchParts;

  public String getMatchExpression()
  {
    return _matchExpression;
  }

  public void setMatchExpression(String matchExpression)
  {
    _matchExpression = matchExpression;
  }

  public String[] getMatchParts()
  {
    return _matchParts;
  }

  public void setMatchParts(String[] matchParts)
  {
    _matchParts = matchParts;
  }

  public boolean matches(String result)
  {
    boolean match = false;
    if (_matchParts == null)
    {
      _matchParts = _matchExpression.split("\\*");
    }
    for (String part : _matchParts)
    {
      if (result.indexOf(part) > -1)
      {
        match = true;
      }
      else
      {
        match = false;
      }
    }
    return match;
  }
}
