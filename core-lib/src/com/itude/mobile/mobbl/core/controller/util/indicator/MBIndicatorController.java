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
package com.itude.mobile.mobbl.core.controller.util.indicator;

import android.app.Activity;

import com.itude.mobile.mobbl.core.util.MBCustomAttributeContainer;

public class MBIndicatorController
{
  private static MBIndicatorController _instance;

  public static MBIndicatorController getInstance()
  {
    if (_instance == null) _instance = new MBIndicatorController();
    return _instance;
  }

  private MBCountingIndicator _activityIndicator      = new MBActivityIndicator();
  private MBCountingIndicator _indeterminateIndicator = new MBIndeterminateProgressIndicator();

  public void setActivityIndicator(MBCountingIndicator activityIndicator)
  {
    _activityIndicator = activityIndicator;
  }

  public void setIndeterminateIndicator(MBCountingIndicator indeterminateIndicator)
  {
    _indeterminateIndicator = indeterminateIndicator;
  }

  void showIndeterminateProgressIndicator(Activity activity, MBCustomAttributeContainer customAttributes)
  {
    if (_indeterminateIndicator != null) _indeterminateIndicator.increaseCount(activity, customAttributes);
  }

  void hideIndeterminateProgressIndicator(Activity activity)
  {
    if (_indeterminateIndicator != null) _indeterminateIndicator.decreaseCount(activity);
  }

  void showActivityIndicator(Activity activity, MBCustomAttributeContainer customAttributes)
  {
    if (_activityIndicator != null) _activityIndicator.increaseCount(activity, customAttributes);
  }

  void hideActivityIndicator(Activity activity)
  {
    if (_activityIndicator != null) _activityIndicator.decreaseCount(activity);
  }

}
