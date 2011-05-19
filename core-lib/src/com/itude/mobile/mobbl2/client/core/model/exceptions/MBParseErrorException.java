package com.itude.mobile.mobbl2.client.core.model.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBParseErrorException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 4941998504577182545L;

  public MBParseErrorException(String msg)
  {
    super(msg);
  }

  public MBParseErrorException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
