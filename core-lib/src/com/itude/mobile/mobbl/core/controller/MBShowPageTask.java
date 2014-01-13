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
package com.itude.mobile.mobbl.core.controller;

import com.itude.mobile.mobbl.core.controller.MBApplicationController.PageBuildResult;

public class MBShowPageTask extends MBOutcomeTask<Object>
{

  private final ResultContainer<PageBuildResult> _page;

  public MBShowPageTask(MBOutcomeTaskManager manager, ResultContainer<PageBuildResult> page)
  {
    super(manager);
    _page = page;

  }

  @Override
  protected com.itude.mobile.mobbl.core.controller.MBOutcomeTask.Threading getThreading()
  {
    return Threading.UI;
  }

  @Override
  protected void execute()
  {
    MBApplicationController.getInstance().showResultingPage(_page.getResult());
  }

}
