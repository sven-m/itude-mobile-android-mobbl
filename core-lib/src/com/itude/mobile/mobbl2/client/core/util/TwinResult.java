package com.itude.mobile.mobbl2.client.core.util;

/**
 * Utility template class for when you want to return 2 values.
 * @author Gert
 *
 * @param <T1> the main result type, often boolean to indicate success.
 * @param <T2> the secondary result type, typically only valid when the boolean is true
 * 
 * An example can be found in DataUtil.java
 */
public class TwinResult<T1, T2>
{
  public T1 _mainResult;
  public T2 _secondResult;
  
  
  public TwinResult(T1 p_mainResult, T2 p_secondResult)
  {
    _mainResult = p_mainResult;
    _secondResult = p_secondResult;
  }

}
