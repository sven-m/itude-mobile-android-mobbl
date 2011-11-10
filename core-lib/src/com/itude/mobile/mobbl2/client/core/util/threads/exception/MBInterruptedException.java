package com.itude.mobile.mobbl2.client.core.util.threads.exception;

import com.itude.mobile.mobbl2.client.core.MBException;

public class MBInterruptedException extends MBException
{

  private static final long serialVersionUID = 2364692866749915559L;

  public MBInterruptedException(String name, String msg)
  {
    super(name, msg);
  }

  public MBInterruptedException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

  public MBInterruptedException(String msg)
  {
    super(msg);
  }

  public MBInterruptedException()
  {
    super();
  }

}
