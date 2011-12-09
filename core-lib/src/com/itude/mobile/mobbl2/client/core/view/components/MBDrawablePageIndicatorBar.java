package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.view.builders.MBStyleHandler;
import com.itude.mobile.mobbl2.client.core.view.builders.MBViewBuilderFactory;

public class MBDrawablePageIndicatorBar extends MBAbstractPageIndicator
{

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
    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    ImageView activeIndicatorView = new ImageView(getContext());
    activeIndicatorView.setBackgroundDrawable(styleHandler.getDrawablePageIndicatorDrawable());

    styleHandler.styleDrawablePageIndicatorBarActiveIndicatorView(activeIndicatorView);

    activeIndicatorView.setSelected(true);

    return activeIndicatorView;
  }

  @Override
  protected View setupInactiveIndicatorView()
  {
    MBStyleHandler styleHandler = MBViewBuilderFactory.getInstance().getStyleHandler();

    ImageView inactiveIndicatorView = new ImageView(getContext());
    inactiveIndicatorView.setBackgroundDrawable(styleHandler.getDrawablePageIndicatorDrawable());

    styleHandler.styleDrawablePageIndicatorBarActiveIndicatorView(inactiveIndicatorView);

    inactiveIndicatorView.setSelected(false);

    return inactiveIndicatorView;
  }

  @Override
  public void setActiveIndicator(int activatingIndicator)
  {

    ImageView currentIndicator = (ImageView) getIndicatorList().get(getActiveIndicatorIndex());
    currentIndicator.setSelected(false);

    setActiveIndicatorIndex(activatingIndicator);

    currentIndicator = (ImageView) getIndicatorList().get(activatingIndicator);
    currentIndicator.setSelected(true);

    getIndicatorContainer().invalidate();

  }
}
