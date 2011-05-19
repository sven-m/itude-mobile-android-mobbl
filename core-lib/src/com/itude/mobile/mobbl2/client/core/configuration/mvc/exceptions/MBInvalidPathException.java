package com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBInvalidPathException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 9152312521594480636L;

  public MBInvalidPathException(String msg)
  {
    super(msg);
  }

  public MBInvalidPathException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
