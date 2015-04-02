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
package com.itude.mobile.mobbl.core;

/**
 * Default Exception Class
 */
public class MBException extends RuntimeException {
    private String _name = null;

    /**
     *
     */
    private static final long serialVersionUID = 1271249723743935918L;

    public MBException() {

    }

    /**
     * Constructor for MBException.
     *
     * @param msg exception message
     */
    public MBException(String msg) {
        super(msg);
    }

    /**
     * Constructor for MBException.
     *
     * @param name name of exception
     * @param msg  exception message
     */
    public MBException(String name, String msg) {
        this(msg);
        setName(name);
    }

    /**
     * Constructor for MBException.
     *
     * @param msg       exception message
     * @param throwable throwable {@link Throwable}
     */
    public MBException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    /**
     * @param name name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * @return name
     */
    public String getName() {
        return _name;
    }

}
