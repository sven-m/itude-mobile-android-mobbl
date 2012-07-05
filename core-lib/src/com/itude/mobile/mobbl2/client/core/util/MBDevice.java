package com.itude.mobile.mobbl2.client.core.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.itude.mobile.mobbl2.client.core.controller.MBApplicationController;

/**
 * @author Coen Houtman
 * 
 * The class provides methods for other classes to check what kind of device the application is running on.
 */
public final class MBDevice
{

  private static MBDevice              _instance;

  private static final String          DEVICE_NORMAL                = "No";
  private static final String          DEVICE_BIG                   = "Yes";

  private String                       _osVersion                   = null;
  private TwinResult<Integer, Integer> _screenSize                  = null;
  private String                       _screenDensityClassification = null;
  private TwinResult<Float, Float>     _screenDensity               = null;
  private String                       _screenType                  = null;
  private String                       _deviceModel                 = null;

  private static String                _isBigDevice                 = null;

  private MBDevice()
  {
  }

  public static MBDevice getInstance()
  {
    if (_instance == null)
    {
      synchronized (MBDevice.class)
      {
        if (_instance == null)
        {
          _instance = new MBDevice();
        }
      }
    }

    return _instance;
  }

  public String getDeviceType()
  {
    if (isPhone())
    {
      return "Smartphone";
    }
    else if (isPhoneV14())
    {
      return "Smartphone V14";
    }
    else if (isTablet())
    {
      return "Tablet";
    }
    else
    {
      return "Unknown";
    }
  }

  public String getDeviceModel()
  {
    if (_deviceModel == null)
    {
      _deviceModel = Build.MODEL;
      if (_deviceModel == null)
      {
        _deviceModel = "";
      }
    }

    return _deviceModel;
  }

  public String getOSVersion()
  {
    if (_osVersion == null)
    {
      switch (Build.VERSION.SDK_INT)
      {
        case (Build.VERSION_CODES.BASE) : //$FALL-THROUGH$
        case (Build.VERSION_CODES.BASE_1_1) :
          _osVersion = "Android 1";
          break;
        case (Build.VERSION_CODES.CUPCAKE) :
          _osVersion = "Android 1.5 Cupcake";
          break;
        case (Build.VERSION_CODES.DONUT) :
          _osVersion = "Android 1.6 Donut";
          break;
        case (Build.VERSION_CODES.ECLAIR) : //$FALL-THROUGH$
        case (Build.VERSION_CODES.ECLAIR_MR1) : //$FALL-THROUGH$
        case (Build.VERSION_CODES.ECLAIR_0_1) :
          _osVersion = "Android 2.0/2.1 Eclair";
          break;
        case (Build.VERSION_CODES.FROYO) :
          _osVersion = "Android 2.2 Froyo";
          break;
        case (Build.VERSION_CODES.GINGERBREAD) :
          _osVersion = "Android 2.3 Gingerbread";
          break;
        case (Build.VERSION_CODES.GINGERBREAD_MR1) :
          _osVersion = "Android 2.3.3 Gingerbread";
          break;
        case (Build.VERSION_CODES.HONEYCOMB) : //$FALL-THROUGH$
        case (Build.VERSION_CODES.HONEYCOMB_MR1) : //$FALL-THROUGH$ 
        case (Build.VERSION_CODES.HONEYCOMB_MR2) :
          _osVersion = "Android 3.0 Honeycomb";
          break;
        case (Build.VERSION_CODES.ICE_CREAM_SANDWICH) : //$FALL-THROUGH$
        case (Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) :
          _osVersion = "Android 4.0 ICS";
          break;
        case (Build.VERSION_CODES.JELLY_BEAN) : //$FALL-THROUGH$
          _osVersion = "Android 4.1 JellyBean";
          break;

        default :
          _osVersion = "Unknown";
      }

      _osVersion += " (" + Build.VERSION.RELEASE + ")";
    }

    return _osVersion;
  }

  public TwinResult<Integer, Integer> getScreenSize()
  {
    if (_screenSize == null)
    {
      Display display = ((WindowManager) MBApplicationController.getInstance().getBaseContext().getSystemService(Context.WINDOW_SERVICE))
          .getDefaultDisplay();
      int width = display.getWidth();
      int height = display.getHeight();

      _screenSize = new TwinResult<Integer, Integer>(width, height);
    }

    return _screenSize;
  }

