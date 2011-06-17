package com.itude.mobile.mobbl2.client.core.util.exceptions;

import com.itude.mobile.mobbl2.client.core.view.MBPage;

public abstract class MBRunnable implements Runnable
{

  private MBPage _page = null;

  /**
   * Default constructor
   */
  public MBRunnable()
  {
  }

  /**
   * @param page the page
   */
  public MBRunnable(MBPage page)
  {
    _page = page;
  }

  /**
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    try
    {
      runMethod();
    }
    catch (Exception e)
    {
      handleException(e);
    }
  }

  protected void handleException(Exception e)
  {
    if (_page != null) _page.handleException(e);
  }

  /**
   * The information that needs to be completed
   */
  public abstract void runMethod();
}
