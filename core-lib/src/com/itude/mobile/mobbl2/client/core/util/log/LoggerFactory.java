package com.itude.mobile.mobbl2.client.core.util.log;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.util.MBProperties;

/**
 *
 */
public final class LoggerFactory
{
  
  public static final int LOGLEVEL = Integer.parseInt(MBProperties.getInstance().getValueForProperty(Constants.C_PROPERTY_LOGLEVEL));

  private LoggerFactory()
  {
  }

  public static Logger getInstance(String tag)
  {
    return new MOBBLLogger(tag);
  }

}
