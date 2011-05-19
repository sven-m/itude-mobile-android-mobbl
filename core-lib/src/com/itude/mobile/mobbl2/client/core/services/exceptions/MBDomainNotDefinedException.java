package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBDomainNotDefinedException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -1075187635800211060L;

  public MBDomainNotDefinedException(String msg)
  {
    super(msg);
  }

  public MBDomainNotDefinedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
