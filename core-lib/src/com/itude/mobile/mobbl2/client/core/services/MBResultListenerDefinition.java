/*
 * (C) Copyright ItudeMobile.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    if (matchParts != null)
    {
      _matchParts = matchParts.clone();
    }
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
