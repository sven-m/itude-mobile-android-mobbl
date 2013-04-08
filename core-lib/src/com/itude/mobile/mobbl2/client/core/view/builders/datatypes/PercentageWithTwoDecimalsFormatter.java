package com.itude.mobile.mobbl2.client.core.view.builders.datatypes;

import com.itude.mobile.android.util.StringUtil;

public class PercentageWithTwoDecimalsFormatter extends MBBaseDataTypeFormatter
{

  @Override
  protected String actuallyFormat(String value)
  {
    return StringUtil.formatPercentageWithTwoDecimals(getLocale(), value);
  }

}
