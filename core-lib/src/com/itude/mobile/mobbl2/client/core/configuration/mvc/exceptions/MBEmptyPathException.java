package com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBEmptyPathException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -3612987590681693945L;

  public MBEmptyPathException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

  public MBEmptyPathException(String msg)
  {
    super(msg);
  }

}
