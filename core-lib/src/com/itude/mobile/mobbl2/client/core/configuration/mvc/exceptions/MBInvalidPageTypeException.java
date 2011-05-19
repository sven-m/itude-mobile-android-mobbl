package com.itude.mobile.mobbl2.client.core.configuration.mvc.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBInvalidPageTypeException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -7306721136666175447L;

  public MBInvalidPageTypeException(String msg)
  {
    super(msg);
  }

  public MBInvalidPageTypeException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
