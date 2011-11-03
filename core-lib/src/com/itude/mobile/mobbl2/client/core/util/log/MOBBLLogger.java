package com.itude.mobile.mobbl2.client.core.util.log;

import android.util.Log;

/**
 * The logger implementation for MOBBL.
 *
 */
public class MOBBLLogger implements Logger
{

  private String _tag;

  public MOBBLLogger(String tag)
  {
    _tag = tag;
  }

  public void debug(String message)
  {
    Log.d(_tag, message);
  }

  public void debug(String message, Throwable throwable)
  {
    Log.d(_tag, message, throwable);
  }

  public void info(String message)
  {
    Log.i(_tag, message);
  }

  public void warn(String message)
  {
    Log.w(_tag, message);
  }

  public void warn(String message, Throwable throwable)
  {
    Log.w(_tag, message, throwable);
  }

  public void warn(StringBuffer message, Throwable throwable)
  {
    warn(message.toString(), throwable);
  }

  public void error(String message)
  {
    Log.e(_tag, message);
  }

  public void error(String message, Throwable throwable)
  {
    Log.e(_tag, message, throwable);
  }

  public void error(StringBuffer message, Throwable throwable)
  {
    error(message.toString(), throwable);
  }

  public void fatal(String message)
  {
    error(message);
  }

  public void fatal(String message, Throwable throwable)
  {
    error(message, throwable);
  }

  public boolean isDebugEnabled()
  {
    return LoggerFactory.LOGLEVEL <= Log.DEBUG;
  }

  public boolean isInfoEnabled()
  {
    return LoggerFactory.LOGLEVEL <= Log.INFO;
  }

  public boolean isWarnEnabled()
  {
    return LoggerFactory.LOGLEVEL <= Log.WARN;
  }

  public boolean isErrorEnabled()
  {
    return LoggerFactory.LOGLEVEL <= Log.ERROR;
  }

  public boolean isFatalEnabled()
  {
    return LoggerFactory.LOGLEVEL <= Log.ERROR;
  }

}
