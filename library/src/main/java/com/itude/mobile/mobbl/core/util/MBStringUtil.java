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
package com.itude.mobile.mobbl.core.util;

import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;

public final class MBStringUtil {
    private static NumberKeyListener _currencyNumberKeyListener;

    private MBStringUtil() {

    }

    public static Spanned fromHTML(String textToTransform) {
        return Html.fromHtml(textToTransform);
    }

    public static NumberKeyListener getCurrencyNumberKeyListener() {
        if (_currencyNumberKeyListener == null) {
            _currencyNumberKeyListener = new NumberKeyListener() {

                @Override
                public int getInputType() {
                    return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                }

                @Override
                protected char[] getAcceptedChars() {
                    return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ','};
                }
            };
        }

        return _currencyNumberKeyListener;
    }
}
