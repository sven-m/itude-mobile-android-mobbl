package com.itude.mobile.mobbl2.client.core.util.exceptions;

import android.os.Bundle;

import com.itude.mobile.mobbl2.client.core.view.MBPage;

public abstract class MBRunnable implements Runnable
{
  private MBPage _page = null;
  private Bundle _parameters;

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
    this(page, null);
  }

  /**
   * @param page
   * @param parameters a {@link android.os.Bundle} with parameters to use in {@link #runMethod()}. 
   * This can be used instead of making variables or parameters final, which can't even be done in some cases. 
   */
  public MBRunnable(MBPage page, Bundle parameters)
  {
    _page = page;
    _parameters = parameters;
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

  /**
   * Get a {@link #java.lang.String} value from the provided {@link android.os.Bundle}.
   * @param key
   * @return null if there is no Bundle available; otherwise @see {@link android.os.Bundle#get(String)}
   */
  protected final String getStringParameter(String key)
  {
    if (_parameters == null) return null;

    return _parameters.getString(key);
  }
}
