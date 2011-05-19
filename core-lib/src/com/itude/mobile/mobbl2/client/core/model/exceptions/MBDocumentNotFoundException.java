package com.itude.mobile.mobbl2.client.core.model.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBDocumentNotFoundException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = -1729729464319255262L;

  public MBDocumentNotFoundException(String msg)
  {
    super(msg);
  }

  public MBDocumentNotFoundException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
