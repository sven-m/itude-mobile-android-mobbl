package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBAlertNotDefinedException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 4183802163615960775L;

  public MBAlertNotDefinedException(String msg)
  {
    super(msg);
  }

  public MBAlertNotDefinedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
