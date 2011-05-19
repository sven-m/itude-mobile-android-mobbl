package com.itude.mobile.mobbl2.client.core.util.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBDataParsingException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -1726186662862494940L;

  public MBDataParsingException(String msg)
  {
    super(msg);
  }

  public MBDataParsingException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
