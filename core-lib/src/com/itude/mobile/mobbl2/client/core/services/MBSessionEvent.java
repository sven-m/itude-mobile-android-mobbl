package com.itude.mobile.mobbl2.client.core.services;

public class MBSessionEvent extends MBEvent
{

  private boolean loggedIn;

  public MBSessionEvent()
  {

  }

  public MBSessionEvent(boolean loggedIn)
  {
    this.loggedIn = loggedIn;
  }

  public void setLoggedIn(boolean loggedIn)
  {
    this.loggedIn = loggedIn;
  }

  public boolean isLoggedIn()
  {
    return this.loggedIn;
  }

}
