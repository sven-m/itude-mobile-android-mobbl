package com.itude.mobile.mobbl2.client.core.view.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBInvalidComponentTypeException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 3539917309987523513L;

  public MBInvalidComponentTypeException(String msg)
  {
    super(msg);
  }

  public MBInvalidComponentTypeException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
