package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBDialogNotDefinedException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -8763172107603449945L;

  public MBDialogNotDefinedException(String msg)
  {
    super(msg);
  }

  public MBDialogNotDefinedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
