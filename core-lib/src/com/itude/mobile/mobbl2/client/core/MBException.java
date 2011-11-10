package com.itude.mobile.mobbl2.client.core;

public class MBException extends RuntimeException
{
  private String            _name            = null;

  /**
   * 
   */
  private static final long serialVersionUID = 1271249723743935918L;

  public MBException()
  {

  }

  public MBException(String msg)
  {
    super(msg);
  }

  public MBException(String name, String msg)
  {
    this(msg);
    setName(name);
  }

  public MBException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }

  public void setName(String name)
  {
    _name = name;
  }

  public String getName()
  {
    return _name;
  }

}
