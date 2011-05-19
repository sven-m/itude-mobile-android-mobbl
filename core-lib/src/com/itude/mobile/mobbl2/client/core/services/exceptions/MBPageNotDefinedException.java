package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBPageNotDefinedException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 4183802163615960775L;

  public MBPageNotDefinedException(String msg)
  {
    super(msg);
  }

  public MBPageNotDefinedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
