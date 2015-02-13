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

import android.content.res.Configuration;
import android.os.Handler;
import android.view.View;

import com.itude.mobile.mobbl.core.view.builders.MBViewBuilderFactory;
import com.itude.mobile.mobbl.core.view.components.tabbar.MBActionBarBuilder;
import com.itude.mobile.mobbl.core.view.components.tabbar.MBTabletActionBarBuilder;

/**
 *  This {@link MBShutdownHandler} can be used to perform actions that cannot be done on pre-Honeycomb devices.
 *  For example the use of the ActionBar.
 */
public class MBTabletViewManager extends MBViewManager
{
  @Override
  protected void onPreCreate()
  {
    // empty to override request window feature to hide Android's standard indeterminate progress indicator
  }

  // End of Android hooks

  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    super.onConfigurationChanged(newConfig);

    // Android 3.2 (perhaps other versions, but not 3.0) destroys the styling of the home icon in the action bar
    // when an onConfigurationChanged is triggered. When the main thread is about here, the handling of
    // that onConfigurationChanged should be in the message queue. This places a new message at the end of
    // the queue that changes the home icon back, which is therefore handled after the onConfigurationChanged message
    // that ruins it.
    final View homeIcon = findViewById(com.itude.mobile.mobbl.core.R.id.home);
    if (homeIcon != null) new Handler().post(new Runnable()
    {

      @Override
      public void run()
      {
        MBViewBuilderFactory.getInstance().getStyleHandler().styleHomeIcon(homeIcon);
      }
    });

  }

  @Override
  protected MBActionBarBuilder getDefaultActionBar()
  {
    return new MBTabletActionBarBuilder(this);
  }
}
