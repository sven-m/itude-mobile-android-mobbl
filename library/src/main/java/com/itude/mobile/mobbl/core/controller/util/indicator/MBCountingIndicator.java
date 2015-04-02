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
import com.itude.mobile.mobbl.core.util.threads.MBThread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Interface class defining the use of a indicator
 */
public abstract class MBCountingIndicator {
    private final AtomicInteger _queue = new AtomicInteger(0);
    private boolean _shown;

    /**
     * Add an indicator
     *
     * @param activity {@link Activity}
     */
    public void increaseCount(final Activity activity) {
        increaseCount(activity, MBCustomAttributeContainer.EMPTY);
    }

    /**
     * Add an indicator with custom attributes
     *
     * @param activity         {@link Activity}
     * @param customAttributes {@link MBCustomAttributeContainer}
     */
    public void increaseCount(final Activity activity, final MBCustomAttributeContainer customAttributes) {
        if (_queue.incrementAndGet() > 0) {

            activity.runOnUiThread(new MBThread() {
                @Override
                public void runMethod() {
                    if (_queue.get() > 0)

                        if (!_shown) {
                            show(activity, customAttributes);
                            _shown = true;
                        } else if (customAttributes.isHasCustomAttributes())
                            updateForAttributes(customAttributes);
                }
            });
        }
    }

    /**
     * Remove the indicator
     *
     * @param activity {@link Activity}
     */
    public void decreaseCount(final Activity activity) {
        if (_queue.decrementAndGet() <= 0) {

            activity.runOnUiThread(new MBThread() {
                @Override
                public void runMethod() {
                    if (_queue.get() <= 0 && _shown) {
                        dismiss(activity);
                        _shown = false;
                    }
                }
            });
        }

    }

    /**
     * Update the indicator with custom attributes
     *
     * @param customAttributes {@link MBCustomAttributeContainer}
     */
    protected void updateForAttributes(MBCustomAttributeContainer customAttributes) {
    }

    /**
     * Show the indicator
     *
     * @param activity         {@link Activity}
     * @param customAttributes {@link MBCustomAttributeContainer}
     */
    protected abstract void show(Activity activity, MBCustomAttributeContainer customAttributes);

    /**
     * Dismiss the indicator
     *
     * @param activity {@link Activity}
     */
    protected abstract void dismiss(Activity activity);
}
