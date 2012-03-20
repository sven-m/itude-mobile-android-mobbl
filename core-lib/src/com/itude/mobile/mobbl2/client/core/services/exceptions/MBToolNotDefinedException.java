package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBToolNotDefinedException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -4131950326282479541L;

  public MBToolNotDefinedException(String msg)
  {
    super(msg);
  }

  public MBToolNotDefinedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
