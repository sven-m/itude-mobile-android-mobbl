package com.itude.mobile.mobbl2.client.core.configuration.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBUnknownElementException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -4763653374081391672L;

  public MBUnknownElementException(String msg)
  {
    super(msg);
  }

  public MBUnknownElementException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
