package com.itude.mobile.mobbl2.client.core.util.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBInvalidRelativePathException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 7936676805593582175L;

  public MBInvalidRelativePathException(String msg)
  {
    super(msg);
  }

  public MBInvalidRelativePathException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
