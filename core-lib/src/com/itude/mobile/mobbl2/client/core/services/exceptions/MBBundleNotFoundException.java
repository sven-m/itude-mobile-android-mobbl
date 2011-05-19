package com.itude.mobile.mobbl2.client.core.services.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBBundleNotFoundException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -4475909702846830986L;

  public MBBundleNotFoundException(String msg)
  {
    super(msg);
  }

  public MBBundleNotFoundException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
