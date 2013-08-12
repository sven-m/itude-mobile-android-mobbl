package com.itude.mobile.mobbl2.client.core.util;

import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;

public final class MBStringUtil
{
  private static NumberKeyListener _currencyNumberKeyListener;

  private MBStringUtil()
  {

  }

  public static Spanned fromHTML(String textToTransform)
  {
    return Html.fromHtml(textToTransform);
  }

  public static NumberKeyListener getCurrencyNumberKeyListener()
  {
    if (_currencyNumberKeyListener == null)
    {
      _currencyNumberKeyListener = new NumberKeyListener()
      {

        @Override
        public int getInputType()
        {
          return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        }

        @Override
        protected char[] getAcceptedChars()
        {
          return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ','};
        }
      };
    }

    return _currencyNumberKeyListener;
  }
}
