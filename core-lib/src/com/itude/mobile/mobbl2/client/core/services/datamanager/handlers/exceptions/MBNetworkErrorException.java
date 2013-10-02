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
package com.itude.mobile.mobbl2.client.core.services.datamanager.handlers.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.services.MBLocalizationService;

public class MBNetworkErrorException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 3091172841776637109L;

  public MBNetworkErrorException(String msg)
  {
    super(MBLocalizationService.getInstance().getTextForKey(msg));
  }

}
