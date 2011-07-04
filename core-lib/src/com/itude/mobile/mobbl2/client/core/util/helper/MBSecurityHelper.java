package com.itude.mobile.mobbl2.client.core.util.helper;

import com.itude.mobile.mobbl2.client.core.model.MBDocument;
import com.itude.mobile.mobbl2.client.core.model.MBSession;

public class MBSecurityHelper implements MBSecurityInterface
{

  private static MBSecurityInterface _instance;

  private MBSecurityHelper()
  {
  }

  public static MBSecurityInterface getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBSecurityHelper();
    }

    return _instance;
  }

  public static void setInstance(MBSecurityInterface helper)
  {
    _instance = helper;
  }

  public void logOutIfCheckNotSelected()
  {
    MBDocument sessionDoc = MBSession.getInstance().getDocument();
    if (sessionDoc.getValueForPath("Session[0]/@loggedIn").equals("true"))
    {
      MBSession.getInstance().logOff();
    }
  }
}
