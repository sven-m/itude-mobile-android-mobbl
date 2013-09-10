package com.itude.mobile.mobbl2.client.core.controller;

import java.util.LinkedList;
import java.util.Queue;

public class MBOutcomeTaskManager
{

  private final Queue<MBOutcomeTask> _tasks;
  private final MBOutcome            _outcome;

  public MBOutcomeTaskManager(MBOutcome outcome)
  {
    _outcome = outcome;
    _tasks = new LinkedList<MBOutcomeTask>();
  }

  public void run()
  {
    MBOutcomeTask task = _tasks.poll();
    if (task != null) task.start();
  }

  public void addTask(MBOutcomeTask task)
  {
    _tasks.add(task);
  }

  public MBOutcome getOutcome()
  {
    return _outcome;
  }

  public void finished(MBOutcomeTask task)
  {
    run();
  }
}
