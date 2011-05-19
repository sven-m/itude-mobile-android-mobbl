package com.itude.mobile.mobbl2.client.core.model.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBUnknownDataTypeException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 8582328344519066950L;

  public MBUnknownDataTypeException(String msg)
  {
    super(msg);
  }

  public MBUnknownDataTypeException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
