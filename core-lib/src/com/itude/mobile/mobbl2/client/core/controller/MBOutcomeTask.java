package com.itude.mobile.mobbl2.client.core.controller;

import com.itude.mobile.android.util.AssertUtil;
import com.itude.mobile.mobbl2.client.core.MBException;
import com.itude.mobile.mobbl2.client.core.controller.util.indicator.MBIndicator;
import com.itude.mobile.mobbl2.client.core.controller.util.indicator.MBIndicator.Type;
import com.itude.mobile.mobbl2.client.core.util.threads.MBThread;

public abstract class MBOutcomeTask implements Runnable
{
  private final MBOutcomeTaskManager _manager;

  protected static enum Threading {
    CURRENT, UI, BACKGROUND,
  };

  public MBOutcomeTask(MBOutcomeTaskManager manager)
  {
    AssertUtil.notNull("manager", manager);
    _manager = manager;
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
