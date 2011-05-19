package com.itude.mobile.mobbl2.client.core.util;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;

public class MBScreenUtilities
{

  public final static int ONE         = convertDimensionPixelsToPixels(1);
  public final static int TWO         = convertDimensionPixelsToPixels(2);
  public final static int THREE       = convertDimensionPixelsToPixels(3);
  public final static int FOUR        = convertDimensionPixelsToPixels(4);
  public final static int FIVE        = convertDimensionPixelsToPixels(5);
  public final static int SIX         = convertDimensionPixelsToPixels(6);
  public final static int SEVEN       = convertDimensionPixelsToPixels(7);
  public final static int NINE        = convertDimensionPixelsToPixels(9);
  public final static int TEN         = convertDimensionPixelsToPixels(10);
  public final static int ELEVEN      = convertDimensionPixelsToPixels(11);
  public final static int TWELVE      = convertDimensionPixelsToPixels(12);
  public final static int THIRTEEN    = convertDimensionPixelsToPixels(13);
  public final static int FOURTEEN    = convertDimensionPixelsToPixels(14);
  public final static int FIFTEEN     = convertDimensionPixelsToPixels(15);
  public final static int SIXTEEN     = convertDimensionPixelsToPixels(16);
  public final static int TWENTY      = convertDimensionPixelsToPixels(20);
  public final static int TWENTYFIVE  = convertDimensionPixelsToPixels(25);
  public final static int FORTY       = convertDimensionPixelsToPixels(40);
  public final static int FIFTY       = convertDimensionPixelsToPixels(50);
  public final static int SEVENTYFIVE = convertDimensionPixelsToPixels(75);
  public final static int HUNDRED     = convertDimensionPixelsToPixels(100);

  public final static int convertDimensionPixelsToPixels(float dimensionPixels)
  {
    final float scale = MBApplicationController.getInstance().getBaseContext().getResources().getDisplayMetrics().density;

    return (int) (dimensionPixels * (scale) + 0.5f);
  }

  public final static int getHeightPixelsForPercentage(float percentage)
  {
    final float height = MBApplicationController.getInstance().getBaseContext().getResources().getDisplayMetrics().heightPixels;

    return (int) ((height / 100) * percentage);
  }

}
