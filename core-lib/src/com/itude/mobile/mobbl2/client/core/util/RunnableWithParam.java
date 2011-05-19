package com.itude.mobile.mobbl2.client.core.util;

/**
 * This class allows you to create a Runnable with a parameter.
 * @author Gert
 *
 * @param <T> the type of parameter
 */
public abstract class RunnableWithParam <T> implements Runnable
{
  private T _param = null;
  public RunnableWithParam(T p_param)
  {
    _param = p_param;
  }
  
  protected T getParam()
  {
    return _param;
  }
  public abstract void run();
}