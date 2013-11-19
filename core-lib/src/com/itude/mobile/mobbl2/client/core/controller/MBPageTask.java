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
package com.itude.mobile.mobbl2.client.core.controller;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController.PageBuildResult;
import com.itude.mobile.mobbl2.client.core.util.Constants;

public class MBPageTask extends MBOutcomeTask<PageBuildResult>
{

  private final MBPageDefinition _pageDefinition;

  public MBPageTask(MBOutcomeTaskManager manager, MBPageDefinition pageDefinition)
  {
    super(manager);
    _pageDefinition = pageDefinition;

  }

  public MBPageDefinition getPageDefinition()
  {
    return _pageDefinition;
  }

  @Override
  protected Threading getThreading()
  {
    return getOutcome().getNoBackgroundProcessing() ? Threading.CURRENT : Threading.BACKGROUND;
  }

  @Override
  protected void execute()
  {
    Log.d(Constants.APPLICATION_NAME, "Going to page " + getPageDefinition().getName());

    final MBApplicationController applicationController = MBApplicationController.getInstance();
    PageBuildResult result = applicationController.preparePage(new MBOutcome(getOutcome()), getPageDefinition().getName(),
                                                               applicationController.getBackStackEnabled());

    setResult(result);
  }

}
