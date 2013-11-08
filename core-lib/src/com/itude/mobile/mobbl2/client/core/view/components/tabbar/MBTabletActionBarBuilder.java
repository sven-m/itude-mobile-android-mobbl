package com.itude.mobile.mobbl2.client.core.view.components.tabbar;

import java.lang.reflect.Field;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MBTabletActionBarBuilder extends MBDefaultActionBarBuilder
{

  public MBTabletActionBarBuilder(Context context)
  {
    super(context);
  }

  @Override
  protected void changeSearchImage(Drawable image, SearchView searchView)
  {
    if (image == null)
    {
      return;
    }

    try
    {
      // change the iconified icon
      Field searchButtonField = searchView.getClass().getDeclaredField("mSearchButton");
      searchButtonField.setAccessible(true);
      ImageView searchButton = (ImageView) searchButtonField.get(searchView);
      searchButton.setImageDrawable(image);

      // change the searchview
      Field searchEditField = searchView.getClass().getDeclaredField("mSearchEditFrame");
      searchEditField.setAccessible(true);
      LinearLayout searchLayout = (LinearLayout) searchEditField.get(searchView);

      LinearLayout searchPlate = (LinearLayout) searchLayout.getChildAt(0);
      MBViewBuilderFactory.getInstance().getStyleHandler().styleSearchPlate(searchPlate);

      // find first image view, assuming this is the icon we need
      setSearchImage(image, searchPlate);

      LinearLayout submitArea = (LinearLayout) searchLayout.getChildAt(1);
      MBViewBuilderFactory.getInstance().getStyleHandler().styleSearchSubmitArea(submitArea);
    }
    catch (Exception e)
    {
      Log.e(Constants.APPLICATION_NAME, "error changing searchbutton icon", e);
    }

  }

}
