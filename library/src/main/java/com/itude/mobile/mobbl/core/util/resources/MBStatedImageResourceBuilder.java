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

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.itude.mobile.mobbl.core.util.MBConstants;
import com.itude.mobile.mobbl.core.view.MBItem;
import com.itude.mobile.mobbl.core.view.MBResource;

import java.util.Map;

public class MBStatedImageResourceBuilder extends MBAbstractStatedResourceBuilder {

    @Override
    public Drawable buildResource(MBResource resource) {
        Map<String, MBItem> items = resource.getItems();

        MBItem enabled = items.get(MBConstants.C_STATED_RESOURCE_STATE_ENABLED);
        MBItem selected = items.get(MBConstants.C_STATED_RESOURCE_STATE_SELECTED);
        MBItem pressed = items.get(MBConstants.C_STATED_RESOURCE_STATE_PRESSED);
        MBItem disabled = items.get(MBConstants.C_STATED_RESOURCE_STATE_DISABLED);
        MBItem checked = items.get(MBConstants.C_STATED_RESOURCE_STATE_CHECKED);

        StateListDrawable stateListDrawable = new StateListDrawable();

        if (pressed != null) {
            int[] itemStates = new int[]{android.R.attr.state_pressed};

            processItem(stateListDrawable, pressed, itemStates);
        }

        if (enabled != null) {
            int[] itemStates = new int[]{android.R.attr.state_enabled, -android.R.attr.state_selected};

            processItem(stateListDrawable, enabled, itemStates);
        }

        if (disabled != null) {
            int[] itemStates = new int[]{-android.R.attr.state_enabled};

            processItem(stateListDrawable, disabled, itemStates);
        }

        if (selected != null) {
            int[] itemStates = new int[]{android.R.attr.state_selected};

            processItem(stateListDrawable, selected, itemStates);
        }
        if (checked != null) {
            int[] itemStates = new int[]{android.R.attr.state_checked};

            processItem(stateListDrawable, checked, itemStates);
        }

        return stateListDrawable;
    }
}
