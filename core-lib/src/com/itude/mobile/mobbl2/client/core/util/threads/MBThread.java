package com.itude.mobile.mobbl2.client.core.util.threads;

import android.os.Bundle;
import android.util.Log;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.threads.exception.MBInterruptedException;
import com.itude.mobile.mobbl2.client.core.view.MBPage;

public class MBThread extends Thread
{
  private MBPage           _page          = null;
  private Bundle           _parameters;
  private volatile boolean _interruptFlag = false;

  private final Runnable   _runnable;

  /**
   * Default constructor
   */
  public MBThread()
  {
    _runnable = null;
  }

  /**
   * @param page the page
   */
  public MBThread(MBPage page)
  {
    this(page, null);
  }

  /**
   * @param page
   * @param parameters a {@link android.os.Bundle} with parameters to use in {@link #runMethod()}. 
   * This can be used instead of making variables or parameters final, which can't even be done in some cases. 
   */
  public MBThread(MBPage page, Bundle parameters)
  {
    this();
    _page = page;
    _parameters = parameters;
  }

  public MBThread(Runnable runnable)
  {
    _runnable = runnable;
  }

  /**
   * @see java.lang.Runnable#run()
   */
  @Override
  public final void run()
  {
    try
    {
      MBThreadHandler.getInstance().register(this);
      if (_runnable == null)
      {
        runMethod();
      }
      else
      {
        _runnable.run();
      }
    }
    catch (MBInterruptedException e)
    {
      Log.w(Constants.APPLICATION_NAME, "Thread interrupted");
    }
    catch (Exception e)
    {
      handleException(e);
    }
    MBThreadHandler.getInstance().unregister(this);
  }

  protected void handleException(Exception e)
  {
    if (_page != null)
    {
      _page.handleException(e);
    }
    else
    {
      Log.w(Constants.APPLICATION_NAME, "Exception thrown in thread " + getName(), e);
    }
  }

  /**
   * The information that needs to be completed
   */
  public void runMethod() throws MBInterruptedException
  {

  }

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

  public void checkForInterruption() throws MBInterruptedException
  {
    if (isInterrupted())
    {
      throw new MBInterruptedException();
    }
  }

  @Override
  public void interrupt()
  {
    _interruptFlag = true;
  }

  @Override
  public boolean isInterrupted()
  {
    return _interruptFlag;
  }
}
