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
package com.itude.mobile.mobbl2.client.core.util.log;

import android.util.Log;

/**
 * The logger implementation for MOBBL.
 *
 */
public class MOBBLLogger implements Logger
{

  private final String _tag;

  public MOBBLLogger(String tag)
  {
    _tag = tag;
  }

  @Override
  public void debug(String message)
  {
    Log.d(_tag, message);
  }

  @Override
  public void debug(String message, Throwable throwable)
  {
    Log.d(_tag, message, throwable);
  }

  @Override
  public void info(String message)
  {
    Log.i(_tag, message);
  }

  @Override
  public void warn(String message)
  {
    Log.w(_tag, message);
  }

  @Override
  public void warn(String message, Throwable throwable)
  {
    Log.w(_tag, message, throwable);
  }

  @Override
  public void warn(StringBuffer message, Throwable throwable)
  {
    warn(message.toString(), throwable);
  }

  @Override
  public void error(String message)
  {
    Log.e(_tag, message);
  }

  @Override
  public void error(String message, Throwable throwable)
  {
    Log.e(_tag, message, throwable);
  }

  @Override
  public void error(StringBuffer message, Throwable throwable)
  {
    error(message.toString(), throwable);
  }

  @Override
  public void fatal(String message)
  {
    error(message);
  }

  @Override
  public void fatal(String message, Throwable throwable)
  {
    error(message, throwable);
  }

  @Override
  public boolean isDebugEnabled()
  {
    return LoggerFactory.LOGLEVEL <= Log.DEBUG;
  }

  @Override
  public boolean isInfoEnabled()
  {
    return LoggerFactory.LOGLEVEL <= Log.INFO;
  }

  @Override
  public boolean isWarnEnabled()
  {
    return LoggerFactory.LOGLEVEL <= Log.WARN;
  }

  @Override
  public boolean isErrorEnabled()
  {
    return LoggerFactory.LOGLEVEL <= Log.ERROR;
  }

  @Override
  public boolean isFatalEnabled()
  {
    return LoggerFactory.LOGLEVEL <= Log.ERROR;
  }

}
