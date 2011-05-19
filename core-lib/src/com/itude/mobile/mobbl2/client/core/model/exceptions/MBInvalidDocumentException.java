package com.itude.mobile.mobbl2.client.core.model.exceptions;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBInvalidDocumentException extends MBException
{

  /**
   * 
   */
  private static final long serialVersionUID = 5444574699024339383L;

  public MBInvalidDocumentException(String msg)
  {
    super(msg);
  }

  public MBInvalidDocumentException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

}
