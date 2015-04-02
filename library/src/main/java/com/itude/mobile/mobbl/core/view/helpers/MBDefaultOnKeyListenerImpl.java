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
package com.itude.mobile.mobbl.core.view.helpers;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

import com.itude.mobile.mobbl.core.controller.MBViewManager;

public class MBDefaultOnKeyListenerImpl implements OnKeyListener {

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            MBViewManager.getInstance().onKeyDown(keyCode, event);
            return true;
        } else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_MENU) {
            MBViewManager.getInstance().onMenuKeyDown(keyCode, event, v);
            return true;
        }

        return false;
    }

}