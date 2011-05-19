package com.itude.mobile.mobbl2.client.core.model.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBInvalidAttributeNameException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public MBInvalidAttributeNameException(String msg)
  {
    super(msg);
  }

  public MBInvalidAttributeNameException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
