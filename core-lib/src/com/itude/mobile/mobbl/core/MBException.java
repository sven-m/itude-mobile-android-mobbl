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
package com.itude.mobile.mobbl.core;

public class MBException extends RuntimeException
{
  private String            _name            = null;

  /**
   * 
   */
  private static final long serialVersionUID = 1271249723743935918L;

  public MBException()
  {

  }

  public MBException(String msg)
  {
    super(msg);
  }

  public MBException(String name, String msg)
  {
    this(msg);
    setName(name);
  }

  public MBException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

  public void setName(String name)
  {
    _name = name;
  }

  public String getName()
  {
    return _name;
  }

}
