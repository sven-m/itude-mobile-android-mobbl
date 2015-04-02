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
package com.itude.mobile.mobbl.core.configuration.mvc.exceptions;

import com.itude.mobile.mobbl.core.MBException;

/**
 * {@link MBException} class used when a file is not found
 */
public class MBFileNotFoundException extends MBException {

    /**
     *
     */
    private static final long serialVersionUID = -3520108136271868523L;

    /**
     * Constructor for MBFileNotFoundException.
     *
     * @param msg exception message
     */
    public MBFileNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Constructor for MBFileNotFoundException.
     *
     * @param msg       exception message
     * @param throwable throwable {@link Throwable}
     */
    public MBFileNotFoundException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
