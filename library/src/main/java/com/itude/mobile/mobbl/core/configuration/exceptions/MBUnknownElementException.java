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
package com.itude.mobile.mobbl.core.configuration.exceptions;

import com.itude.mobile.mobbl.core.MBException;

/**
 * {@link MBException} class used when elements are unknown
 */
public class MBUnknownElementException extends MBException {

    /**
     *
     */
    private static final long serialVersionUID = -4763653374081391672L;

    /**
     * Constructor for MBUnknownElementException.
     *
     * @param msg exception message
     */
    public MBUnknownElementException(String msg) {
        super(msg);
    }

    /**
     * Constructor for MBUnknownElementException.
     *
     * @param msg       exception message
     * @param throwable throwable {@link Throwable}
     */
    public MBUnknownElementException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
