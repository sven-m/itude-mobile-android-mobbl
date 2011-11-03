package com.itude.mobile.mobbl2.client.core.services;

public class MBSessionEvent extends MBEvent
{

  private final boolean loggedIn;

  public MBSessionEvent(boolean loggedIn)
  {
    this.loggedIn = loggedIn;
  }

  public boolean isLoggedIn()
  {
    return this.loggedIn;
  }

}
