package com.itude.mobile.mobbl2.client.core.view.builders.datatypes;

import com.itude.mobile.android.util.StringUtil;

public class PriceWithThreeDecimalsFormatter extends MBBaseDataTypeFormatter
{

  @Override
  protected String actuallyFormat(String value)
  {
    return StringUtil.formatPriceWithThreeDecimals(getLocale(), value);
  }

}
