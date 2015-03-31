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

import com.itude.mobile.android.util.log.MBLog;
import com.itude.mobile.mobbl.core.configuration.mvc.MBPageDefinition;
import com.itude.mobile.mobbl.core.controller.MBApplicationController.PageBuildResult;
import com.itude.mobile.mobbl.core.util.MBConstants;

/**
 * {@link MBOutcomeTask} class describing a page task
 */
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
    MBLog.d(MBConstants.APPLICATION_NAME, "Going to page " + getPageDefinition().getName());

    final MBApplicationController applicationController = MBApplicationController.getInstance();
    PageBuildResult result = applicationController.preparePage(new MBOutcome(getOutcome()), getPageDefinition().getName(),
                                                               applicationController.getBackStackEnabled());

    setResult(result);
  }

}
