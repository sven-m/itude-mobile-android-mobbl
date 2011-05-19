package com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBFileNotFoundException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -3520108136271868523L;

  public MBFileNotFoundException(String msg)
  {
    super(msg);
  }

  public MBFileNotFoundException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
