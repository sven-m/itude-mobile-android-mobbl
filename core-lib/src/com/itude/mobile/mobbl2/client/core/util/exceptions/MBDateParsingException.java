package com.itude.mobile.mobbl2.client.core.util.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBDateParsingException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -3824251496748591427L;

  public MBDateParsingException(String msg)
  {
    super(msg);
  }

  public MBDateParsingException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
