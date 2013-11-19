/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
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
package com.itude.mobile.mobbl2.client.core.model;

import android.test.ApplicationTestCase;

import com.itude.mobile.mobbl2.client.core.MBApplicationCore;

public class MBExpressionTest extends ApplicationTestCase<MBApplicationCore>
{

  private static final String EXPRESSION_IN  = "${index}${${blerp[${index}]}[${index}]}${index}";
  private static final String EXPRESSION_OUT = "1Heuy!1";

  public MBExpressionTest()
  {
    super(MBApplicationCore.class);
  }

  public void testSubstitution()
  {
    TestElementContainer tec = new TestElementContainer();
    String out = tec.substituteExpressions(EXPRESSION_IN, null, null);
    assertEquals(EXPRESSION_OUT, out);

  }

  private static class TestElementContainer extends MBElementContainer
  {
    @Override
    public <T> T getValueForPath(String path)
    {
      if ("index".equals(path)) return (T) "1";
      else if ("blerp[1]".equals(path)) return (T) "whoop";
      else if ("whoop[1]".equals(path)) return (T) "Heuy!";
      else return (T) "MERP!";
    }
  }

}
