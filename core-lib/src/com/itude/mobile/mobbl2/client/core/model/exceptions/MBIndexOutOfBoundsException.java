package com.itude.mobile.mobbl2.client.core.model.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBIndexOutOfBoundsException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 4498476068805513875L;

  public MBIndexOutOfBoundsException(String msg)
  {
    super(msg);
  }

  public MBIndexOutOfBoundsException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
