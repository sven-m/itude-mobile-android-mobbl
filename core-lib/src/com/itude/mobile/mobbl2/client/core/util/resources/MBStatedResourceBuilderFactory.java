package com.itude.mobile.mobbl2.client.core.util.resources;

import com.itude.mobile.android.util.StringUtil;
import com.itude.mobile.mobbl2.client.core.configuration.resources.MBResourceConfiguration;

public class MBStatedResourceBuilderFactory
{
  private static MBStatedResourceBuilderFactory _instance = null;

  private MBStatedResourceBuilderFactory()
  {
  }

  public static MBStatedResourceBuilderFactory getInstance()
  {
    if (_instance == null)
    {
      _instance = new MBStatedResourceBuilderFactory();
    }

    return _instance;
  }

  public MBAbstractStatedResourceBuilder getStatedResourceBuilder(String builder, MBResourceConfiguration config)
  {
    if (StringUtil.isBlank(builder))
    {
      return new MBStatedResourceBuilder(config);
    }
    else if ("RadioGroup".equals(builder))
    {
      return new MBRadioGroupStatedResourceBuilder(config);
    }
    else if ("Color".equals(builder))
    {
      return new MBColorStatedResourceBuilder(config);
    }

    return null;
  }
}
