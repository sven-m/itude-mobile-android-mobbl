package com.itude.mobile.mobbl2.client.core.util.threads;

import java.util.HashSet;

import android.util.Log;

import com.itude.mobile.mobbl2.client.core.util.Constants;

public final class MBThreadHandler
{
  private HashSet<MBThread>      _runningThreads = null;

  private static MBThreadHandler _instance       = null;

  private MBThreadHandler()
  {
    _runningThreads = new HashSet<MBThread>();
  }

  public static final MBThreadHandler getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBThreadHandler();
    }

    return _instance;
  }

  public void register(MBThread thread)
  {
    _runningThreads.add(thread);
  }

  public void unregister(MBThread thread)
  {
    _runningThreads.remove(thread);
  }

  public synchronized void stopAllRunningThreads()
  {
    Log.d(Constants.APPLICATION_NAME, "Stopping all running threads from MBThreadHandler");

    for (MBThread thread : _runningThreads)
    {
      thread.interrupt();
    }

    _runningThreads.clear();
  }
}
