package com.itude.mobile.mobbl2.client.core.model.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBNoIndexSpecifiedException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 5559020541333037781L;

  public MBNoIndexSpecifiedException(String msg)
  {
    super(msg);
  }

  public MBNoIndexSpecifiedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
