package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBScriptErrorException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -661654491873974277L;

  public MBScriptErrorException(String msg)
  {
    super(msg);
  }

  public MBScriptErrorException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
