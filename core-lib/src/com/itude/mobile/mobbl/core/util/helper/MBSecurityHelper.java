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
package com.itude.mobile.mobbl.core.util.helper;

import com.itude.mobile.mobbl.core.model.MBDocument;
import com.itude.mobile.mobbl.core.model.MBSession;

public final class MBSecurityHelper implements MBSecurityInterface
{

  private static MBSecurityInterface _instance;

  private MBSecurityHelper()
  {
  }

  public static MBSecurityInterface getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBSecurityHelper();
    }

    return _instance;
  }

  public static void setInstance(MBSecurityInterface helper)
  {
    _instance = helper;
  }

  @Override
  public void logOutIfCheckNotSelected()
  {
    MBDocument sessionDoc = MBSession.getInstance().getDocument();
    if (sessionDoc != null && sessionDoc.getBooleanForPath("Session[0]/@loggedIn"))
    {
      MBSession.getInstance().logOff();
    }
  }
}
