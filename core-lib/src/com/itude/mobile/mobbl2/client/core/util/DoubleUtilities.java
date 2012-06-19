package com.itude.mobile.mobbl2.client.core.util;

import java.text.DecimalFormat;

public final class DoubleUtilities
{

  private DoubleUtilities()
  {
  }

  /**
   * @param value Value that needs to be rounded
   * @param digits number of digits that needs to be rounded to.
   * @return double, with supplied <i>digits</i>
   */
  public static double round(double value, int digits)
  {
    DecimalFormat formatter = new DecimalFormat();
    formatter.setMaximumFractionDigits(digits);
    formatter.setMinimumFractionDigits(digits);
    formatter.setGroupingUsed(false);
    return Double.valueOf(formatter.format(value));
  }

}