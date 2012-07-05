package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBResourceInvalidException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -5871849971476543264L;

  public MBResourceInvalidException(String msg)
  {
    super(msg);
  }

  public MBResourceInvalidException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
