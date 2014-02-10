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
package com.itude.mobile.mobbl.core.util.threads;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.itude.mobile.mobbl.core.util.Constants;

public final class MBThreadHandler
{
  private final Set<MBThread>    _runningThreads;

  private static MBThreadHandler _instance = null;

  @TargetApi(Build.VERSION_CODES.GINGERBREAD)
  private MBThreadHandler()
  {
    Set<MBThread> set;
    try
    {
      set = new ConcurrentSkipListSet<MBThread>(new Comparator<MBThread>()
      {

        @Override
        public int compare(MBThread lhs, MBThread rhs)
        {
          return (int) (lhs.getId() - rhs.getId());
        }

      });
    }
    catch (Exception e)
    {
      set = Collections.synchronizedSet(new HashSet<MBThread>());
    }
    _runningThreads = set;
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
