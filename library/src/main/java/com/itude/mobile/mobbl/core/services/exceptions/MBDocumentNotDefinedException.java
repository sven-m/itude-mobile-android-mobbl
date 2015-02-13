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
 * {@link MBException} class used when a document is not defined 
 *
 */
public class MBDocumentNotDefinedException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 2392748771879566275L;

  /**
   * Constructor for MBDocumentNotDefinedException.
   * 
   * @param msg exception message
   */
  public MBDocumentNotDefinedException(String msg)
  {
    super(msg);
  }

  /**
   * Constructor for MBDocumentNotDefinedException.
   * 
   * @param msg exception message
   * @param throwable throwable {@link Throwable}
   */
  public MBDocumentNotDefinedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
