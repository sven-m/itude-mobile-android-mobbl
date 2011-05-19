package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBDocumentNotDefinedException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 2392748771879566275L;

  public MBDocumentNotDefinedException(String msg)
  {
    super(msg);
  }

  public MBDocumentNotDefinedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
