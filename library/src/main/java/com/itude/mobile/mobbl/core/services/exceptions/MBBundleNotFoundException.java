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
package com.itude.mobile.mobbl.core.services.exceptions;

import com.itude.mobile.mobbl.core.MBException;

/**
 * {@link MBException} class used when a bundle is not found
 */
public class MBBundleNotFoundException extends MBException {

    /**
     *
     */
    private static final long serialVersionUID = -4475909702846830986L;

    /**
     * Constructor for MBBundleNotFoundException.
     *
     * @param msg exception message
     */
    public MBBundleNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Constructor for MBBundleNotFoundException.
     *
     * @param msg       exception message
     * @param throwable throwable {@link Throwable}
     */
    public MBBundleNotFoundException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