  public String getScreenDensityClassification()
  {
    if (_screenDensityClassification == null)
    {
      DisplayMetrics metrics = new DisplayMetrics();
      ((WindowManager) MBApplicationController.getInstance().getBaseContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
          .getMetrics(metrics);

      switch (metrics.densityDpi)
      {
        case (DisplayMetrics.DENSITY_LOW) :
          _screenDensityClassification = "low";
          break;
        case (DisplayMetrics.DENSITY_MEDIUM) :
          _screenDensityClassification = "medium";
          break;
        case (DisplayMetrics.DENSITY_HIGH) :
          _screenDensityClassification = "high";
          break;
        case (DisplayMetrics.DENSITY_XHIGH) :
          _screenDensityClassification = "xhigh";
          break;
        default :
          _screenDensityClassification = "unknown";
      }
    }

    return _screenDensityClassification;
  }

  public TwinResult<Float, Float> getScreenDensity()
  {
    if (_screenDensity == null)
    {
      DisplayMetrics metrics = new DisplayMetrics();
      ((WindowManager) MBApplicationController.getInstance().getBaseContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
          .getMetrics(metrics);

      _screenDensity = new TwinResult<Float, Float>(metrics.xdpi, metrics.ydpi);
    }

    return _screenDensity;
  }

  public String getScreenType()
  {
    if (_screenType == null)
    {
      int screenType = MBApplicationController.getInstance().getResources().getConfiguration().screenLayout
                       & Configuration.SCREENLAYOUT_SIZE_MASK;

      switch (screenType)
      {
        case (Configuration.SCREENLAYOUT_SIZE_SMALL) :
          _screenType = "small";
          break;
        case (Configuration.SCREENLAYOUT_SIZE_NORMAL) :
          _screenType = "normal";
          break;
        case (Configuration.SCREENLAYOUT_SIZE_LARGE) :
          _screenType = "large";
          break;
        case (Configuration.SCREENLAYOUT_SIZE_XLARGE) :
          _screenType = "xlarge";
          break;
        case (Configuration.SCREENLAYOUT_SIZE_UNDEFINED) :
          _screenType = "unknown";
          break;
        default :
          _screenType = "unknown";
      }
    }

    return _screenType;
  }

  public static String isBigDeviceType()
  {
    if (_isBigDevice == null)
    {
      //Verifies if the Generalized Size of the device is XLARGE to be
      // considered a Tablet
      boolean xlarge = ((MBApplicationController.getInstance().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);

      // If XLarge, checks if the Generalized Density is at least MDPI
      // (160dpi)
      if (xlarge)
      {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) MBApplicationController.getInstance().getBaseContext().getSystemService(Context.WINDOW_SERVICE))
            .getDefaultDisplay().getMetrics(metrics);

        // MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
        // DENSITY_TV=213, DENSITY_XHIGH=320
        if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
            || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM || metrics.densityDpi == DisplayMetrics.DENSITY_TV
            || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH)
        {

          _isBigDevice = DEVICE_BIG;

        }
        else
        {
          _isBigDevice = DEVICE_NORMAL;
        }
      }
      else
      {
        _isBigDevice = DEVICE_NORMAL;

      }
    }
    return _isBigDevice;
  }

  public boolean isPhone()
  {
    return DEVICE_NORMAL.equals(isBigDeviceType()) && (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB);
  }

  public boolean isPhoneV14()
  {
    return DEVICE_NORMAL.equals(isBigDeviceType()) && (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1);
  }

  public static boolean isTablet()
  {
    return DEVICE_BIG.equals(isBigDeviceType());
  }

  @Override
  public String toString()
  {
    StringBuilder result = new StringBuilder();
    result.append(" - Type: " + getDeviceType() + "\n");
    result.append(" - OS version: " + getOSVersion() + "\n");
    result.append(" - Screen type: " + getScreenType() + "\n");
    result.append(" - Screen size: " + getScreenSize()._mainResult + " x " + getScreenSize()._secondResult + "\n");
    result.append(" - Screen density: " + getScreenDensity()._mainResult + " x " + getScreenDensity()._secondResult + "\n");
    result.append(" - Screen classification: " + getScreenDensityClassification() + "\n");
    result.append(" - Has big screen: " + isBigDeviceType());

    return result.toString();
  }

}
