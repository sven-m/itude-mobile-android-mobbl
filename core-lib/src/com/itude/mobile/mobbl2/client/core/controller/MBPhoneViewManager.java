package com.itude.mobile.mobbl2.client.core.controller;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SearchView;

import com.itude.mobile.mobbl2.client.core.util.Constants;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

@TargetApi(11)
public class MBPhoneViewManager extends MBNextGenViewManager
{

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

      setSearchImage(image, searchLayout);

      LinearLayout searchPlate = (LinearLayout) searchLayout.getChildAt(1);
      MBViewBuilderFactory.getInstance().getStyleHandler().styleSearchPlate(searchPlate);

      LinearLayout submitArea = (LinearLayout) searchLayout.getChildAt(2);
      MBViewBuilderFactory.getInstance().getStyleHandler().styleSearchSubmitArea(submitArea);
    }
    catch (Exception e)
    {
      Log.e(Constants.APPLICATION_NAME, "error changing searchbutton icon", e);
    }
  }

  // End of Android hooks

  @Override
  public void setContentView(int id)
  {
    setContentView(getLayoutInflater().inflate(id, null));
  }

  @Override
  public void setContentView(View v)
  {
    setContentView(v, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
  }
}
