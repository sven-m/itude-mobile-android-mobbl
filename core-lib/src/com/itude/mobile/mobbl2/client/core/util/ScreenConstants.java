/*
 * (C) Copyright ItudeMobile.
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
package com.itude.mobile.mobbl2.client.core.util;

import android.content.Context;

import com.itude.mobile.android.util.ScreenUtil;
import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;

public final class ScreenConstants
{
  private final static Context context               = MBApplicationController.getInstance().getBaseContext();

  public final static int      ONE                   = ScreenUtil.convertDimensionPixelsToPixels(context, 1);
  public final static int      TWO                   = ScreenUtil.convertDimensionPixelsToPixels(context, 2);
  public final static int      THREE                 = ScreenUtil.convertDimensionPixelsToPixels(context, 3);
  public final static int      FOUR                  = ScreenUtil.convertDimensionPixelsToPixels(context, 4);
  public final static int      FIVE                  = ScreenUtil.convertDimensionPixelsToPixels(context, 5);
  public final static int      SIX                   = ScreenUtil.convertDimensionPixelsToPixels(context, 6);
  public final static int      SEVEN                 = ScreenUtil.convertDimensionPixelsToPixels(context, 7);
  public final static int      NINE                  = ScreenUtil.convertDimensionPixelsToPixels(context, 9);
  public final static int      TEN                   = ScreenUtil.convertDimensionPixelsToPixels(context, 10);
  public final static int      ELEVEN                = ScreenUtil.convertDimensionPixelsToPixels(context, 11);
  public final static int      TWELVE                = ScreenUtil.convertDimensionPixelsToPixels(context, 12);
  public final static int      THIRTEEN              = ScreenUtil.convertDimensionPixelsToPixels(context, 13);
  public final static int      FOURTEEN              = ScreenUtil.convertDimensionPixelsToPixels(context, 14);
  public final static int      FIFTEEN               = ScreenUtil.convertDimensionPixelsToPixels(context, 15);
  public final static int      SIXTEEN               = ScreenUtil.convertDimensionPixelsToPixels(context, 16);
  public final static int      TWENTY                = ScreenUtil.convertDimensionPixelsToPixels(context, 20);
  public final static int      JACKBAUER             = ScreenUtil.convertDimensionPixelsToPixels(context, 24);
  public final static int      TWENTYFIVE            = ScreenUtil.convertDimensionPixelsToPixels(context, 25);
  public final static int      TWENTYEIGHT           = ScreenUtil.convertDimensionPixelsToPixels(context, 28);
  public final static int      THIRTYFIVE            = ScreenUtil.convertDimensionPixelsToPixels(context, 35);
  public final static int      FORTY                 = ScreenUtil.convertDimensionPixelsToPixels(context, 40);
  public final static int      FORTYSIX              = ScreenUtil.convertDimensionPixelsToPixels(context, 46);
  public final static int      FIFTY                 = ScreenUtil.convertDimensionPixelsToPixels(context, 50);
  public final static int      SEVENTY               = ScreenUtil.convertDimensionPixelsToPixels(context, 70);
  public final static int      SEVENTYFIVE           = ScreenUtil.convertDimensionPixelsToPixels(context, 75);
  public final static int      HUNDRED               = ScreenUtil.convertDimensionPixelsToPixels(context, 100);
  public final static int      HUNDREDANDTEN         = ScreenUtil.convertDimensionPixelsToPixels(context, 110);
  public final static int      HUNDREDANDFIFTY       = ScreenUtil.convertDimensionPixelsToPixels(context, 150);
  public final static int      HUNDREDANDSEVENTYFIVE = ScreenUtil.convertDimensionPixelsToPixels(context, 175);
  public final static int      TWOHUNDRED            = ScreenUtil.convertDimensionPixelsToPixels(context, 200);

}
