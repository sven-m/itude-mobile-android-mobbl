package com.itude.mobile.mobbl2.client.core.services;

public class MBSessionEvent extends MBEvent
{

  private final boolean _loggedIn;

  public MBSessionEvent(boolean loggedIn)
  {
    _loggedIn = loggedIn;
  }

  public boolean isLoggedIn()
  {
    return _loggedIn;
  }

}
