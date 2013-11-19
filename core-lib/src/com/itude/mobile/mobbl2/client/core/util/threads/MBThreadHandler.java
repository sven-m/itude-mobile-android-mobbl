/*
 * (C) Copyright Itude Mobile B.V., The Netherlands
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
