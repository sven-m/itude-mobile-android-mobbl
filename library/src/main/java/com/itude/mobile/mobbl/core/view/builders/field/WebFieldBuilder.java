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
package com.itude.mobile.mobbl.core.view.builders.field;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl.core.controller.MBApplicationController;
import com.itude.mobile.mobbl.core.services.MBResourceService;
import com.itude.mobile.mobbl.core.view.MBField;

public class WebFieldBuilder extends MBBaseFieldBuilder {

    @Override
    public View buildField(MBField field) {
        WebView webView = new WebView(MBApplicationController.getInstance().getViewManager());
        webView.setScrollContainer(false);

        if (StringUtil.isNotBlank(field.getSource())) {
            webView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            String url = MBResourceService.getInstance().getUrlById(field.getSource());
            webView.loadUrl(url);
        } else {
            webView.loadDataWithBaseURL(null, field.getValuesForDisplay(), null, "UTF-8", null);

        }
        getStyleHandler().styleWebView(webView, field);

        return webView;
    }

}
