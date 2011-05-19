package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBResourceNotDefinedException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -5754123787762535939L;
  
  public MBResourceNotDefinedException(String msg)
  {
    super(msg);
  }
  
  public MBResourceNotDefinedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
