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
package com.itude.mobile.mobbl.core.services;

public class MBRefreshEvent extends MBEvent
{

  private boolean _forceDocumentRefreshing = false;

  public MBRefreshEvent()
  {
    super();
  }

  public MBRefreshEvent(boolean forceDocumentRefreshing)
  {
    this._forceDocumentRefreshing = forceDocumentRefreshing;
  }

  public boolean isForceDocumentRefreshing()
  {
    return _forceDocumentRefreshing;
  }

  public void setForceDocumentRefreshing(boolean forceDocumentRefreshing)
  {
    this._forceDocumentRefreshing = forceDocumentRefreshing;
  }

}
