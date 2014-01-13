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
package com.itude.mobile.mobbl.core.controller;

import android.util.Log;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl.core.MBException;
import com.itude.mobile.mobbl.core.controller.util.indicator.MBIndicator;
import com.itude.mobile.mobbl.core.controller.util.indicator.MBIndicator.Type;
import com.itude.mobile.mobbl.core.util.threads.MBThread;

public abstract class MBOutcomeTask<Result> implements Runnable
{
  private final MBOutcomeTaskManager _manager;

  public static class ResultContainer<T>
  {
    private T result;

    public void setResult(T result)
    {
      this.result = result;
    }

    public T getResult()
    {
      return result;
    }
  }

  protected static enum Threading {
    CURRENT, UI, BACKGROUND,
  };

  private final ResultContainer<Result> _container;

  public MBOutcomeTask(MBOutcomeTaskManager manager)
  {
    AssertUtil.notNull("manager", manager);
    _manager = manager;
    _container = new ResultContainer<Result>();
  }

  public ResultContainer<Result> getResultContainer()
  {
    return _container;
  }

  protected void setResult(Result result)
  {
    _container.setResult(result);
  }

  protected MBOutcome getOutcome()
  {
    return _manager.getOutcome();
  }

  protected MBIndicator showIndicator()
  {
    if ("exception".equals(getOutcome().getOutcomeName())) return MBIndicator.show(Type.none, getOutcome());
    if (getThreading() != Threading.BACKGROUND) return MBIndicator.show(Type.none, getOutcome());

    String indicator = getOutcome().getIndicator();
    if (indicator == null || "ACTIVITY".equals(indicator))
    {
      return MBIndicator.show(Type.activity, getOutcome());

    }
    else if ("PROGRESS".equals(indicator))
    {
      return MBIndicator.show(Type.indeterminate, getOutcome());

    }
    else throw new MBException("Unknown indicator type " + indicator);
  }

  protected Threading getThreading()
  {
    return Threading.CURRENT;
  }

  public void finished()
  {

  }

  public void start()
  {
    switch (getThreading())
    {
      case CURRENT :
        run();
        break;
      case UI :
        MBViewManager.getInstance().runOnUiThread(new MBThread(this));
        break;
      case BACKGROUND :
        new MBThread(this).start();
        break;
      default :
        throw new MBException("Unknown threading model " + getThreading());
    }
  }

  @Override
  public final void run()
  {
    MBIndicator indicator = showIndicator();
    try
    {
      Log.d(this.getClass().getSimpleName(), "Running outcome task for " + getOutcome());
      execute();
      _manager.finished(this);
    }
    finally
    {
      indicator.release();
    }
  }

  protected abstract void execute();

}
