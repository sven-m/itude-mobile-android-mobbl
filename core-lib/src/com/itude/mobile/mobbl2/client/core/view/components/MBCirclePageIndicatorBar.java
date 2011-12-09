package com.itude.mobile.mobbl2.client.core.view.components;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;

public class MBCirclePageIndicatorBar extends MBAbstractPageIndicator
{

  private final int DEFAULT_CIRCLE_SIZE = MBScreenUtilities.SIX;
  private int       _inactiveIndicatorColour;
  private int       _activeIndicatorColour;

  public MBCirclePageIndicatorBar(Context context)
  {
    super(context);
  }

  @Override
  protected ViewGroup setupIndicatorContainer()
  {
    LinearLayout circleContainer = new LinearLayout(this.getContext());
    circleContainer.setOrientation(LinearLayout.HORIZONTAL);
    circleContainer.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    removeAllViews();

    return circleContainer;
  }

  @Override
  public void setActiveIndicator(int activeIndicator)
  {
    ImageView currentIndicator = (ImageView) getIndicatorList().get(getActiveIndicatorIndex());
    ((ShapeDrawable) currentIndicator.getDrawable()).getPaint().setColor(_inactiveIndicatorColour);
    currentIndicator.invalidate();
    setActiveIndicatorIndex(activeIndicator);
    currentIndicator = (ImageView) getIndicatorList().get(activeIndicator);
    ((ShapeDrawable) currentIndicator.getDrawable()).getPaint().setColor(_activeIndicatorColour);
    currentIndicator.invalidate();
  }

  @Override
  protected View setupActiveIndicatorView()
  {
    ShapeDrawable circle = new ShapeDrawable(new OvalShape());
    circle.setBounds(0, 0, DEFAULT_CIRCLE_SIZE, DEFAULT_CIRCLE_SIZE);
    circle.setIntrinsicHeight(DEFAULT_CIRCLE_SIZE);
    circle.setIntrinsicWidth(DEFAULT_CIRCLE_SIZE);
    circle.getPaint().setAntiAlias(true);
    circle.getPaint().setColor(_activeIndicatorColour);

    ImageView circleView = new ImageView(this.getContext());
    circleView.setPadding(MBScreenUtilities.FIVE, 0, MBScreenUtilities.FIVE, 0);
    circleView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    circleView.setImageDrawable(circle);

    return circleView;
  }

  @Override
  protected View setupInactiveIndicatorView()
  {
    ShapeDrawable circle = new ShapeDrawable(new OvalShape());
    circle.setBounds(0, 0, DEFAULT_CIRCLE_SIZE, DEFAULT_CIRCLE_SIZE);
    circle.setIntrinsicHeight(DEFAULT_CIRCLE_SIZE);
    circle.setIntrinsicWidth(DEFAULT_CIRCLE_SIZE);
    circle.getPaint().setAntiAlias(true);
    circle.getPaint().setColor(_inactiveIndicatorColour);

    ImageView circleView = new ImageView(this.getContext());
    circleView.setPadding(MBScreenUtilities.FIVE, 0, MBScreenUtilities.FIVE, 0);
    circleView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    circleView.setImageDrawable(circle);

    return circleView;
  }

  public void setActiveIndicatorColor(int colour)
  {
    _activeIndicatorColour = colour;
    resetPageIndicator();
  }

  public void setInactiveIndicatorColor(int colour)
  {
    _inactiveIndicatorColour = colour;
    resetPageIndicator();
  }
}
