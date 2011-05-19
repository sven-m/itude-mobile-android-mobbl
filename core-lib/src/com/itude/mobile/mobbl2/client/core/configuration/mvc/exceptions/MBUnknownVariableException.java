package com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBUnknownVariableException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 4160255176791154859L;

  public MBUnknownVariableException(String msg)
  {
    super(msg);
  }

  public MBUnknownVariableException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
