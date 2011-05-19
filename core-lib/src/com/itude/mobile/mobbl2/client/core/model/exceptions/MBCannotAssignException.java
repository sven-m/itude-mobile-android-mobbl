package com.itude.mobile.mobbl2.client.core.model.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBCannotAssignException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -4515819821558295854L;

  public MBCannotAssignException(String msg)
  {
    super(msg);
  }

  public MBCannotAssignException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
