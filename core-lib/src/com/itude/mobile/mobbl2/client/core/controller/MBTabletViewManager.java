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
package com.itude.mobile.mobbl2.client.core.controller;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

/**
 * @author Coen Houtman
 *
 *  This ViewManager can be used to perform actions that cannot be done on pre-Honeycomb devices.
 *  For example the use of the ActionBar.
 */
@TargetApi(11)
public class MBTabletViewManager extends MBNextGenViewManager
{
  @Override
  protected void onPreCreate()
  {
    // empty to override request window feature to hide Android's standard indeterminate progress indicator
  }

  @Override
  protected void changeSearchImage(Drawable image, SearchView searchView)
  {
    if (image == null)
    {
      return;
    }

    try
    {
      // change the iconified icon
      Field searchButtonField = searchView.getClass().getDeclaredField("mSearchButton");
      searchButtonField.setAccessible(true);
      ImageView searchButton = (ImageView) searchButtonField.get(searchView);
      searchButton.setImageDrawable(image);

      // change the searchview
      Field searchEditField = searchView.getClass().getDeclaredField("mSearchEditFrame");
      searchEditField.setAccessible(true);
      LinearLayout searchLayout = (LinearLayout) searchEditField.get(searchView);

      LinearLayout searchPlate = (LinearLayout) searchLayout.getChildAt(0);
      MBViewBuilderFactory.getInstance().getStyleHandler().styleSearchPlate(searchPlate);

      // find first image view, assuming this is the icon we need
      setSearchImage(image, searchPlate);

      LinearLayout submitArea = (LinearLayout) searchLayout.getChildAt(1);
      MBViewBuilderFactory.getInstance().getStyleHandler().styleSearchSubmitArea(submitArea);
    }
    catch (Exception e)
    {
      Log.e(Constants.APPLICATION_NAME, "error changing searchbutton icon", e);
    }
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
    final View homeIcon = findViewById(android.R.id.home);
    if (homeIcon != null) new Handler().post(new Runnable()
    {

      @Override
      public void run()
      {
        MBViewBuilderFactory.getInstance().getStyleHandler().styleHomeIcon(homeIcon);
      }
    });

  }
}
