package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBActionNotDefinedException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -19044856396571552L;

  public MBActionNotDefinedException(String msg)
  {
    super(msg);
  }

  public MBActionNotDefinedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
