package com.itude.mobile.mobbl2.client.core.view.components;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class MBAbstractPageIndicator extends LinearLayout
{

  private List<View>            _indicators;
  private int                   _indicatorCount;
  private final ViewGroup       _indicatorContainer;
  private int                   _activeIndicator;
  private MBSlidableViewFlipper _viewFlipper;

  public MBAbstractPageIndicator(Context context)
  {
    super(context);

    _indicatorContainer = setupIndicatorContainer();
    addView(_indicatorContainer);
    _activeIndicator = 0;
  }

  protected abstract ViewGroup setupIndicatorContainer();

  protected abstract View setupActiveIndicatorView();

  protected abstract View setupInactiveIndicatorView();

  public ViewGroup getIndicatorContainer()
  {
    return _indicatorContainer;
  }

  public void setIndicatorCount(int amountOfIndicators)
  {
    _indicatorCount = amountOfIndicators;

    resetPageIndicator();
  }

  public int getIndicatorCount()
  {
    return _indicatorCount;
  }

  public void resetPageIndicator()
  {
    getIndicatorList().clear();
    _indicatorContainer.removeAllViews();
    for (int i = 0; i < _indicatorCount; i++)
    {
      final int index = i;

      if (i == _activeIndicator)
      {
        _indicators.add(setupActiveIndicatorView());
      }
      else
      {
        _indicators.add(setupInactiveIndicatorView());
      }

      View addedIndicator = _indicators.get(i);
      addedIndicator.setOnClickListener(new OnClickListener()
      {

        @Override
        public void onClick(View arg0)
        {
          onIndicatorClick(index);
        }
      });
      _indicatorContainer.addView(addedIndicator);
    }
  }

  public abstract void setActiveIndicator(int activatingIndicator);

  public void setActiveIndicatorIndex(int activeIndicator)
  {
    _activeIndicator = activeIndicator;
  }

  public int getActiveIndicatorIndex()
  {
    return _activeIndicator;
  }

  public View getIndicatorAtIndex(int index)
  {
    if (getIndicatorList().size() < index)
    {
      return getIndicatorList().get(index);
    }

    return null;
  }

  public List<View> getIndicatorList()
  {
    if (_indicators == null)
    {
      _indicators = new ArrayList<View>();
    }

    return _indicators;
  }

  public void attachViewFlipper(MBSlidableViewFlipper viewFlipper)
  {
    _viewFlipper = viewFlipper;
  }

  public MBSlidableViewFlipper getAttachedViewFlipper()
  {
    return _viewFlipper;
  }

  public abstract void onIndicatorClick(int indicatorIndex);

}
