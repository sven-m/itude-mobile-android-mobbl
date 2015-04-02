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
package com.itude.mobile.mobbl.core.view.components;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.itude.mobile.mobbl.core.util.MBScreenConstants;
import com.itude.mobile.mobbl.core.view.builders.MBViewBuilderFactory;

/**
 * @deprecated
 */
@Deprecated
public class MBSegmentedControl extends RadioGroup {
    public MBSegmentedControl(Context context) {
        super(context);
        setOrientation(RadioGroup.HORIZONTAL);
    }

    public void addItem(int id, String text) {
        addItem(id, getChildCount(), text);
    }

    public void addItem(int id, int index, String text) {
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT, 1);
        MBSegmentedItem item = new MBSegmentedItem(getContext());

        item.setPadding(0, MBScreenConstants.FIVE, 0, MBScreenConstants.FIVE);
        item.setId(id);
        item.setText(text);

        MBViewBuilderFactory.getInstance().getStyleHandler().styleSegmentedItem(item);

        addView(item, index, layoutParams);
    }
}
