package com.itude.mobile.mobbl2.client.core.view.components;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itude.mobile.mobbl2.client.core.util.MBScreenUtilities;

public class MBPageIndicatorBar extends LinearLayout
{

  private int                       amountOfIndicators;
  private int                       activeIndicator;
  private int                       activeIndicatorColor;
  private int                       inactiveIndicatorColor;
  private final int                 defaultCircleSize = MBScreenUtilities.SIX;

  private final List<ImageView>     circleImages;
  private final List<ShapeDrawable> circles;

  public MBPageIndicatorBar(Context context)
  {
    super(context);
    setGravity(Gravity.CENTER);
    circles = new ArrayList<ShapeDrawable>();
    circleImages = new ArrayList<ImageView>();
    activeIndicator = 0;
  }

  public MBPageIndicatorBar(Context context, int amountOfIndicators)
  {
    this(context);
    setIndicatorCount(amountOfIndicators);
  }

  public void setIndicatorCount(int amountOfIndicators)
  {
    LinearLayout circleContainer = new LinearLayout(this.getContext());
    circleContainer.setOrientation(LinearLayout.HORIZONTAL);
    circleContainer.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    removeAllViews();
    addView(circleContainer);

    this.amountOfIndicators = amountOfIndicators;
    circleContainer.removeAllViews();
    circles.clear();
    circleImages.clear();
    for (int i = 0; i < this.amountOfIndicators; i++)
    {
      ShapeDrawable circle = new ShapeDrawable(new OvalShape());
      circle.setBounds(0, 0, defaultCircleSize, defaultCircleSize);
      circle.setIntrinsicHeight(defaultCircleSize);
      circle.setIntrinsicWidth(defaultCircleSize);
      circle.getPaint().setAntiAlias(true);
      circle.getPaint().setColor(inactiveIndicatorColor);

      circles.add(circle);

      ImageView circleView = new ImageView(this.getContext());
      circleView.setPadding(MBScreenUtilities.FIVE, 0, MBScreenUtilities.FIVE, 0);
      circleView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      circleView.setImageDrawable(circle);
      circleImages.add(circleView);

      circleContainer.addView(circleView);
    }
  }

  public void updateIndicatorCount(int newIndicatorCount)
  {
  }

  public void setActiveIndicator(int activeIndicator)
  {
    circles.get(this.activeIndicator).getPaint().setColor(inactiveIndicatorColor);
    circleImages.get(this.activeIndicator).invalidate();
    this.activeIndicator = activeIndicator;
    circles.get(this.activeIndicator).getPaint().setColor(activeIndicatorColor);
    circleImages.get(this.activeIndicator).invalidate();
  }

  public int getActiveIndicator()
  {
    return activeIndicator;
  }

  public int getActiveIndicatorColor()
  {
    return activeIndicatorColor;
  }

  public void setActiveIndicatorColor(int activeIndicatorColor)
  {
    this.activeIndicatorColor = activeIndicatorColor;

    // Also update circles
    for (int i = 0; i < circles.size(); i++)
    {

      if (i == activeIndicator)
      {
        ShapeDrawable circle = circles.get(i);
        circle.getPaint().setColor(activeIndicatorColor);
        break;
      }

    }
  }

  public int getInactiveIndicatorColor()
  {
    return inactiveIndicatorColor;
  }

  public void setInactiveIndicatorColor(int inactiveIndicatorColor)
  {
    this.inactiveIndicatorColor = inactiveIndicatorColor;

    // Also update circles
    for (int i = 0; i < circles.size(); i++)
    {

      if (i != activeIndicator)
      {
        ShapeDrawable circle = circles.get(i);
        circle.getPaint().setColor(inactiveIndicatorColor);
      }

    }
  }

}
