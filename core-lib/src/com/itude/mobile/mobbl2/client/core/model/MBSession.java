package com.itude.mobile.mobbl2.client.core.model;

public class MBSession implements MBSessionInterface
{

  private static MBSessionInterface _instance;

  private MBSession()
  {

  }

  public static MBSessionInterface getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBSession();
    }

    return _instance;
  }

  public static void setInstance(MBSessionInterface session)
  {
    _instance = session;
  }

  //
  //Override the following methods in an instance specific for your app; and register it app startup with setSharedInstance
  //
  public MBDocument getDocument()
  {
    return null;
  }

  public void logOff()
  {
  }

  public boolean isLoggedOn()
  {
    return false;
  }

}
