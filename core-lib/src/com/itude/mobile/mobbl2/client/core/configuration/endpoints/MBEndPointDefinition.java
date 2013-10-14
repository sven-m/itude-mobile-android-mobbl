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
package com.itude.mobile.mobbl2.client.core.configuration.endpoints;

import java.util.ArrayList;
import java.util.List;

import com.itude.mobile.mobbl2.client.core.configuration.MBDefinition;
import com.itude.mobile.mobbl2.client.core.services.MBResultListenerDefinition;

public class MBEndPointDefinition extends MBDefinition
{
  private String                                 _documentIn;
  private String                                 _documentOut;
  private String                                 _endPointUri;
  private final List<MBResultListenerDefinition> _resultListeners;
  private boolean                                _cacheable;
  private int                                    _timeout;
  private int                                    _ttl;

  public MBEndPointDefinition()
  {
    _resultListeners = new ArrayList<MBResultListenerDefinition>();
  }

  public MBEndPointDefinition(MBEndPointDefinition mbEndPointDefinition)
  {
    _documentIn = mbEndPointDefinition.getDocumentIn();
    _documentOut = mbEndPointDefinition.getDocumentOut();
    _endPointUri = mbEndPointDefinition.getEndPointUri();
    _resultListeners = mbEndPointDefinition.getResultListeners();
    _cacheable = mbEndPointDefinition.getCacheable();
    _timeout = mbEndPointDefinition.getTimeout();
    _ttl = mbEndPointDefinition.getTtl();
  }

  public String getDocumentIn()
  {
    return _documentIn;
  }

  public void setDocumentIn(String documentIn)
  {
    _documentIn = documentIn;
  }

  public String getDocumentOut()
  {
    return _documentOut;
  }

  public void setDocumentOut(String documentOut)
  {
    _documentOut = documentOut;
  }

  public String getEndPointUri()
  {
    return _endPointUri;
  }

  public void setEndPointUri(String endPointUri)
  {
    _endPointUri = endPointUri;
  }

  public boolean getCacheable()
  {
    return _cacheable;
  }

  public void setCacheable(boolean cacheable)
  {
    _cacheable = cacheable;
  }

  public int getTimeout()
  {
    return _timeout;
  }

  public void setTimeout(int timeout)
  {
    _timeout = timeout;
  }

  public int getTtl()
  {
    return _ttl;
  }

  public void setTtl(int ttl)
  {
    _ttl = ttl;
  }

  @Override
  public void addResultListener(MBResultListenerDefinition lsnr)
  {
    _resultListeners.add(lsnr);
  }

  public List<MBResultListenerDefinition> getResultListeners()
  {
    return _resultListeners;
  }
}
