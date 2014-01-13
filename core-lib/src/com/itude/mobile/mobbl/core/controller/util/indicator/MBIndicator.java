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

import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.controller.MBViewManager;
import com.itude.mobile.mobbl.core.util.MBCustomAttributeContainer;

public class MBIndicator
{
  // type 'none' obviously doesn't show anything; it's there to have a handle for some fringe cases in which we show indicators pretty much all of the time, with some small exceptions
  public enum Type {
    none, indeterminate, activity
  };

  private final Type     _type;
  private boolean        _active;
  private final Activity _activity;

  protected MBIndicator(Type type)
  {
    _type = type;
    _active = true;
    _activity = MBViewManager.getInstance();
  }

  protected MBIndicator(Activity activity, Type type)
  {
    _type = type;
    _active = true;
    _activity = activity;
  }

  private void showIndicator(MBCustomAttributeContainer customAttributes)
  {
    switch (_type)
    {
      case indeterminate :
        MBIndicatorController.getInstance().showIndeterminateProgressIndicator(_activity, customAttributes);
        break;
      case activity :
        MBIndicatorController.getInstance().showActivityIndicator(_activity, customAttributes);
        break;
      case none :
        break;
    }
  }

  private void hideIndicator()
  {
    switch (_type)
    {
      case indeterminate :
        MBIndicatorController.getInstance().hideIndeterminateProgressIndicator(_activity);
        break;
      case activity :
        MBIndicatorController.getInstance().hideActivityIndicator(_activity);
        break;
      case none :
        break;
    }

  }

  public void release()
  {
    if (!_active) throw new MBException("Trying to release already released MBIndicator");
    _active = false;
    hideIndicator();
  }

  /**
   * @deprecated Only exists to support applications that don't cleanly use the framework, like Actum. Don't use this in any new application! 
   */
  @Deprecated
  public static MBIndicator show(Activity activity, Type type)
  {
    MBIndicator indicator = new MBIndicator(activity, type);
    indicator.showIndicator(MBCustomAttributeContainer.EMPTY);
    return indicator;

  }

  public static MBIndicator show(Type type)
  {
    return show(type, MBCustomAttributeContainer.EMPTY);
  }

  public static MBIndicator show(Type type, MBCustomAttributeContainer customAttributes)
  {
    MBIndicator indicator = new MBIndicator(type);
    indicator.showIndicator(customAttributes);
    return indicator;
  }
}
