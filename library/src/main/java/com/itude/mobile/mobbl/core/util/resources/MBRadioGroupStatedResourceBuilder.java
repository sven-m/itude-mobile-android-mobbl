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
package com.itude.mobile.mobbl.core.util.resources;

import java.util.Map;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.itude.mobile.mobbl.core.util.Constants;
import com.itude.mobile.mobbl.core.view.MBItem;
import com.itude.mobile.mobbl.core.view.MBResource;

public class MBRadioGroupStatedResourceBuilder extends MBAbstractStatedResourceBuilder
{

  @Override
  public Drawable buildResource(MBResource resource)
  {
    Map<String, MBItem> items = resource.getItems();

    MBItem checked = items.get(Constants.C_STATED_RESOURCE_STATE_CHECKED);
    MBItem unchecked = items.get(Constants.C_STATED_RESOURCE_STATE_UNCHECKED);

    StateListDrawable stateListDrawable = new StateListDrawable();

    if (checked != null)
    {
      int[] itemStates = new int[]{android.R.attr.state_checked};

      processItem(stateListDrawable, checked, itemStates);
    }
    if (unchecked != null)
    {
      int[] itemStates = new int[]{-android.R.attr.state_checked};

      processItem(stateListDrawable, unchecked, itemStates);
    }

    return stateListDrawable;
  }

}
