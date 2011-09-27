package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBDrawablePageIndicatorBar extends MBAbstractPageIndicator
{

  private Drawable _inactiveDrawable;
  private Drawable _activeDrawable;

  public MBDrawablePageIndicatorBar(Context context)
  {
    super(context);
  }

  @Override
  protected ViewGroup setupIndicatorContainer()
  {
    LinearLayout container = new LinearLayout(this.getContext());
    container.setOrientation(LinearLayout.HORIZONTAL);
    container.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    removeAllViews();

    return container;
  }

  @Override
  protected View setupActiveIndicatorView()
  {
    ImageView activeIndicatorView = new ImageView(getContext());
    activeIndicatorView.setBackgroundDrawable(_activeDrawable);

    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();
    styleHandler.styleDrawablePageIndicatorBarActiveIndicatorView(activeIndicatorView);

    return activeIndicatorView;
  }

  @Override
  protected View setupInactiveIndicatorView()
  {
    ImageView inactiveIndicatorView = new ImageView(getContext());
    inactiveIndicatorView.setBackgroundDrawable(_inactiveDrawable);

    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();
    styleHandler.styleDrawablePageIndicatorBarActiveIndicatorView(inactiveIndicatorView);

    return inactiveIndicatorView;
  }

  @Override
  public void setActiveIndicator(int activatingIndicator)
  {
    Log.d("ali", "Before activatingIndicator");

    ImageView currentIndicator = (ImageView) getIndicatorList().get(getActiveIndicatorIndex());
    currentIndicator.setBackgroundDrawable(_inactiveDrawable);

    setActiveIndicatorIndex(activatingIndicator);

    currentIndicator = (ImageView) getIndicatorList().get(activatingIndicator);
    currentIndicator.setBackgroundDrawable(_activeDrawable);
    Log.d("ali", "After activatingIndicator before invalidating");

    getIndicatorContainer().invalidate();

    Log.d("ali", "After activatingIndicator after invalidating");
  }

  public void setActiveIndicatorDrawable(Drawable activeDrawable)
  {
    _activeDrawable = activeDrawable;
    resetPageIndicator();
  }

  public void setInactiveIndicatorDrawable(Drawable inactiveDrawable)
  {
    _inactiveDrawable = inactiveDrawable;
    resetPageIndicator();
  }

}
