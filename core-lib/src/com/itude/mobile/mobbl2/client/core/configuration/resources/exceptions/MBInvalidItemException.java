package com.itude.mobile.mobbl2.client.core.configuration.resources.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBInvalidItemException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -4940356575580672785L;

  public MBInvalidItemException(String msg)
  {
    super(msg);
  }

  public MBInvalidItemException(String name, String msg)
  {
    super(name, msg);
  }

  public MBInvalidItemException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
